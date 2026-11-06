-- V12: Criação da tabela de Categorias Dinâmicas da Linha do Tempo e adição de event_date
CREATE TABLE IF NOT EXISTS public.timeline_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(80) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(50) NOT NULL DEFAULT 'Flag',
    description VARCHAR(255),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_timeline_categories_order ON public.timeline_categories (display_order ASC, name ASC);
CREATE INDEX IF NOT EXISTS idx_timeline_categories_active ON public.timeline_categories (is_active);

-- Seed com as categorias fundamentais do clube
INSERT INTO public.timeline_categories (id, name, slug, icon, description, display_order, is_active)
VALUES 
    ('11111111-1111-1111-1111-111111111101', 'Fundação', 'fundacao', 'Flag', 'Origem, fusão e fundação do clube', 1, true),
    ('11111111-1111-1111-1111-111111111102', 'Títulos', 'titulos', 'Trophy', 'Conquistas oficiais e taças levantadas', 2, true),
    ('11111111-1111-1111-1111-111111111103', 'Estádio', 'estadio', 'Landmark', 'Estádio Boca do Lobo e patrimônio áureo-cerúleo', 3, true),
    ('11111111-1111-1111-1111-111111111104', 'Marcos Históricos', 'marcos', 'Sparkles', 'Grandes momentos institucionais e celebrações', 4, true),
    ('11111111-1111-1111-1111-111111111105', 'Glórias', 'glorias', 'Star', 'Partidas lendárias, clássicos e feitos memoráveis', 5, true)
ON CONFLICT (slug) DO NOTHING;

-- Adiciona event_date e category_id em timeline_events
ALTER TABLE public.timeline_events 
ADD COLUMN IF NOT EXISTS event_date DATE,
ADD COLUMN IF NOT EXISTS category_id UUID REFERENCES public.timeline_categories(id) ON DELETE SET NULL;

-- Vincula os registros existentes da V11 às novas categorias
UPDATE public.timeline_events SET category_id = '11111111-1111-1111-1111-111111111101' WHERE category = 'FOUNDATION' OR category = 'fundacao';
UPDATE public.timeline_events SET category_id = '11111111-1111-1111-1111-111111111102' WHERE category = 'TITLE' OR category = 'titulos';
UPDATE public.timeline_events SET category_id = '11111111-1111-1111-1111-111111111103' WHERE category = 'STADIUM' OR category = 'estadio';
UPDATE public.timeline_events SET category_id = '11111111-1111-1111-1111-111111111104' WHERE category = 'MILESTONE' OR category = 'marcos';
UPDATE public.timeline_events SET category_id = '11111111-1111-1111-1111-111111111105' WHERE category = 'GLORY' OR category = 'glorias';

-- Popula event_date nas datas conhecidas
UPDATE public.timeline_events SET event_date = '1908-10-11' WHERE "year" = 1908;
UPDATE public.timeline_events SET event_date = '2008-10-11' WHERE "year" = 2008;
