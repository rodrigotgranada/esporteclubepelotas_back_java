package com.esporteclubepelotas.api.common.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class FirebaseStorageService implements StorageService {

    @Value("${firebase.credentials-base64:}")
    private String credentialsBase64;

    @Value("${firebase.storage-bucket:}")
    private String storageBucket;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        if (credentialsBase64 == null || credentialsBase64.isBlank()) {
            log.warn("⚠️ Firebase credentials (FIREBASE_CREDENTIALS_BASE64) não fornecidas. O serviço de upload funcionará em modo fallback/mock.");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                byte[] decoded = Base64.getDecoder().decode(credentialsBase64);
                InputStream serviceAccount = new ByteArrayInputStream(decoded);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(storageBucket)
                        .build();

                FirebaseApp.initializeApp(options);
                this.initialized = true;
                log.info("✅ Firebase Storage inicializado com sucesso para o bucket: {}", storageBucket);
            } else {
                this.initialized = true;
            }
        } catch (Exception e) {
            log.error("❌ Falha ao inicializar Firebase Storage: {}", e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(byte[] bytes, String fileName, String folder, String contentType) {
        String cleanFolder = folder != null && !folder.isBlank() ? folder.replaceAll("^/|/$", "") + "/" : "";
        String uniqueFileName = UUID.randomUUID() + "-" + fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String fullPath = cleanFolder + uniqueFileName;

        if (!initialized) {
            log.info("Mock upload realizado para {} (tamanho: {} bytes)", fullPath, bytes.length);
            return "https://storage.googleapis.com/" + (storageBucket != null ? storageBucket : "mock-bucket") + "/" + fullPath;
        }

        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.create(fullPath, bytes, contentType != null ? contentType : "application/octet-stream");

            // URL pública do Firebase Storage
            String encodedPath = URLEncoder.encode(fullPath, StandardCharsets.UTF_8);
            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media", bucket.getName(), encodedPath);
        } catch (Exception e) {
            log.error("Erro ao fazer upload para o Firebase Storage: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar upload do arquivo", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (!initialized || fileUrl == null || fileUrl.isBlank()) return;

        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            // Extrai o path a partir da URL
            if (fileUrl.contains("/o/")) {
                String encodedPath = fileUrl.split("/o/")[1].split("\\?")[0];
                String decodedPath = java.net.URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
                Blob blob = bucket.get(decodedPath);
                if (blob != null) {
                    blob.delete();
                    log.info("Arquivo excluído do Firebase Storage: {}", decodedPath);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao excluir arquivo do Firebase Storage: {}", e.getMessage(), e);
        }
    }
}
