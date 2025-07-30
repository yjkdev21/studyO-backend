package com.ex.tjspring.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

    @Value("${aws.s3.bucket:upload-bucket-study}")
    private String bucket;

    @Value("${aws.s3.region:ap-northeast-2}")
    private String region;

    // S3 버킷 내 저장될 디렉토리 경로 (선택 사항, 기본값: uploads/)
    // 디렉토리 경로가 확실히 슬래시로 끝나도록 보장
    @Value("${aws.s3.directory:uploads/}")
    private String dir;

    private S3Client s3Client;

    public S3Service(@Value("${aws.s3.region:ap-northeast-2}") String region) {
        this.region = region;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /** MultipartFile 업로드 (이미지 등) */
    // 반환값을 S3 객체 키 (예: uploads/dirKey/unique-filename.jpg)로 변경
    public String upload( S3DirKey dirKey , MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.warn("Uploaded file is empty.");
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        String key = dir + dirKey.getDirKeyName() + "/" + uniqueFileName; // S3에 저장될 객체 키 ( upload/dirkey/391739f-aefe...jpg )

        log.info("S3 Upload Details: bucket={}, region={}, key={}, contentType={}",
                bucket, region, key, file.getContentType());

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));

            // S3 객체 키만 반환하도록 변경
            return uniqueFileName;
        } catch (S3Exception e) {
            log.error("S3 upload failed for key: {}. Error: {}", key, e.getMessage());
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("File processing failed for upload. Error: {}", e.getMessage());
            throw e;
        }
    }

    /** 로컬 Path(File) 업로드 */
    // 반환값을 S3 객체 키로 변경
    public String upload( S3DirKey dirKey , Path path) throws IOException {
        if (!Files.exists(path)) {
            log.warn("Local file does not exist: {}", path.toString());
            return null;
        }

        String uniqueFileName = UUID.randomUUID().toString() + "-" + path.getFileName().toString();
        String key = dir + dirKey.getDirKeyName() + "/" + uniqueFileName;

        log.info("S3 Path Upload Details: bucket={}, region={}, key={}, contentType={}",
                bucket, region, key, Files.probeContentType(path));

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(Files.probeContentType(path))
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(req, RequestBody.fromFile(path));
            // S3 객체 키만 반환하도록 변경
            return uniqueFileName;
        } catch (S3Exception e) {
            log.error("S3 path upload failed for key: {}. Error: {}", key, e.getMessage());
            throw new IOException("Failed to upload file from path to S3: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("File processing failed for path upload. Error: {}", e.getMessage());
            throw e;
        }
    }

    /** S3 객체 다운로드 */
    public byte[] downloadFile( S3DirKey dirKey , String storedFileName) throws IOException {
        String key = dir + dirKey.getDirKeyName() + "/" + storedFileName;
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            log.error("S3 download failed for key: {}. Error: {}", storedFileName, e.getMessage());
            throw new IOException("Failed to download file from S3: " + e.getMessage(), e);
        }
    }

    /** S3 객체 삭제 */
    public void delete( S3DirKey dirKey , String storedFileName) {

        if (storedFileName == null || storedFileName.trim().isEmpty()) {
            log.warn("Attempted to delete with empty or null S3 key.");
            return;
        }

        String key = dir + dirKey.getDirKeyName() + "/" + storedFileName;

        log.info("delete key = {}", key);

        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key) // s3ObjectKey를 직접 사용
                .build();

        try {
            DeleteObjectResponse response = s3Client.deleteObject(delReq);

            if (response.deleteMarker() != null && response.deleteMarker()) {
                log.info("Object marked for deletion (versioning enabled): {}", storedFileName);
            } else {
                log.info("Object deleted successfully or no delete marker created: {}", storedFileName);
            }

        } catch (S3Exception e) {
            log.error("S3 delete failed for key: {}. Error: {}", storedFileName, e.getMessage());
            throw e;
        }
    }

    public String getFileFullPath(S3DirKey dirKey , String storedFileName) {
        String s3Host = "https://upload-bucket-study.s3.ap-northeast-2.amazonaws.com/";
        return s3Host + dir + dirKey.getDirKeyName() + "/" + storedFileName;
    }



}
