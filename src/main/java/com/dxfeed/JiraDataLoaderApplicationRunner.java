package com.dxfeed;

import com.dxfeed.loader.JiraDataLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import com.dxfeed.config.AppConfig;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DependsOn({"commandLineArguments"})
public class JiraDataLoaderApplicationRunner implements CommandLineRunner, ApplicationContextAware {

    private final @NonNull AppConfig appConfig;
    private final @NonNull JiraDataLoader jiraDataLoader;

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void run(String... args) {

        int exitCode = 0;

        if (!checkConfiguration()) {
            printUsage();
            exitCode = 1;
        } else {

            if (appConfig.isClean()) {
                // cleanup database
                System.out.println("cleaning up database...");
            }

            if (appConfig.isAll()) {
                jiraDataLoader.load();
            } else if (StringUtils.isBlank(appConfig.getDateStr()) && appConfig.getLimit() > 0) {
                jiraDataLoader.load(appConfig.getLimit(), appConfig.getSkip());
            } else if (StringUtils.isNotBlank(appConfig.getDateStr()) && appConfig.getLimit() > 0) {
                jiraDataLoader.load(appConfig.getDateStr(), appConfig.getLimit(), appConfig.getSkip());
            } else if (StringUtils.isNotBlank(appConfig.getDateStr())) {
                jiraDataLoader.load(appConfig.getDateStr());
            }
        }

        // jiraDataLoader.load("2020-03-16");
        // jiraDataLoader.load("-7d", 5);


        // close up
        applicationContext.close();
        System.exit(exitCode);
    }

    private boolean checkConfiguration() {
        return
         !((StringUtils.isBlank(appConfig.getUrl()) || StringUtils.isBlank(appConfig.getUser()) || StringUtils.isBlank(appConfig.getPass()) || appConfig.getProjects().isEmpty())
         ||(!appConfig.isClean() && !appConfig.isAll() && appConfig.getLimit() <= 0 && StringUtils.isBlank(appConfig.getDateStr()))
         );
    }

    public void printUsage() {
        System.out.println("");
        System.out.println("Usage: ");
        System.out.println("");
        System.out.println("  java -jar jira-data-loader.jar --user=<login>             \\\n" +
                           "                                 --pass=<password>          \\\n" +
                           "                                 --url=<jira url>           \\\n" +
                           "                                 --projects=<projects list> \\\n" +
                           "                                [--clean]                   \\\n" +
                           "                                {--all | [--since=<date string>] [--limit=<N> [--skip=<M>]]}");
        System.out.println("");
        System.out.println("\t--url\t\tJira server URL");
        System.out.println("\t--user\t\tJira login");
        System.out.println("\t--pass\t\tJira user password");
        System.out.println("\t--projects\tList of Jira projects to get data from");
        System.out.println("");
        System.out.println("\t--clean\t\tClean database before load");
        System.out.println("\t--all\t\tLoad all issues from Jira");
        System.out.println("\t--since\t\tLoad all issues updated after given date*");
        System.out.println("\t--limit\t\tLoad latest N issues only");
        System.out.println("\t--skip\t\tSkip latest M issues");
        System.out.println("");
        System.out.println("\t NOTE 1: date string in --since argument can be of a form \"yyyy-mm-dd\", \"-5d\", \"-1w\"");
        System.out.println("");
        System.out.println("\t NOTE 2: --all argument has precedence over --limit argument\n" +
                           "\t         (i.e. if --all is passed all the issues will be loaded even if\n" +
                           "\t          --limit is also passed)");
        System.out.println("");
    }

}
