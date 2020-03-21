package com.dxfeed.atlassian;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.dxfeed.config.AppConfig;
import com.dxfeed.tools.FluentJson;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

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

        // https://community.atlassian.com/t5/Answers-Developer-Questions/Getting-SocketTimeOutException-while-fetching-custom-field/qaq-p/519455#309492
        jiraRestClient = new CustomAsynchronousJiraRestClientFactory()
            .createWithBasicHttpAuthenticationCustom(
//        jiraRestClient = new AsynchronousJiraRestClientFactory()
//            .createWithBasicHttpAuthentication(
                URI.create(appConfig.getUrl()),
                appConfig.getUser(),
                appConfig.getPass(),
                60
            );
    }

    // https://stackoverflow.com/a/43972064
//    private void setClientTimeout(JiraRestClient client, int milliseconds) {
//        try {
//            Field f1 = Class.forName("com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient").getDeclaredField("httpClient");
//            Field f2 = Class.forName("com.atlassian.jira.rest.client.internal.async.AtlassianHttpClientDecorator").getDeclaredField("httpClient");
//            Field f3 = Class.forName("com.atlassian.httpclient.apache.httpcomponents.ApacheAsyncHttpClient").getDeclaredField("httpClient");
//            Field f4 = Class.forName("org.apache.http.impl.client.cache.CachingHttpAsyncClient").getDeclaredField("backend");
//            Field f5 = Class.forName("org.apache.http.impl.nio.client.InternalHttpAsyncClient").getDeclaredField("defaultConfig");
//            Field f6 = Class.forName("org.apache.http.client.config.RequestConfig").getDeclaredField("socketTimeout");
//            f1.setAccessible(true);
//            f2.setAccessible(true);
//            f3.setAccessible(true);
//            f4.setAccessible(true);
//            f5.setAccessible(true);
//            f6.setAccessible(true);
//            Object requestConfig = f5.get(f4.get(f3.get(f2.get(f1.get(client)))));
//            f6.setInt(requestConfig, milliseconds);
//            f1.setAccessible(false);
//            f2.setAccessible(false);
//            f3.setAccessible(false);
//            f4.setAccessible(false);
//            f5.setAccessible(false);
//            f6.setAccessible(false);
//        } catch (Exception ignore) {
//            ignore.printStackTrace();
//        }
//    }

    @SneakyThrows
    public void test() {
        StreamSupport.stream(
            jiraRestClient
                .getSearchClient()
                .searchJql("")
                .get()
                .getIssues()
                .spliterator(), false)
        .forEach(issue -> issue.getWorklogs());

    }

    // load to collection
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

    // load to consumer
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
