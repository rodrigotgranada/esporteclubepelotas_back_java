package com.esporteclubepelotas.api.common.storage;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utilitário transversal que garante o padrão arquitetural de persistência de mídias:
 * 1. Nenhuma URL temporária do navegador (blob:) jamais é salva no banco de dados.
 * 2. Todo upload para o provedor de Storage valida presença de arquivo e retorno de URL pública válida
 *    antes de permitir o commit transacional.
 */
public final class MediaUploadSanitizer {

    private MediaUploadSanitizer() {
    }

    /**
     * Sanitiza uma URL opcional:
     * - null -> null
     * - vazio ou iniciando com "blob:" -> null
     * - caso contrário -> string trimada
     */
    public static String sanitizeNullableUrl(String url) {
        if (url == null) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("blob:")) {
            return null;
        }
        return trimmed;
    }

    /**
     * Sanitiza uma URL não-nula (ex: coverImage = ""):
     * - null, vazio ou iniciando com "blob:" -> ""
     * - caso contrário -> string trimada
     */
    public static String sanitizeUrlOrEmpty(String url) {
        String clean = sanitizeNullableUrl(url);
        return clean != null ? clean : "";
    }

    /**
     * Verifica se o DTO enviou intenção explícita de limpar uma URL existente (string em branco)
     * ignorando URLs temporárias "blob:".
     */
    public static boolean isBlobUrl(String url) {
        return url != null && url.trim().startsWith("blob:");
    }

    /**
     * Remove itens nulos, vazios ou URLs temporárias "blob:" de uma lista de URLs.
     */
    public static List<String> sanitizeUrlList(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String u : urls) {
            String clean = sanitizeNullableUrl(u);
            if (clean != null) {
                result.add(clean);
            }
        }
        return result;
    }

    public static String sanitizeFolderId(String folderId) {
        if (folderId == null || folderId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        String sanitized = folderId.trim().replaceAll("[^a-zA-Z0-9_-]", "");
        return sanitized.isEmpty() ? UUID.randomUUID().toString() : sanitized;
    }

    public static String extractSafeExtension(String filename, String fallbackExt) {
        if (filename == null || !filename.contains(".")) {
            return fallbackExt;
        }
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase().replaceAll("[^a-z0-9]", "");
        return ext.isEmpty() ? fallbackExt : ext;
    }

    public static String uploadAndVerify(
            MultipartFile file,
            String path,
            StorageService storageService,
            String errorMessage
    ) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de mídia válido foi enviado.");
        }
        try {
            String url = storageService.uploadFile(file, path);
            if (url == null || url.isBlank() || url.startsWith("blob:")) {
                throw new BusinessException(errorMessage);
            }
            return url;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(errorMessage + " Detalhe: " + ex.getMessage());
        }
    }

    public static String uploadAndVerify(
            MultipartFile file,
            String folder,
            String fileName,
            StorageService storageService,
            String errorMessage
    ) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de mídia válido foi enviado.");
        }
        try {
            String url = storageService.uploadFile(file, folder, fileName);
            if (url == null || url.isBlank() || url.startsWith("blob:")) {
                throw new BusinessException(errorMessage);
            }
            return url;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(errorMessage + " Detalhe: " + ex.getMessage());
        }
    }
}

