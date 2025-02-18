package com.api.glovoCRM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableRetry
@EnableAsync
@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "com.api.glovoCRM.DAOs")
//@EnableRedisRepositories(basePackages = "com.api.glovoCRM.DAOs.Redis")
@EntityScan(basePackages = "com.api.glovoCRM.Models")
public class GlovoCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlovoCrmApplication.class, args);
	}

}
