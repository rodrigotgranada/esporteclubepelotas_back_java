-- Migration V43: Adiciona flag is_female na tabela news_articles para isolamento editorial do Futebol Feminino
ALTER TABLE public.news_articles
    ADD COLUMN IF NOT EXISTS is_female BOOLEAN DEFAULT false;

CREATE INDEX IF NOT EXISTS idx_news_articles_is_female ON public.news_articles(is_female);

