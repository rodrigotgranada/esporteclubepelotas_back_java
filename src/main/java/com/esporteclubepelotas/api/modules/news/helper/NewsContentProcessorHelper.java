package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.security.HtmlSanitizerHelper;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
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

                if (src.startsWith("data:")) {
                    try {
                        Matcher matcher = BASE64_PATTERN.matcher(src);
                        if (!matcher.matches()) {
                            throw new BusinessException("Imagem embutida em formato inválido.");
                        }
                        byte[] bytes = Base64.getDecoder().decode(matcher.group(3));
                        MediaUploadSanitizer.ValidatedMedia validated =
                                MediaUploadSanitizer.validateBytes(bytes, MediaUploadSanitizer.MediaKind.IMAGE);

                        String fileName = "content-" + System.currentTimeMillis() + "-"
                                + (int) (Math.random() * 100000) + "." + validated.extension();
                        String folder = "news/content";

                        String url = storageService.uploadFile(
                                validated.bytes(),
                                fileName,
                                folder,
                                validated.contentType()
                        );
                        attrs.put("src", url);
                        node.put("attrs", attrs);
                        result.add(node);
                        continue;
                    } catch (BusinessException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        log.error("Falha ao converter imagem base64 no conteúdo da matéria: {}", ex.getMessage());
                        throw new BusinessException("Falha ao fazer upload de uma imagem inserida no texto da matéria.");
                    }
                } else if (!src.isBlank()) {
                    String safeSrc = HtmlSanitizerHelper.sanitizeSafeUrl(src);
                    attrs.put("src", safeSrc != null ? safeSrc : "");
                    node.put("attrs", attrs);
                }
            }

            // Sanitiza links em marks Tiptap contra esquemas javascript:/data:
            if (node.get("marks") instanceof List<?> rawMarks) {
                List<Object> safeMarks = new ArrayList<>();
                for (Object markObj : rawMarks) {
                    if (markObj instanceof Map<?, ?> rawMarkMap) {
                        Map<String, Object> mark = new HashMap<>((Map<String, Object>) rawMarkMap);
                        if ("link".equalsIgnoreCase(Objects.toString(mark.get("type"), ""))
                                && mark.get("attrs") instanceof Map<?, ?> rawLinkAttrs) {
                            Map<String, Object> linkAttrs = new HashMap<>((Map<String, Object>) rawLinkAttrs);
                            String safeHref = HtmlSanitizerHelper.sanitizeSafeUrl(Objects.toString(linkAttrs.get("href"), ""));
                            if (safeHref == null) {
                                continue;
                            }
                            linkAttrs.put("href", safeHref);
                            mark.put("attrs", linkAttrs);
                        }
                        safeMarks.add(mark);
                    } else {
                        safeMarks.add(markObj);
                    }
                }
                node.put("marks", safeMarks);
            }

            if (node.get("content") instanceof List<?> nestedList) {
                node.put("content", processNodes((List<Object>) nestedList, storageService));
            }

            result.add(node);
        }

        return result;
    }
}
