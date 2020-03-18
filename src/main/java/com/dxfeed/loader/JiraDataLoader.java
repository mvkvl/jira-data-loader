package com.dxfeed.loader;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.dxfeed.atlassian.Jira;
import com.dxfeed.config.AppConfig;
import com.dxfeed.tools.FluentJson;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JiraDataLoader {

    private final @NonNull Jira jira;
    private final @NonNull AppConfig appConfig;

    public void load() {
        load(null);
    }
    public void load(int limit) {
        load(null, limit);
    }
    public void load(int limit, int offset) {
        load(null, limit, offset);
    }
    public void load(String dateStr) {
        load(dateStr, Integer.MAX_VALUE, 0);
    }
    public void load(String dateStr, int limit) {
        load(dateStr, limit, 0);
    }
    public void load(String dateStr, int limit, int offset) {
        String query
            = (StringUtils.isNotBlank(dateStr))
            ? "project in (" + appConfig.getProjects().stream().collect(Collectors.joining(",")) + ") AND Updated > " + dateStr
            : "project in (" + appConfig.getProjects().stream().collect(Collectors.joining(",")) + ")"
        ;
        jira.load(query, limit, offset, this::processIssues);
        jira.close();
    }

    AtomicInteger counter = new AtomicInteger(0);
    private void processIssues(Collection<Issue> issues) {
        issues.stream().forEach(
//            this::printIssueHeader
            this::printIssueDetail
        );
    }

    @SneakyThrows
    private void printIssueDetail(Issue issue) {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        System.out.println(new FluentJson(om.writeValueAsString(issue)).toPrettyString(2));
    }
    private void printIssueHeader(Issue issue) {
        System.out.println(
            String.format("%6d.  %-10s  %s", counter.addAndGet(1), issue.getKey(), issue.getSummary())
        );
    }

}
