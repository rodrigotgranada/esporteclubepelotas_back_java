package com.esporteclubepelotas.api.modules.stadium.entity;

import com.esporteclubepelotas.api.modules.stadium.dto.StadiumFactDto;
import com.esporteclubepelotas.api.modules.stadium.dto.StadiumGalleryImageDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stadium_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StadiumInfo {

    @Id
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "subtitle", length = 500)
    private String subtitle;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(name = "inauguration_date", length = 100)
    private String inaugurationDate;

    @Column(name = "capacity", length = 100)
    private String capacity;

    @Column(name = "pitch_dimensions", length = 100)
    private String pitchDimensions;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "maps_direct_url", columnDefinition = "TEXT")
    private String mapsDirectUrl;

    @Column(name = "maps_embed_url", columnDefinition = "TEXT")
    private String mapsEmbedUrl;

    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gallery_json", nullable = false)
    private List<StadiumGalleryImageDto> gallery = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "facts_json", nullable = false)
    private List<StadiumFactDto> facts = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}

