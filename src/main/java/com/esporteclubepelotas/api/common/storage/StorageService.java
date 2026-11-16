package com.esporteclubepelotas.api.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(byte[] bytes, String fileName, String folder, String contentType);
    void deleteFile(String fileUrl);

    default String uploadFile(MultipartFile file, String path) {
        int idx = path != null ? path.lastIndexOf('/') : -1;
        String folder = idx > 0 ? path.substring(0, idx) : (path != null && !path.isBlank() ? path : "uploads");
        String fileName = idx > 0 ? path.substring(idx + 1) : (file != null && file.getOriginalFilename() != null ? file.getOriginalFilename() : "media");
        return uploadFile(file, folder, fileName);
    }

    default String uploadFile(MultipartFile file, String folder, String fileName) {
        MediaUploadSanitizer.MediaKind kind = (folder != null && folder.contains("transparency"))
                || (fileName != null && fileName.toLowerCase().endsWith(".pdf"))
                ? MediaUploadSanitizer.MediaKind.IMAGE_OR_PDF
                : (folder != null && folder.contains("audio"))
                ? MediaUploadSanitizer.MediaKind.AUDIO
                : MediaUploadSanitizer.MediaKind.IMAGE;

        MediaUploadSanitizer.ValidatedMedia validated = MediaUploadSanitizer.validateFile(file, kind);
        String baseName = (fileName != null && !fileName.isBlank()) ? fileName : "media";
        int dotIdx = baseName.lastIndexOf('.');
        String safeFileName = (dotIdx > 0 ? baseName.substring(0, dotIdx) : baseName) + "." + validated.extension();
        return uploadFile(validated.bytes(), safeFileName, folder, validated.contentType());
    }
}
