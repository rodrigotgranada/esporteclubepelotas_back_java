package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class NewsMediaUploadHelper {

    private NewsMediaUploadHelper() {
    }

    public static String uploadCoverFile(MultipartFile file, String rawFolderId, StorageService storageService) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de capa enviado.");
        }
        String folderId = sanitizeFolderId(rawFolderId);
        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = "cover-" + System.currentTimeMillis() + "." + ext;
        String folder = "news/" + folderId;

        String url = storageService.uploadFile(file, folder, fileName);
        if (url == null || url.isBlank()) {
            throw new BusinessException("Falha ao enviar a imagem de capa para o servidor de arquivos.");
        }
        return url;
    }

    public static List<String> uploadGalleryFiles(List<MultipartFile> files, String rawFolderId, StorageService storageService) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de galeria enviado.");
        }
        String folderId = sanitizeFolderId(rawFolderId);
        String folder = "news/" + folderId + "/gallery";
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String ext = getFileExtension(file.getOriginalFilename());
            String fileName = "photo-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 100000) + "." + ext;
            String url = storageService.uploadFile(file, folder, fileName);
            if (url == null || url.isBlank()) {
                throw new BusinessException("Falha ao enviar uma das fotos da galeria.");
            }
            urls.add(url);
        }

        if (urls.isEmpty()) {
            throw new BusinessException("Nenhuma foto válida foi enviada para a galeria.");
        }
        return urls;
    }

    public static Map<String, String> uploadContentImage(MultipartFile file, StorageService storageService) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }
        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = "content-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 100000) + "." + ext;
        String folder = "news/content";

        String url = storageService.uploadFile(file, folder, fileName);
        if (url == null || url.isBlank()) {
            throw new BusinessException("Falha ao enviar imagem do conteúdo.");
        }
        return Map.of("url", url);
    }

    private static String sanitizeFolderId(String rawFolderId) {
        if (rawFolderId == null || rawFolderId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        String cleaned = rawFolderId.trim().replaceAll("[^a-zA-Z0-9_-]", "");
        return cleaned.isEmpty() ? UUID.randomUUID().toString() : cleaned;
    }

    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}

