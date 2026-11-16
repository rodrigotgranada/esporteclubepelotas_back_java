-- ==============================================================================
-- V49: Hardening de Schema (UTC Timestamps, PK/Sequence), Integridade e Índices
-- ==============================================================================

-- 1. Alinhamento estrito TIMESTAMP WITH TIME ZONE (UTC / Instant) para ddl-auto=validate
ALTER TABLE IF EXISTS transparency_documents
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE IF EXISTS transparency_board_members
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE IF EXISTS newsletter_subscribers
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC';

ALTER TABLE IF EXISTS polls
    ALTER COLUMN starts_at TYPE TIMESTAMP WITH TIME ZONE USING starts_at AT TIME ZONE 'UTC',
    ALTER COLUMN ends_at TYPE TIMESTAMP WITH TIME ZONE USING ends_at AT TIME ZONE 'UTC',
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE IF EXISTS poll_options
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC';

ALTER TABLE IF EXISTS poll_votes
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN user_id TYPE VARCHAR(36) USING user_id::text;

-- 2. Ressincroniza sequence de themes após seed explícito com id=1 na V8
SELECT setval(
    pg_get_serial_sequence('themes', 'id'),
    COALESCE((SELECT MAX(id) FROM themes), 1),
    true
);

-- 3. Garante NOT NULL em is_female para compatibilidade com boolean primitivo da entidade NewsArticle
UPDATE news_articles SET is_female = false WHERE is_female IS NULL;
ALTER TABLE news_articles ALTER COLUMN is_female SET DEFAULT false;
ALTER TABLE news_articles ALTER COLUMN is_female SET NOT NULL;

-- 4. Índices compostos e GIN para dia de jogo e consultas públicas frequentes
CREATE INDEX IF NOT EXISTS idx_match_events_timeline
    ON match_events (match_id, minute DESC, minute_extra DESC NULLS LAST, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_news_articles_prev_slugs_gin
    ON news_articles USING GIN (previous_slugs jsonb_path_ops);

CREATE INDEX IF NOT EXISTS idx_news_articles_category_pub
    ON news_articles (is_active, status, category_id, published_at DESC);

CREATE INDEX IF NOT EXISTS idx_news_articles_female
    ON news_articles (is_active, status, is_female, published_at DESC);

CREATE INDEX IF NOT EXISTS idx_news_articles_scheduled
    ON news_articles (status, published_at)
    WHERE is_active = true;

CREATE INDEX IF NOT EXISTS idx_squad_members_athlete
    ON squad_members (athlete_id);

CREATE INDEX IF NOT EXISTS idx_idols_active_featured
    ON idols (is_active, is_featured, display_order ASC, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_polls_active_created
    ON polls (is_active, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_users_reset_token
    ON users (reset_password_token)
    WHERE reset_password_token IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_transparency_docs_pub_cat_year
    ON transparency_documents (is_published, category, year DESC, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_transparency_board_active_type
    ON transparency_board_members (is_active, board_type, display_order ASC, name ASC);

CREATE INDEX IF NOT EXISTS idx_timeline_events_category
    ON timeline_events (category_id, is_active);

