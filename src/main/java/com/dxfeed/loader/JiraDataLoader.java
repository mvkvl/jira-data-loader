package com.dxfeed.loader;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.dxfeed.atlassian.Jira;
import com.dxfeed.config.AppConfig;
import com.dxfeed.tools.FileTools;
import com.dxfeed.tools.FluentJson;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.FluentProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JiraDataLoader {

    private final @NonNull Jira jira;
    private final @NonNull AppConfig appConfig;
    private final @NonNull CamelContext camelContext;

    private FluentJson issueTemplate;
    private FluentProducerTemplate producerTemplate;

    @PostConstruct
    private void loadIssueTemplate() {
        String contents = FileTools.readFileFromResources("issue.template.json");
        issueTemplate   = new FluentJson(contents);
        producerTemplate = camelContext.createFluentProducerTemplate();
    }

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


    public void cleanup() {
        // drop 'issues' collection from MongoDB
        DBObject commandBody = new BasicDBObject("drop", "issues");
        producerTemplate
            .withBody( commandBody )
            .to("direct:mongoCommand")
            .send()
        ;
    }

    AtomicInteger counter = new AtomicInteger(0);
    private void processIssues(Collection<Issue> issues) {
        issues.stream().map(this::issueToJson).forEach(
            this::processIssue
//            this::printIssueHeader
//            this::printIssueDetail
        );
    }
    private void processIssue(FluentJson issueJson) {

        printIssueHeader(issueJson);

        issueJson.set("_id", issueJson.get("id").get());

        // save issue to MongoDb (or replace if already exists)
        producerTemplate
            .withHeader("Issue", issueJson.toString())
            .withHeader("Query", "{\"_id\": " + issueJson.get("id").get() + "}")
            .to("direct:saveIssue")
            .send()
        ;
    }

    @SneakyThrows
    private void printIssueDetail(Issue issue) {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        System.out.println(new FluentJson(om.writeValueAsString(issue)).toPrettyString(2));
    }
    private void printIssueHeader(Issue issue) {
        System.out.println(
                String.format("%6d.  %10d %-10s  %s", counter.addAndGet(1), issue.getId(), issue.getKey(), issue.getSummary())
        );
    }
    private void printIssueHeader(FluentJson issueJson) {
        System.out.println(
            String.format("%6d.  %10d %-10s  %s",
                counter.addAndGet(1),
                issueJson.get("id").get(),
                issueJson.get("key").get(),
                issueJson.get("summary").get())
        );
    }

    @SneakyThrows
    private FluentJson issueToJson(Issue issue) {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        FluentJson issueJson = new FluentJson(om.writeValueAsString(issue));
        FluentJson result = new FluentJson();
        fillInJsonTemplate(result, issueTemplate, issueJson);
        return result;
    }

    /**
     * - Fills in empty FluentJson object with data from issueJson according to templateJson
     * - Template is located at resources folder
     * - Only fields found in issueJson are copied from template to result
     *
     * OUT @param result
     * IN @param template
     * IN @param issue
     */
    private void fillInJsonTemplate(FluentJson result, FluentJson template, FluentJson issue) {
        template.keys().forEach(key -> {
            if (null != template && !template.isEmpty() && null != template.get(key)) {

                // object value is string
                if (template.get(key).get() instanceof String) {
                    result.set(key, issue.get(key));
                }
                // object value is number
                else if (template.get(key).get() instanceof Number) {
                    result.set(key, issue.get(key));
                }
                // object value is object
                else if (template.get(key).get() instanceof JSONObject) {
                    if (null != issue.get(key)) {
                        // log.info("fill in JSON object {}", key);
                        result.set(key, new FluentJson());
                        fillInJsonTemplate(result.get(key), template.get(key), issue.get(key));
                    } else {
                        log.trace("skipping null object {}", key);
                        result.set(key, new FluentJson());
                    }
                }
                // object value is array
                else if (template.get(key).get() instanceof JSONArray) {
                    result.set(key, new JSONArray());
                    if (template.get(key).isEmpty()) {
                        log.trace("skipping empty array: {}", key);
                    } else{
                        if (null != issue.get(key) && !issue.get(key).isEmpty()) {
                            if (key.toLowerCase().contains("field")) {
                                result.get(key).add(issue.get(key));
                            } else {
                                issue.get(key).stream().forEach(item -> {
                                    FluentJson fj = new FluentJson();
                                    result.get(key).add(fj);
                                    fillInJsonTemplate(fj, template.get(key).get(0), item);
                                });
                            }
                        }
                    }
                }
            }
        });
    }


//    public void test() {
//        FluentJson issue = new FluentJson(FileTools.readFile("sample.issue.json"));
//        FluentJson result = new FluentJson();
//        fillInJsonTemplate(result, issueTemplate, issue);
//        System.out.print(result.toPrettyString(2));
//    }

}
