package com.codeplatform.code_executor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private static final String TEMP_FOLDER = "C:\\temp";

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucketName}")
    private String bucketName;

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    private S3Client getS3Client() {
        if (s3Client == null) {
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }
        return s3Client;
    }

    private S3Presigner getS3Presigner() {
        if (s3Presigner == null) {
            s3Presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }
        return s3Presigner;
    }

    public String downloadFile(String fileName) throws IOException {
        logger.info("Downloading file from S3: bucket={}, key={}", bucketName, fileName);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            Path tempPath = Paths.get(TEMP_FOLDER, fileName);
            
            // Ensure temp folder exists
            Files.createDirectories(Paths.get(TEMP_FOLDER));

            // Delete existing file if it exists
            if (Files.exists(tempPath)) {
                Files.delete(tempPath);
                logger.info("Deleted existing file: {}", tempPath);
            }

            getS3Client().getObject(getObjectRequest, tempPath);

            logger.info("File downloaded successfully: {}", tempPath);
            return tempPath.toString();

        } catch (S3Exception e) {
            logger.error("S3 error downloading file: {}", e.getMessage());
            if (e.statusCode() == 404) {
                throw new IOException("File not found in S3: " + fileName, e);
            }
            throw new IOException("Failed to download file from S3: " + e.getMessage(), e);
        }
    }

    public String generateUploadUrl(String fileName) {
        logger.info("Generating presigned upload URL for: {}", fileName);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(objectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                getS3Presigner().presignPutObject(presignRequest);

        String url = presignedRequest.url().toString();
        logger.info("Generated presigned URL successfully");
        return url;
    }

    public String handleCodeSubmission(String fileName, String language) {
        String uploadUrl = generateUploadUrl(fileName);
        return uploadUrl;
    }

    public String uploadFile(String filePath, String userId, String fileName) throws IOException {
        logger.info("Uploading file to S3: userId={}, fileName={}", userId, fileName);

        try {
            // Generate unique S3 key: userId_timestamp_fileName
            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            String s3Key = userId + "_" + timestamp + "_" + fileName;

            Path path = Paths.get(filePath);
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            getS3Client().putObject(putObjectRequest, path);

            logger.info("File uploaded successfully to S3: {}", s3Key);
            return s3Key;

        } catch (S3Exception e) {
            logger.error("S3 error uploading file: {}", e.getMessage());
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }
}