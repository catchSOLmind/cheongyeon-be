package com.catchsolmind.cheongyeonbe.domain.user.service;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private static final String BUCKET = "cheongyeon-fe-solmind";
    private static final String REGION = "ap-northeast-2";

    private final UserRepository userRepository;
    private final S3Client s3Client;

    public String upload(User user, MultipartFile image) {
        validate(image);

        String extension = getExtension(image.getOriginalFilename());
        String key = "profile/user-" + user.getUserId() + "/" + UUID.randomUUID() + "." + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(key)
                    .contentType(image.getContentType())
                    // .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(image.getBytes())
            );

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 실패", e);
        }

        String imageUrl = "https://" + BUCKET + ".s3." + REGION + ".amazonaws.com/" + key;

        user.setProfileImg(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    private void validate(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }

        if (!image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        if (image.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("이미지 용량은 5MB 이하여야 합니다.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "png";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
