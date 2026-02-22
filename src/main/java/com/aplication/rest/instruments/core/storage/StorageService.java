package com.aplication.rest.instruments.core.storage;

import com.aplication.rest.instruments.core.exceptions.ValidationException;
import io.minio.PutObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;
    // Minio configuration
    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.url}")
    private String minioUrl;
    //Allowed content types (allow list)
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp"
    );

    public String uploadImage(MultipartFile file) {
        // Validate file
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ValidationException("Unsupported format. Only JPG, PNG, and WEBP are supported.");
        }

        try {
            //Generate a unique name for the file to avoid collisions (e.g: "123e4567...-image.png")
            //Replace all spaces in the original filename with hyphens for URL compatibility -
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "-");
            String uniqueFileName = UUID.randomUUID() + "-" + originalFilename;

            //upload to MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName) // file name in the bucket
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            // Return the public URL of the uploaded file
            // Format: http://localhost:9000/instruments/123-image.png
            return minioUrl + "/" + bucketName + "/" + uniqueFileName;

        } catch (Exception e) {
            // Transform any error (network, minio down) into a server error
            throw new RuntimeException("Error uploading image to storage server", e);
        }
    }
}
