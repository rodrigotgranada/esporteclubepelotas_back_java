-- Migration V33: Criação da tabela de Mantos e Uniformes Oficiais do Clube
CREATE TABLE IF NOT EXISTS public.club_kits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    kit_type VARCHAR(30) NOT NULL,
    competition VARCHAR(150) NOT NULL,
    season_year INTEGER NOT NULL,
    image_url TEXT NOT NULL,
    gallery_urls JSONB DEFAULT '[]'::jsonb,
    manufacturer VARCHAR(100),
    sponsor VARCHAR(150),
    description TEXT,
    is_current BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE INDEX IF NOT EXISTS idx_club_kits_current ON public.club_kits(is_current);
CREATE INDEX IF NOT EXISTS idx_club_kits_season_year ON public.club_kits(season_year);
CREATE INDEX IF NOT EXISTS idx_club_kits_competition ON public.club_kits(competition);
CREATE INDEX IF NOT EXISTS idx_club_kits_type ON public.club_kits(kit_type);
CREATE INDEX IF NOT EXISTS idx_club_kits_active ON public.club_kits(is_active);
CREATE INDEX IF NOT EXISTS idx_club_kits_deleted ON public.club_kits(deleted_at);
CREATE INDEX IF NOT EXISTS idx_club_kits_order ON public.club_kits(display_order);

-- Registra o módulo 'kits' nos módulos do sistema caso ainda não exista
INSERT INTO public.system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES (
    'm-kits-001',
    'Acervo de Mantos & Uniformes',
    'kits',
    'Uniformes da temporada atual (titular, reserva, goleiro, especial) e acervo histórico por campeonato',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    updated_at = NOW();

-- 1. Uniforme Titular Atual 2026 (Gauchão Série A)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
    'Manto Titular Tradicional 2026',
    'HOME',
    'Gauchão Série A',
    2026,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Biscoitos Zezé',
    'O clássico manto sagrado listrado verticalmente em azul cerúleo e amarelo ouro. Tecido tecnológico Dry-Fit de alta performance com escudo centenário bordado e detalhe comemorativo na gola.',
    true,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 2. Uniforme Reserva Atual 2026 (Gauchão Série A)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'a2b3c4d5-e6f7-8a9b-0c1d-2e3f4a5b6c7d',
    'Manto Reserva Branco 2026',
    'AWAY',
    'Gauchão Série A',
    2026,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Biscoitos Zezé',
    'Uniforme número 2 em branco puro com detalhes em frisos áureo-cerúleos nos ombros e punhos. Elegância e tradição para as batalhas longe da Boca do Lobo.',
    true,
    true,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 3. Uniforme de Goleiro Atual 2026 (Gauchão Série A)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'a3b4c5d6-e7f8-9a0b-1c2d-3e4f5a6b7c8d',
    'Camisa de Goleiro Grafite 2026',
    'GOALKEEPER',
    'Gauchão Série A',
    2026,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Banrisul',
    'Armadura dos guardiões da Boca do Lobo em tom grafite antracite com grafismos em amarelo ouro refletindo a muralha cerúlea.',
    true,
    true,
    2,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 4. Manto Especial Edição Histórica 2026 (Gauchão Série A)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'a4b5c6d7-e8f9-0a1b-2c3d-4e5f6a7b8c9d',
    'Manto Retrô Campeão Gaúcho de 1930',
    'SPECIAL',
    'Gauchão Série A',
    2026,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Biscoitos Zezé',
    'Edição comemorativa especial alusiva à histórica conquista de 1930, trazendo as clássicas listras largas, gola polo com cadarço e o escudo primitivo com as iniciais entrelaçadas.',
    true,
    true,
    3,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 5. Manto Histórico Anterior: Copa FGF 2025 (Titular)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'b1c2d3e4-f5a6-7b8c-9d0e-2f3a4b5c6d7e',
    'Manto Titular Copa FGF 2025',
    'HOME',
    'Copa FGF',
    2025,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Banrisul',
    'Uniforme utilizado na vitoriosa campanha da Copa FGF Troféu Zagallo de 2025.',
    false,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 6. Manto Histórico Anterior: Copa FGF 2025 (Reserva)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'b2c3d4e5-f6a7-8b9c-0d1e-3f4a5b6c7d8e',
    'Manto Reserva Copa FGF 2025',
    'AWAY',
    'Copa FGF',
    2025,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Banrisul',
    'Versão branca utilizada nas partidas fora de casa na Copa FGF 2025.',
    false,
    true,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 7. Manto Histórico Anterior: Gauchão Série A2 2024 (Manto do Acesso Titular)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'c1d2e3f4-a5b6-7c8d-9e0f-3a4b5c6d7e8f',
    'Manto do Acesso Histórico 2024',
    'HOME',
    'Gauchão Série A2',
    2024,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Biscoitos Zezé',
    'O manto inesquecível da temporada de 2024 que selou o épico retorno do Lobo à elite do futebol gaúcho.',
    false,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 8. Manto Histórico Anterior: Gauchão Série A2 2024 (Reserva)
INSERT INTO public.club_kits (
    id,
    name,
    kit_type,
    competition,
    season_year,
    image_url,
    gallery_urls,
    manufacturer,
    sponsor,
    description,
    is_current,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'c2d3e4f5-a6b7-8c9d-0e1f-4a5b6c7d8e9f',
    'Manto Reserva do Acesso 2024',
    'AWAY',
    'Gauchão Série A2',
    2024,
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    'Dresch Sport',
    'Biscoitos Zezé',
    'Uniforme branco com diagonal azul e amarela utilizado na campanha heroica do acesso em 2024.',
    false,
    true,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

