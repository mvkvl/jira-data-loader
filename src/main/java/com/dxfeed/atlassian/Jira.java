package com.dxfeed.atlassian;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.dxfeed.config.AppConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Jira {

    private static final int READ_PAGE_SIZE = 100;

    private final @NonNull AppConfig appConfig;

    JiraRestClient jiraRestClient;

    @PostConstruct
    private void prepare() {
        System.out.println(appConfig.toString());
        jiraRestClient = new AsynchronousJiraRestClientFactory()
            .createWithBasicHttpAuthentication(
                URI.create(appConfig.getUrl()),
                appConfig.getUser(),
                appConfig.getPass()
            );
    }

    @SneakyThrows
    public Collection<Issue> load(String query, int limit, int offset) {
        Collection<Issue> result = new ArrayList<>();
        int start = offset;
        while(start < limit) {

            int readSize = Math.min(READ_PAGE_SIZE, limit - result.size());

            Iterable<Issue> issues = jiraRestClient
                    .getSearchClient()
                    .searchJql(query, readSize, start, new HashSet<>(Arrays.asList("*all")))
                    .get()
                    .getIssues();

            if (!issues.iterator().hasNext()) {
//                System.err.println("no data. break.");
                break;
            }

            result.addAll((Collection<? extends Issue>) issues);
            System.out.println("read : " + result.size());
            start += readSize;
//            System.out.println("start: " + start);
        }
        return result;
    }
    public Collection<Issue> load(String query) {
        return load(query, Integer.MAX_VALUE, 0);
    }
    public Collection<Issue> load(String query, int limit) {
        return load(query, limit, 0);
    }

    @SneakyThrows
    public void load(String query, int limit, int offset, Consumer<Collection<Issue>> consumer) {
        int start = offset;
        while(start < limit + offset) {
            int readSize = Math.min(READ_PAGE_SIZE, limit + offset - start);
            Iterable<Issue> issues = jiraRestClient
                    .getSearchClient()
                    .searchJql(query, readSize, start, new HashSet<>(Arrays.asList("*all")))
                    .get()
                    .getIssues();
            if (!issues.iterator().hasNext())
                break;
            consumer.accept((Collection<Issue>) issues);
            start += readSize;
        }
    }
    public void load(String query, Consumer<Collection<Issue>> consumer) {
        load(query, Integer.MAX_VALUE, 0, consumer);
    }
    public void load(String query, int limit, Consumer<Collection<Issue>> consumer) {
        load(query, limit, 0, consumer);
    }

    @SneakyThrows
    public void close() {
        jiraRestClient.close();
    }

}
