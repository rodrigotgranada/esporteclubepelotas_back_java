-- V9: Atualiza as reações das matérias para as chaves canônicas do sistema (wolf, fire, clap, heart)
UPDATE public.news_articles 
SET reactions = jsonb_build_object('wolf', 15, 'fire', 22, 'clap', 18, 'heart', 40)
WHERE reactions IS NOT NULL;
