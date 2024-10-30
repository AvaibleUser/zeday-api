package com.ayds.zeday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ayds.zeday.property.AmazonProperties;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonConfig {

    @Bean
    S3Client s3Client(AmazonProperties amazonProperties) {
        return S3Client.builder()
                .region(Region.of(amazonProperties.region()))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(amazonProperties.accessKey(), amazonProperties.secretKey())))
                .forcePathStyle(true)
                .build();
    }
}
