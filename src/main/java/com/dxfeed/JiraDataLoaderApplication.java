package com.dxfeed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.dxfeed.config.AppConfig;

@Slf4j
@Configuration
@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JiraDataLoaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(JiraDataLoaderApplication.class, args);
	}

}
