package com.ayds.zeday.service.util;

import static software.amazon.awssdk.core.sync.RequestBody.fromInputStream;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.zeday.domain.dto.FileDto;
import com.ayds.zeday.domain.exception.BadRequestException;
import com.ayds.zeday.property.AmazonProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;
    private final AmazonProperties amazonProperties;

    public String store(String filename, InputStream inputStream, String contentType, long contentLength) {
        s3Client.putObject(
                builder -> builder.bucket(amazonProperties.bucketName())
                        .key(filename)
                        .contentType(contentType)
                        .contentLength(contentLength),
                fromInputStream(inputStream, contentLength));

        return loadUrl(filename);
    }

    public String store(String filename, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("The file must have any content.");
        }

        try {
            return store(filename, file.getInputStream(), file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String store(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return store(filename, file);
    }

    private ResponseInputStream<GetObjectResponse> loadResponse(String filename) {
        return s3Client.getObject(
                builder -> builder.bucket(amazonProperties.bucketName())
                        .key(filename));
    }

    public FileDto load(String filename) throws IOException {
        ResponseInputStream<GetObjectResponse> response = loadResponse(filename);

        return new FileDto(response.readAllBytes(), response.response().contentType());
    }

    public String loadUrl(String filename) {
        return s3Client.utilities()
                .getUrl(
                        builder -> builder.bucket(amazonProperties.bucketName())
                                .key(filename))
                .toExternalForm();
    }

    public void delete(String filename) {
        s3Client.deleteObject(
                builder -> builder.bucket(amazonProperties.bucketName())
                        .key(filename));
    }
}
