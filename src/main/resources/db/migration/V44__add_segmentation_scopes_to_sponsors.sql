-- V44: Adicionar escopos de exibição e segmentação aos patrocinadores (Principal, Base e Feminino)

ALTER TABLE public.sponsors
    ADD COLUMN IF NOT EXISTS show_on_main BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS show_on_youth BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS show_on_female BOOLEAN NOT NULL DEFAULT false;

-- Índices parciais para otimizar queries públicas filtradas por escopo
CREATE INDEX IF NOT EXISTS idx_sponsors_scope_main
    ON public.sponsors (is_active, display_order)
    WHERE deleted_at IS NULL AND show_on_main = true;

CREATE INDEX IF NOT EXISTS idx_sponsors_scope_youth
    ON public.sponsors (is_active, display_order)
    WHERE deleted_at IS NULL AND show_on_youth = true;

CREATE INDEX IF NOT EXISTS idx_sponsors_scope_female
    ON public.sponsors (is_active, display_order)
    WHERE deleted_at IS NULL AND show_on_female = true;

-- Atualizar os patrocinadores institucionais do seed para também apoiarem a Base e o Feminino
UPDATE public.sponsors
SET show_on_youth = true, show_on_female = true
WHERE name IN ('Banrisul', 'Biscoitos Zezé', 'Unimed Pelotas');

