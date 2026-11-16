package com.esporteclubepelotas.api.common.storage;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Utilitário transversal que garante o padrão arquitetural e de segurança de mídias:
 * 1. Nenhuma URL temporária do navegador (blob:) jamais é salva no banco de dados.
 * 2. Todo upload valida os Magic Bytes reais do arquivo no servidor (nunca confia no Content-Type do cliente),
 *    bloqueando SVG, HTML, scripts e executáveis.
 */
public final class MediaUploadSanitizer {

    public enum MediaKind {
        IMAGE,
        IMAGE_OR_PDF,
        AUDIO
    }

    public record ValidatedMedia(byte[] bytes, String extension, String contentType) {}

    private static final Set<String> AUDIO_EXTS = Set.of("mp3", "wav", "ogg", "m4a");
    private static final Set<String> PDF_EXTS = Set.of("pdf");

    private MediaUploadSanitizer() {
    }

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

    public static String sanitizeUrlOrEmpty(String url) {
        String clean = sanitizeNullableUrl(url);
        return clean != null ? clean : "";
    }

    public static boolean isBlobUrl(String url) {
        return url != null && url.trim().startsWith("blob:");
    }

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
        String ext = filename.substring(filename.lastIndexOf(".") + 1)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
        return ext.isEmpty() ? fallbackExt : ext;
    }

    public static ValidatedMedia validateFile(MultipartFile file, MediaKind kind) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de mídia válido foi enviado.");
        }
        try {
            return validateBytes(file.getBytes(), kind);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Falha ao ler os bytes do arquivo enviado.");
        }
    }

    public static ValidatedMedia validateBytes(byte[] bytes, MediaKind kind) {
        if (bytes == null || bytes.length < 4) {
            throw new BusinessException("Arquivo vazio ou corrompido.");
        }

        ValidatedMedia detectedImage = detectImageMagicBytes(bytes);
        if (kind == MediaKind.IMAGE) {
            if (detectedImage == null) {
                throw new BusinessException("Formato de imagem inválido ou não permitido. Utilize apenas PNG, JPG, WEBP ou GIF.");
            }
            return detectedImage;
        }

        if (kind == MediaKind.IMAGE_OR_PDF) {
            if (detectedImage != null) {
                return detectedImage;
            }
            if (isPdfMagicBytes(bytes)) {
                return new ValidatedMedia(bytes, "pdf", "application/pdf");
            }
            throw new BusinessException("Arquivo não permitido. Envie apenas documentos PDF ou imagens PNG/JPG/WEBP válidas.");
        }

        if (kind == MediaKind.AUDIO) {
            ValidatedMedia audio = detectAudioMagicBytes(bytes);
            if (audio == null) {
                throw new BusinessException("Formato de áudio inválido. Utilize apenas MP3, WAV, OGG ou M4A.");
            }
            return audio;
        }

        throw new BusinessException("Tipo de mídia não suportado.");
    }

    private static ValidatedMedia detectImageMagicBytes(byte[] b) {
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return new ValidatedMedia(b, "png", "image/png");
        }
        // JPEG: FF D8 FF
        if ((b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return new ValidatedMedia(b, "jpg", "image/jpeg");
        }
        // GIF: GIF87a / GIF89a
        if (b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a') {
            return new ValidatedMedia(b, "gif", "image/gif");
        }
        // WEBP: RIFF....WEBP
        if (b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return new ValidatedMedia(b, "webp", "image/webp");
        }
        return null;
    }

    private static boolean isPdfMagicBytes(byte[] b) {
        if (b.length < 5) return false;
        String header = new String(Arrays.copyOfRange(b, 0, 5), StandardCharsets.US_ASCII);
        return "%PDF-".equals(header);
    }

    private static ValidatedMedia detectAudioMagicBytes(byte[] b) {
        // WAV: RIFF....WAVE
        if (b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'A' && b[10] == 'V' && b[11] == 'E') {
            return new ValidatedMedia(b, "wav", "audio/wav");
        }
        // OGG: OggS
        if (b[0] == 'O' && b[1] == 'g' && b[2] == 'g' && b[3] == 'S') {
            return new ValidatedMedia(b, "ogg", "audio/ogg");
        }
        // MP3 ID3 tag or frame sync
        if ((b[0] == 'I' && b[1] == 'D' && b[2] == '3')
                || ((b[0] & 0xFF) == 0xFF && (b[1] & 0xE0) == 0xE0)) {
            return new ValidatedMedia(b, "mp3", "audio/mpeg");
        }
        // MP4/M4A: ....ftyp
        if (b.length >= 8 && b[4] == 'f' && b[5] == 't' && b[6] == 'y' && b[7] == 'p') {
            return new ValidatedMedia(b, "m4a", "audio/mp4");
        }
        return null;
    }

    private static String replaceOrAppendExtension(String fileName, String verifiedExt) {
        String base = fileName;
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) {
            base = fileName.substring(0, dotIdx);
        }
        base = base.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (base.isBlank()) {
            base = "media";
        }
        return base + "." + verifiedExt;
    }

    public static String uploadAndVerify(
            MultipartFile file,
            String path,
            StorageService storageService,
            String errorMessage
    ) {
        int idx = path != null ? path.lastIndexOf('/') : -1;
        String folder = idx > 0 ? path.substring(0, idx) : "uploads";
        String fileName = idx > 0 ? path.substring(idx + 1) : (path != null ? path : "media");
        return uploadAndVerify(file, folder, fileName, storageService, errorMessage);
    }

    public static String uploadAndVerify(
            MultipartFile file,
            String folder,
            String fileName,
            StorageService storageService,
            String errorMessage
    ) {
        String extHint = extractSafeExtension(file != null ? file.getOriginalFilename() : fileName, "");
        MediaKind kind = AUDIO_EXTS.contains(extHint) || (fileName != null && fileName.contains("hino"))
                ? MediaKind.AUDIO
                : PDF_EXTS.contains(extHint) ? MediaKind.IMAGE_OR_PDF : MediaKind.IMAGE;
        return uploadAndVerify(file, folder, fileName, kind, storageService, errorMessage);
    }

    public static String uploadAndVerify(
            MultipartFile file,
            String folder,
            String fileName,
            MediaKind kind,
            StorageService storageService,
            String errorMessage
    ) {
        ValidatedMedia validated = validateFile(file, kind);
        String safeName = replaceOrAppendExtension(fileName != null ? fileName : "media", validated.extension());
        try {
            String url = storageService.uploadFile(validated.bytes(), safeName, folder, validated.contentType());
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
