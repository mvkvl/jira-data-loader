package com.dxfeed.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "jdl")
public class AppConfig {

    private String url;
    private String user;
    private String pass;

    @Value("${loader.projects:}")
    private List<String> projects;

    private boolean all   = false;

    @Value("${loader.since:}")
    private String  dateStr;
    private int     limit = 0;
    private int     skip  = 0;

    private boolean clean;
    private boolean encrypt;

    @PostConstruct
    private void init() {
        if (null == projects)
            projects = new ArrayList<>();
    }

    public String toString() {
        return new StringBuilder()
            .append("url     : ").append(url).append("\n")
            .append("user    : ").append(user).append("\n")
            .append("pass    : ").append("*********").append("\n")
            .append("projects: ").append(projects).append("\n")
            .append("all     : ").append(all).append("\n")
            .append("dateStr : ").append(dateStr).append("\n")
            .append("limit   : ").append(limit).append("\n")
            .append("skip    : ").append(skip).append("\n")
            .append("clean   : ").append(clean).append("\n")
            .append("encrypt : ").append(encrypt).append("\n")
            .toString()
        ;
    }
}
