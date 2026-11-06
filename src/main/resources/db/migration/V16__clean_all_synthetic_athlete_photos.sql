-- V16: Remove todas as URLs sintéticas geradas nos atletas e comissão técnica do seed (sem hash real do Unsplash)
UPDATE public.athletes
SET photo_url = NULL
WHERE photo_url LIKE 'https://images.unsplash.com/photo-%'
  AND photo_url NOT LIKE 'https://images.unsplash.com/photo-%-%';
