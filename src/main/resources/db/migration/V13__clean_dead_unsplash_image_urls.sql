-- V13: Limpa URLs externas de teste do Unsplash que foram excluídas na fonte
-- Ao definir como NULL, o frontend utiliza o fallback visual oficial do clube sem gerar erros 404 no console
UPDATE public.news_articles
SET cover_image = NULL
WHERE cover_image LIKE '%photo-1508098682722%'
   OR cover_image LIKE '%photo-1517649763962%';

UPDATE public.timeline_events
SET image_url = NULL
WHERE image_url LIKE '%photo-1508098682722%'
   OR image_url LIKE '%photo-1517649763962%';
