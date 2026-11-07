package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class NewsContentProcessorHelper {

    private static final Pattern BASE64_PATTERN = Pattern.compile("^data:([A-Za-z-+]+/([A-Za-z-+]+));base64,(.+)$");

    private NewsContentProcessorHelper() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> processContentBase64Images(Map<String, Object> content, StorageService storageService) {
        if (content == null || storageService == null) {
            return content;
        }

        Object contentNodes = content.get("content");
        if (!(contentNodes instanceof List<?> list)) {
            return content;
        }

        Map<String, Object> copy = new HashMap<>(content);
        copy.put("content", processNodes((List<Object>) list, storageService));
        return copy;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> processNodes(List<Object> nodes, StorageService storageService) {
        List<Object> result = new ArrayList<>();

        for (Object nodeObj : nodes) {
            if (!(nodeObj instanceof Map<?, ?> rawMap)) {
                result.add(nodeObj);
                continue;
            }

            Map<String, Object> node = new HashMap<>((Map<String, Object>) rawMap);
            String type = Objects.toString(node.get("type"), "");

            if ("image".equalsIgnoreCase(type) && node.get("attrs") instanceof Map<?, ?> rawAttrs) {
                Map<String, Object> attrs = new HashMap<>((Map<String, Object>) rawAttrs);
                String src = Objects.toString(attrs.get("src"), "");

                if (src.startsWith("data:image/")) {
                    try {
                        Matcher matcher = BASE64_PATTERN.matcher(src);
                        if (matcher.matches()) {
                            String mimeType = matcher.group(1);
                            String ext = matcher.group(2);
                            String base64Data = matcher.group(3);

                            byte[] bytes = Base64.getDecoder().decode(base64Data);
                            String fileName = "content-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 100000) + "." + ext;
                            String folder = "news/content";

                            String url = storageService.uploadFile(bytes, folder, fileName, mimeType);
                            attrs.put("src", url);
                            node.put("attrs", attrs);
                            result.add(node);
                            continue;
                        }
                    } catch (Exception ex) {
                        log.error("Falha ao converter imagem base64 no conteúdo da matéria: {}", ex.getMessage());
                        throw new BusinessException("Falha ao fazer upload de uma imagem inserida no texto da matéria.");
                    }
                }
            }

            if (node.get("content") instanceof List<?> nestedList) {
                node.put("content", processNodes((List<Object>) nestedList, storageService));
            }

            result.add(node);
        }

        return result;
    }
}
