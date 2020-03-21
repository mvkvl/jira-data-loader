package com.dxfeed.route;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class IssueProcessingRoute extends RouteBuilder {

    @Value("${mongodb.db}") String db;

    @Bean(name = "mongoClient")
    private MongoClient getClient(
        @Value("${mongodb.host}") String host,
        @Value("${mongodb.port}") int port,
        @Value("${mongodb.user}") String user,
        @Value("${mongodb.pass}") String pass
    ) {
        // Set credentials
        MongoCredential credential =
            MongoCredential.createCredential(user, db, pass.toCharArray());

        ServerAddress serverAddress = new ServerAddress(host, port);

        MongoClientOptions options =
            new MongoClientOptions.Builder().connectTimeout(5000).build();

        // Mongo Client
        return new MongoClient(Arrays.asList(serverAddress), credential, options);
    }

    @Override
    public void configure() {

        onException()
            .log(LoggingLevel.WARN, log.getName(), "${exception.getMessage()}")
            .handled(true);

        from("direct:saveIssue")
            .setBody(simple("${headers.Issue}"))
            .to("mongodb:mongoClient?database=" + db + "&collection=issues&operation=save")
            .log(LoggingLevel.INFO, log.getName(), "PROCESSING RESULT    : ${body}")
        ;
    }
}
