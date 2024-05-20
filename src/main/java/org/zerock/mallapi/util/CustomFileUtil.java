package org.zerock.mallapi.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File tempFolder = new File(uploadPath);

        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }

        uploadPath = tempFolder.getAbsolutePath();

        log.info("-------------------------------");
        log.info(uploadPath);
    }

    // 업로드시 문자열 목록 반환
    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {

        if (files == null || files.size() == 0) {
            return null;// List.of();
        }

        List<String> uploadFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String savedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(uploadPath, savedName);
            try {
                Files.copy(file.getInputStream(), savePath); // 원본파일업로드

                // 이미지파일일 경우 s를 붙인 썸네일 파일을 만든다
                if (Objects.requireNonNull(file.getContentType()).startsWith("image")) {
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                    Thumbnails.of(savePath.toFile()).size(200, 200).toFile(thumbnailPath.toFile());
                }

                uploadFileNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return uploadFileNames;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "defalut.jpeg");
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.size() == 0) {
            return;
        }

        fileNames.forEach(fileName -> {
            // 썸네일 삭제
            String thumbnailFileName = "s_" + fileName;

            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath, fileName);

            try {
                Files.deleteIfExists(thumbnailPath);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
