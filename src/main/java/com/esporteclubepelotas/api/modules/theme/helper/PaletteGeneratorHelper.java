package com.esporteclubepelotas.api.modules.theme.helper;

import com.esporteclubepelotas.api.modules.theme.entity.ThemePalette;
import lombok.AllArgsConstructor;
import lombok.Getter;

public final class PaletteGeneratorHelper {

    private PaletteGeneratorHelper() {}

    @Getter
    @AllArgsConstructor
    public static class Hsl {
        private final double h;
        private final double s;
        private final double l;
    }

    public static class GeneratedPalettes {
        private final ThemePalette light;
        private final ThemePalette dark;

        public GeneratedPalettes(ThemePalette light, ThemePalette dark) {
            this.light = light;
            this.dark = dark;
        }

        public ThemePalette getLight() {
            return light;
        }

        public ThemePalette getDark() {
            return dark;
        }
    }

    public static Hsl hexToHsl(String hex) {
        String cleaned = hex.replace("#", "").trim();
        if (cleaned.length() == 3) {
            StringBuilder sb = new StringBuilder();
            for (char c : cleaned.toCharArray()) {
                sb.append(c).append(c);
            }
            cleaned = sb.toString();
        }

        double r = Integer.parseInt(cleaned.substring(0, 2), 16) / 255.0;
        double g = Integer.parseInt(cleaned.substring(2, 4), 16) / 255.0;
        double b = Integer.parseInt(cleaned.substring(4, 6), 16) / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double h = 0;
        double s = 0;
        double l = (max + min) / 2.0;

        if (max != min) {
            double d = max - min;
            s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6.0 : 0.0);
            } else if (max == g) {
                h = (b - r) / d + 2.0;
            } else {
                h = (r - g) / d + 4.0;
            }
            h /= 6.0;
        }

        return new Hsl(Math.round(h * 360.0), Math.round(s * 100.0), Math.round(l * 100.0));
    }

    public static String hslToHex(Hsl hsl) {
        double sRatio = Math.max(0.0, Math.min(100.0, hsl.getS())) / 100.0;
        double lRatio = Math.max(0.0, Math.min(100.0, hsl.getL())) / 100.0;

        double c = (1.0 - Math.abs(2.0 * lRatio - 1.0)) * sRatio;
        double x = c * (1.0 - Math.abs(((hsl.getH() / 60.0) % 2.0) - 1.0));
        double m = lRatio - c / 2.0;

        double r = 0;
        double g = 0;
        double b = 0;

        double h = hsl.getH();
        if (h >= 0 && h < 60) {
            r = c; g = x; b = 0;
        } else if (h >= 60 && h < 120) {
            r = x; g = c; b = 0;
        } else if (h >= 120 && h < 180) {
            r = 0; g = c; b = x;
        } else if (h >= 180 && h < 240) {
            r = 0; g = x; b = c;
        } else if (h >= 240 && h < 300) {
            r = x; g = 0; b = c;
        } else if (h >= 300 && h <= 360) {
            r = c; g = 0; b = x;
        }

        int red = Math.max(0, Math.min(255, (int) Math.round((r + m) * 255.0)));
        int green = Math.max(0, Math.min(255, (int) Math.round((g + m) * 255.0)));
        int blue = Math.max(0, Math.min(255, (int) Math.round((b + m) * 255.0)));

        return String.format("#%02x%02x%02x", red, green, blue);
    }

    public static GeneratedPalettes generateCampaignPalettes(String seedHex) {
        Hsl hsl = hexToHsl(seedHex);
        double h = hsl.getH();
        double s = hsl.getS();

        ThemePalette light = ThemePalette.builder()
                .primary(seedHex.toLowerCase())
                .secondary(hslToHex(new Hsl(h, Math.min(100.0, s + 10.0), 32.0)))
                .buttonSecondary(hslToHex(new Hsl(h, Math.min(100.0, s + 10.0), 32.0)))
                .buttonPrimaryText(hslToHex(new Hsl(h, Math.min(100.0, s + 10.0), 20.0)))
                .buttonSecondaryText("#ffffff")
                .background(hslToHex(new Hsl(h, 35.0, 97.0)))
                .surface("#ffffff")
                .header(seedHex.toLowerCase())
                .card("#ffffff")
                .drawer("#ffffff")
                .table("#ffffff")
                .textPrimary(hslToHex(new Hsl(h, 60.0, 15.0)))
                .textSecondary(hslToHex(new Hsl(h, 40.0, 32.0)))
                .border(hslToHex(new Hsl(h, 45.0, 88.0)))
                .success("#22c55e")
                .error("#ea580c") // Terracota Alerta
                .warning("#f59e0b")
                .build();

        ThemePalette dark = ThemePalette.builder()
                .primary(hslToHex(new Hsl(h, Math.min(100.0, s + 15.0), 68.0)))
                .secondary(seedHex.toLowerCase())
                .buttonSecondary(seedHex.toLowerCase())
                .buttonPrimaryText("#000000")
                .buttonSecondaryText("#ffffff")
                .background(hslToHex(new Hsl(h, 35.0, 7.0)))
                .surface(hslToHex(new Hsl(h, 25.0, 14.0)))
                .header(hslToHex(new Hsl(h, 30.0, 11.0)))
                .card(hslToHex(new Hsl(h, 30.0, 11.0)))
                .drawer(hslToHex(new Hsl(h, 30.0, 11.0)))
                .table(hslToHex(new Hsl(h, 30.0, 11.0)))
                .textPrimary(hslToHex(new Hsl(h, 40.0, 95.0)))
                .textSecondary(hslToHex(new Hsl(h, 30.0, 80.0)))
                .border(hslToHex(new Hsl(h, 40.0, 24.0)))
                .success("#22c55e")
                .error("#ea580c") // Terracota Alerta
                .warning("#f59e0b")
                .build();

        return new GeneratedPalettes(light, dark);
    }
}

