package com.rurocker.aws.lambdacloudwatch;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class JobEventTriggerApplication {

    @Value("${aws.config.access-key-id}")
    private String accessKeyId;

    @Value("${aws.config.access-secret-key}")
    private String accessSecretKey;

    public static void main(String[] args) {
        SpringApplication.run(JobEventTriggerApplication.class, args);
    }

    @Bean
    public AmazonCloudWatch createCloudWatch() {
        return AmazonCloudWatchClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKeyId, accessSecretKey)))
                .build();
    }

    @Bean
    public AWSSimpleSystemsManagement ssmClient() {
        return AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKeyId, accessSecretKey)))
                .build();
    }

    @Bean
    public ReentrantLock reentrantLock() {
        return new ReentrantLock();
    }

    // enable async
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
