package com.dxfeed.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoCommandRoute extends RouteBuilder {

    @Value("${mongodb.db}") String db;

    @Override
    public void configure() {

        onException()
                .log(LoggingLevel.WARN, log.getName(), "${exception.getMessage()}")
                .handled(true);

        from("direct:mongoCommand")
            .log(LoggingLevel.INFO, log.getName(), "COMMAND REQUEST: ${body}")
            .to("mongodb:mongoClient?database=" + db + "&collection=issues&operation=command")
            .log(LoggingLevel.INFO, log.getName(), "COMMAND RESULT: ${body}")
        ;

    }
}
