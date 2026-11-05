package com.esporteclubepelotas.api.modules.football.helper;

import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public final class AthletesSlugHelper {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES = Pattern.compile("(^-+|-+$)");

    private AthletesSlugHelper() {}

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "atleta";
        }

        String nowhitespace = WHITESPACE.matcher(input.trim().toLowerCase(Locale.ROOT)).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        slug = NONLATIN.matcher(slug).replaceAll("");
        slug = Pattern.compile("-+").matcher(slug).replaceAll("-");
        slug = EDGES.matcher(slug).replaceAll("");

        return slug.isBlank() ? "atleta" : slug;
    }

    public static String generateUniqueAthleteSlug(AthleteRepository repository, String name, String nickname, UUID currentId) {
        String baseText = (nickname != null && !nickname.isBlank()) ? nickname : name;
        String baseSlug = slugify(baseText);

        boolean exists = currentId != null
                ? repository.existsBySlugAndIdNot(baseSlug, currentId)
                : repository.existsBySlug(baseSlug);

        if (!exists) {
            return baseSlug;
        }

        int counter = 1;
        while (true) {
            String candidate = baseSlug + "-" + counter;
            boolean candidateExists = currentId != null
                    ? repository.existsBySlugAndIdNot(candidate, currentId)
                    : repository.existsBySlug(candidate);

            if (!candidateExists) {
                return candidate;
            }
            counter++;
        }
    }
}

