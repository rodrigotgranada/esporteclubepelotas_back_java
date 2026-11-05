package com.esporteclubepelotas.api.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(byte[] bytes, String fileName, String folder, String contentType);
    void deleteFile(String fileUrl);

    default String uploadFile(MultipartFile file, String path) {
        int idx = path.lastIndexOf('/');
        String folder = idx > 0 ? path.substring(0, idx) : "uploads";
        String fileName = idx > 0 ? path.substring(idx + 1) : path;
        return uploadFile(file, folder, fileName);
    }

    default String uploadFile(MultipartFile file, String folder, String fileName) {
        try {
            String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
            return uploadFile(file.getBytes(), fileName, folder, contentType);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler bytes do arquivo para upload", e);
        }
    }
}
