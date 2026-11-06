-- V17: Expande colunas de URLs de mídia, distintivos, avatares, fotos e capas para TEXT
-- Suporta tanto URLs longas assinadas (Firebase Storage / AWS S3) quanto Data URLs em modo fallback/mock

ALTER TABLE public.settings
    ALTER COLUMN club_logo_url TYPE TEXT,
    ALTER COLUMN google_maps_embed_url TYPE TEXT;

ALTER TABLE public.users
    ALTER COLUMN avatar_url TYPE TEXT;

ALTER TABLE public.athletes
    ALTER COLUMN photo_url TYPE TEXT;

ALTER TABLE public.idols
    ALTER COLUMN photo_url TYPE TEXT;

ALTER TABLE public.news_articles
    ALTER COLUMN cover_image TYPE TEXT,
    ALTER COLUMN youtube_video_url TYPE TEXT;

ALTER TABLE public.squads
    ALTER COLUMN cover_url TYPE TEXT;

ALTER TABLE public.timeline_events
    ALTER COLUMN image_url TYPE TEXT;
