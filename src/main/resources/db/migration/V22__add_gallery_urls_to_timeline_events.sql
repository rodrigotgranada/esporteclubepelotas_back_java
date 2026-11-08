-- V22: Adicionar coluna gallery_urls para mini-galeria de fotos históricas complementares
ALTER TABLE timeline_events
ADD COLUMN IF NOT EXISTS gallery_urls jsonb DEFAULT '[]'::jsonb;
