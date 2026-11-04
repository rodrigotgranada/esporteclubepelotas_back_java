package com.esporteclubepelotas.api.common.storage;

public interface StorageService {
    String uploadFile(byte[] bytes, String fileName, String folder, String contentType);
    void deleteFile(String fileUrl);
}

