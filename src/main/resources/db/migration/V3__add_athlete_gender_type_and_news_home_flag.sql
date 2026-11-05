-- Migration V3: Add gender, athlete_type, youth_category, entry_year to athletes
-- and show_on_homepage to news_articles

ALTER TABLE public.athletes
    ADD COLUMN IF NOT EXISTS gender character varying(20) DEFAULT 'MALE' NOT NULL,
    ADD COLUMN IF NOT EXISTS athlete_type character varying(20) DEFAULT 'PROFESSIONAL' NOT NULL,
    ADD COLUMN IF NOT EXISTS youth_category character varying(30),
    ADD COLUMN IF NOT EXISTS entry_year integer;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'athletes_gender_check'
    ) THEN
        ALTER TABLE public.athletes
            ADD CONSTRAINT athletes_gender_check CHECK (((gender)::text = ANY ((ARRAY['MALE'::character varying, 'FEMALE'::character varying])::text[])));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'athletes_type_check'
    ) THEN
        ALTER TABLE public.athletes
            ADD CONSTRAINT athletes_type_check CHECK (((athlete_type)::text = ANY ((ARRAY['PROFESSIONAL'::character varying, 'YOUTH'::character varying])::text[])));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_athletes_gender_type ON public.athletes USING btree (is_active, gender, athlete_type);

-- Add show_on_homepage to news_articles
ALTER TABLE public.news_articles
    ADD COLUMN IF NOT EXISTS show_on_homepage boolean DEFAULT true NOT NULL;

CREATE INDEX IF NOT EXISTS idx_news_articles_home ON public.news_articles USING btree (is_active, status, show_on_homepage, published_at);

