package com.esporteclubepelotas.api.modules.news.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsReactionsCounter implements Serializable {

    @Builder.Default
    private int wolf = 0;

    @Builder.Default
    private int fire = 0;

    @Builder.Default
    private int clap = 0;

    @Builder.Default
    private int heart = 0;

    public void increment(NewsReactionType type) {
        if (type == null) return;
        switch (type) {
            case WOLF -> this.wolf++;
            case FIRE -> this.fire++;
            case CLAP -> this.clap++;
            case HEART -> this.heart++;
        }
    }

    public void decrement(NewsReactionType type) {
        if (type == null) return;
        switch (type) {
            case WOLF -> this.wolf = Math.max(0, this.wolf - 1);
            case FIRE -> this.fire = Math.max(0, this.fire - 1);
            case CLAP -> this.clap = Math.max(0, this.clap - 1);
            case HEART -> this.heart = Math.max(0, this.heart - 1);
        }
    }
}

