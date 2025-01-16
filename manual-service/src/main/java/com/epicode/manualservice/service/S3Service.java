package com.epicode.manualservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName = "epicode"; // S3 버킷 이름

    public String uploadFile(String key, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, Paths.get(file.getAbsolutePath()));

        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
    }
}
