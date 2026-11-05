package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public final class NewsSlugHelper {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES = Pattern.compile("(^-+|-+$)");

    private NewsSlugHelper() {}

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "materia";
        }

        String nowhitespace = WHITESPACE.matcher(input.trim().toLowerCase(Locale.ROOT)).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        slug = NONLATIN.matcher(slug).replaceAll("");
        slug = Pattern.compile("-+").matcher(slug).replaceAll("-");
        slug = EDGES.matcher(slug).replaceAll("");

        return slug.isBlank() ? "materia" : slug;
    }

    public static String generateUniqueArticleSlug(String title, NewsArticleRepository repository, UUID currentArticleId) {
        String baseSlug = slugify(title);

        boolean exists = currentArticleId != null
                ? repository.existsBySlugAndIdNot(baseSlug, currentArticleId)
                : repository.existsBySlug(baseSlug);

        if (!exists) {
            return baseSlug;
        }

        int counter = 1;
        while (true) {
            String candidate = baseSlug + "-" + counter;
            boolean candidateExists = currentArticleId != null
                    ? repository.existsBySlugAndIdNot(candidate, currentArticleId)
                    : repository.existsBySlug(candidate);

            if (!candidateExists) {
                return candidate;
            }
            counter++;
        }
    }
}

