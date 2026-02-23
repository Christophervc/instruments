package com.aplication.rest.instruments.core.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.url}")
    private String url;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket-name}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public CommandLineRunner initMinio(MinioClient minioClient) {
        return args -> {
            try {
                boolean bExists = minioClient.bucketExists(
                        BucketExistsArgs.builder().bucket(bucketName).build());

                if (!bExists) {

                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                    System.out.println("Bucket '" + bucketName + "' created successfully.");

                    String policy = """
                        {
                          "Statement": [
                            {
                              "Action": "s3:GetObject",
                              "Effect": "Allow",
                              "Principal": "*",
                              "Resource": "arn:aws:s3:::%s/*"
                            }
                          ],
                          "Version": "2012-10-17"
                        }
                        """.formatted(bucketName);

                    minioClient.setBucketPolicy(
                            SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
                    System.out.println("Public policies applied to bucket '" + bucketName + "'.");
                }
            } catch (Exception e) {
                System.err.println("Initialization error MinIO: " + e.getMessage());
            }
        };
    }
}
