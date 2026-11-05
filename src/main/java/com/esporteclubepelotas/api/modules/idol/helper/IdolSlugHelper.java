package com.esporteclubepelotas.api.modules.idol.helper;

import com.esporteclubepelotas.api.modules.idol.repository.IdolRepository;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class IdolSlugHelper {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIDASH = Pattern.compile("-+");

    private IdolSlugHelper() {}

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "idolo";
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = MULTIDASH.matcher(slug).replaceAll("-");
        slug = slug.toLowerCase(Locale.ENGLISH);
        if (slug.startsWith("-")) slug = slug.substring(1);
        if (slug.endsWith("-")) slug = slug.substring(0, slug.length() - 1);
        return slug.isBlank() ? "idolo" : slug;
    }

    public static String generateUniqueSlug(String baseText, IdolRepository repository, String currentId) {
        String baseSlug = toSlug(baseText);
        String candidate = baseSlug;
        int counter = 2;

        while (true) {
            var existing = repository.findBySlug(candidate);
            if (existing.isEmpty() || (currentId != null && existing.get().getId().equals(currentId))) {
                return candidate;
            }
            candidate = baseSlug + "-" + counter++;
        }
    }
}

