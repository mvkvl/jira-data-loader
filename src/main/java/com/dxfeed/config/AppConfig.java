package com.dxfeed.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "a2c")
public class AppConfig {

    private String url;
    private String user;
    private String pass;
    private List<String> projects = new ArrayList<>();

    private boolean all   = false;
    private String  dateStr;
    private int     limit = 0;
    private int     skip  = 0;

    private boolean clean;

    public String toString() {
        return new StringBuilder()
            .append("url     : ").append(url).append("\n")
            .append("user    : ").append(user).append("\n")
            .append("pass    : ").append(pass).append("\n")
            .append("projects: ").append(projects).append("\n")
            .append("all     : ").append(all).append("\n")
            .append("dateStr : ").append(dateStr).append("\n")
            .append("limit   : ").append(limit).append("\n")
            .append("skip    : ").append(skip).append("\n")
            .append("clean   : ").append(clean).append("\n")
            .toString()
        ;
    }
}
