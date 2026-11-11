-- ==========================================================
-- Migration V46: Campos de Denúncia e Moderação para Comentários de Notícias
-- ==========================================================

ALTER TABLE public.news_comments
  ADD COLUMN IF NOT EXISTS is_reported BOOLEAN NOT NULL DEFAULT false,
  ADD COLUMN IF NOT EXISTS report_count INT NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS last_report_reason TEXT,
  ADD COLUMN IF NOT EXISTS last_reported_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_news_comments_reported
  ON public.news_comments (is_reported, status);

