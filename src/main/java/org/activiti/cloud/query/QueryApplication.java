package org.activiti.cloud.query;

import org.activiti.cloud.starter.query.configuration.EnableActivitiQuery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableActivitiQuery
@EnableScheduling
@SpringBootApplication
public class QueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryApplication.class, args);
	}

}
