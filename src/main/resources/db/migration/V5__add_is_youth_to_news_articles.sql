-- Migration V5: Add is_youth flag to news_articles for youth academy portal hub
ALTER TABLE public.news_articles
    ADD COLUMN IF NOT EXISTS is_youth boolean DEFAULT false NOT NULL;

CREATE INDEX IF NOT EXISTS idx_news_articles_youth ON public.news_articles USING btree (is_active, status, is_youth, published_at);
