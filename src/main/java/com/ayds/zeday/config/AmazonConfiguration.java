package com.ayds.zeday.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ayds.zeday.property.AmazonProperties;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonConfiguration {

    @Bean
    S3Client s3Client(@Value("${aws.s3.region}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .forcePathStyle(true)
                .build();
    }

    @Bean
    AmazonProperties amazonProperties(@Value("${aws.s3.bucketName}") String bucketName) {
        return new AmazonProperties(bucketName);
    }
}
