package com.esporteclubepelotas.api.modules.theme.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemePalette implements Serializable {

    @Builder.Default
    private String primary = "#fbbf24";

    @Builder.Default
    private String secondary = "#1e3a8a";

    @Builder.Default
    private String background = "#0a0a0a";

    @Builder.Default
    private String surface = "#171717";

    @Builder.Default
    private String header = "#111111";

    @Builder.Default
    private String card = "#111111";

    @Builder.Default
    private String drawer = "#111111";

    @Builder.Default
    private String table = "#111111";

    @Builder.Default
    private String buttonSecondary = "#1e3a8a";

    private String buttonPrimaryText;

    private String buttonSecondaryText;

    @Builder.Default
    private String textPrimary = "#ffffff";

    @Builder.Default
    private String textSecondary = "#9ca3af";

    @Builder.Default
    private String border = "#374151";

    @Builder.Default
    private String success = "#22c55e";

    @Builder.Default
    private String error = "#ea580c"; // Terracota Alerta (Proibição da cor vermelha - Regra 4)

    @Builder.Default
    private String warning = "#f59e0b";
}
