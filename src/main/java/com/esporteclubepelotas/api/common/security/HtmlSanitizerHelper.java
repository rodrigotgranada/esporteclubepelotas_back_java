package com.esporteclubepelotas.api.common.security;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.util.Locale;

/**
 * Sanitização defensiva contra XSS para conteúdos HTML ricos, comentários e URLs externas.
 */
public final class HtmlSanitizerHelper {

    private static final Safelist RICH_TEXT_SAFELIST = Safelist.relaxed()
            .addTags("figure", "figcaption", "hr")
            .addAttributes("img", "src", "alt", "title", "width", "height", "class")
            .addAttributes("a", "href", "title", "target", "rel")
            .addAttributes(":all", "class")
            .addProtocols("a", "href", "http", "https", "mailto")
            .addProtocols("img", "src", "http", "https");

    private HtmlSanitizerHelper() {
    }

    /**
     * Remove tags perigosas (&lt;script&gt;, iframes arbitrários, event handlers on*) mantendo
     * formatação editorial segura para trechos HTML legados (ex: Estádio Boca do Lobo).
     */
    public static String sanitizeRichHtml(String rawHtml) {
        if (rawHtml == null) {
            return null;
        }
        String trimmed = rawHtml.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        // Se for documento JSON Tiptap serializado, preserva estrutura JSON (sanitizada separadamente)
        if (trimmed.startsWith("{") && trimmed.contains("\"type\":\"doc\"")) {
            return trimmed;
        }
        Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        return Jsoup.clean(trimmed, "", RICH_TEXT_SAFELIST, outputSettings);
    }

    /**
     * Remove quaisquer tags HTML de comentários e textos curtos mantendo texto puro.
     */
    public static String sanitizePlainText(String rawText) {
        if (rawText == null) {
            return null;
        }
        return Jsoup.clean(rawText, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false)).trim();
    }

    /**
     * Valida esquemas de URLs externas para evitar javascript: / data: / vbscript:.
     */
    public static String sanitizeSafeUrl(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }
        String trimmed = rawUrl.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("/")) {
            return trimmed;
        }
        return null;
    }
}

