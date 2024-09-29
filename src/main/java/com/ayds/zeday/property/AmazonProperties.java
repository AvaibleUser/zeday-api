package com.ayds.zeday.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.s3")
public record AmazonProperties(
        String region,
        String bucketName) {
}
