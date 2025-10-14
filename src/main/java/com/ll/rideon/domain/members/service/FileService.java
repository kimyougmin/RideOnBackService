package com.ll.rideon.domain.members.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.profile:/uploads/profile}")
    private String profileUploadPath;

    /**
     * 프로필 이미지 파일 업로드
     */
    public String uploadProfileImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        
        if (!isValidImageExtension(fileExtension)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (지원: jpg, jpeg, png, gif)");
        }

        // 업로드 디렉토리 생성
        createUploadDirectory(profileUploadPath);

        // 고유한 파일명 생성
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = Paths.get(profileUploadPath, fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath);
        
        log.info("프로필 이미지 업로드 성공: {}", fileName);
        
        // 파일 URL 반환 (실제 환경에 맞게 수정 필요)
        return "/uploads/profile/" + fileName;
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 유효한 이미지 확장자 검증
     */
    private boolean isValidImageExtension(String extension) {
        return extension.matches("(jpg|jpeg|png|gif)");
    }

    /**
     * 업로드 디렉토리 생성
     */
    private void createUploadDirectory(String path) throws IOException {
        Path uploadDir = Paths.get(path);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("파일 삭제 성공: {}", filePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }
}
