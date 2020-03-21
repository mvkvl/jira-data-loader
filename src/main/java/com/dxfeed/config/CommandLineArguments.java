package com.dxfeed.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class CommandLineArguments {

    @Autowired
    public CommandLineArguments(ApplicationArguments args, AppConfig appConfig) {
        if (args.containsOption("url")) {
            appConfig.setUrl(args.getOptionValues("url").get(0));
        }
        if (args.containsOption("user")) {
            appConfig.setUser(args.getOptionValues("user").get(0));
        }
        if (args.containsOption("pass")) {
            appConfig.setPass(args.getOptionValues("pass").get(0));
        }
        if (args.containsOption("projects")) {
            appConfig.getProjects().addAll(Arrays.asList(args.getOptionValues("projects").get(0).split(",")));
        }
        if (args.containsOption("since")) {
            appConfig.setDateStr(args.getOptionValues("since").get(0));
        }
        if (args.containsOption("limit")) {
            appConfig.setLimit(Integer.valueOf(args.getOptionValues("limit").get(0)));
        }
        if (args.containsOption("skip")) {
            appConfig.setSkip(Integer.valueOf(args.getOptionValues("skip").get(0)));
        }
        if (args.containsOption("clean")) {
            appConfig.setClean(true);
        }
        if (args.containsOption("all") || StringUtils.isEmpty(appConfig.getDateStr()) && appConfig.getLimit() <= 0) {
            appConfig.setAll(true);
        }
    }

}
