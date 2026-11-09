-- Migration V31: Criação da tabela de Símbolos, Mascote Oficial e Identidade do Clube
CREATE TABLE IF NOT EXISTS public.club_symbols (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category VARCHAR(30) NOT NULL,
    name VARCHAR(150) NOT NULL,
    subtitle VARCHAR(250),
    description TEXT,
    image_url TEXT,
    gallery_urls JSONB DEFAULT '[]'::jsonb,
    year_start INTEGER,
    year_end INTEGER,
    is_official BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE INDEX IF NOT EXISTS idx_club_symbols_category ON public.club_symbols(category);
CREATE INDEX IF NOT EXISTS idx_club_symbols_active ON public.club_symbols(is_active);
CREATE INDEX IF NOT EXISTS idx_club_symbols_deleted ON public.club_symbols(deleted_at);
CREATE INDEX IF NOT EXISTS idx_club_symbols_order ON public.club_symbols(display_order);

-- Registra o módulo 'symbols' nos módulos do sistema caso ainda não exista
INSERT INTO public.system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES (
    'm-symbols-001',
    'Símbolos & Mascote',
    'symbols',
    'Mascote oficial, histórico de mascotes, escudos históricos, bandeira e cores áureo-cerúleas',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 1. Mascote Oficial do Pelotas (O Lobo da Boca do Lobo)
INSERT INTO public.club_symbols (
    id,
    category,
    name,
    subtitle,
    description,
    image_url,
    gallery_urls,
    year_start,
    is_official,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'b1c2d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e',
    'MASCOT',
    'Lobo da Boca do Lobo',
    'Mascote Oficial do Esporte Clube Pelotas',
    'O Lobo é o símbolo máximo da alma, da garra e da lealdade áureo-cerúlea. A alcunha nasceu intimamente entrelaçada com a mística do Estádio Boca do Lobo — o palco de futebol mais antigo em atividade ininterrupta no Rio Grande do Sul e um dos monumentos do futebol brasileiro. Quando o Pelotas entra em campo, o Lobo sintetiza a força da matilha: uma torcida destemida e apaixonada que canta e defende o pavilhão azul e amarelo com honra.',
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    1917,
    true,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 2. Mascote Histórico (Fantasia Clássica dos Anos 80)
INSERT INTO public.club_symbols (
    id,
    category,
    name,
    subtitle,
    description,
    image_url,
    gallery_urls,
    year_start,
    year_end,
    is_official,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e',
    'MASCOT',
    'Lobo dos Anos Dourados',
    'Versão clássica da mascote nas décadas de 80 e 90',
    'Representação histórica do mascote que alegrou gerações de torcedores no alambrado da Boca do Lobo durante as históricas campanhas do Campeonato Gaúcho e competições nacionais.',
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    1980,
    1995,
    false,
    true,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 3. Escudo Oficial Centenário
INSERT INTO public.club_symbols (
    id,
    category,
    name,
    subtitle,
    description,
    image_url,
    gallery_urls,
    year_start,
    is_official,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'c1d2e3f4-a5b6-7c8d-9e0f-1a2b3c4d5e6f',
    'SHIELD',
    'Distintivo Oficial Centenário',
    'O Brasão Sagrado do Esporte Clube Pelotas',
    'O distintivo do Esporte Clube Pelotas é composto pelas elegantes iniciais entrelaçadas "E.C.P." no interior de um escudo de formato suíço. As cores azul cerúleo e amarelo ouro refletem a grandeza do ideal e a tradição que atravessa os séculos desde 1908, coroada pela histórica conquista do Campeonato Gaúcho de 1930.',
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    1908,
    true,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 4. Escudo da Fundação (1908)
INSERT INTO public.club_symbols (
    id,
    category,
    name,
    subtitle,
    description,
    image_url,
    gallery_urls,
    year_start,
    year_end,
    is_official,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'c2d3e4f5-a6b7-8c9d-0e1f-2a3b4c5d6e7f',
    'SHIELD',
    'Escudo Primitivo da Fundação',
    'Desenhado pelos fundadores em 11 de outubro de 1908',
    'Os primeiros traços do escudo áureo-cerúleo desenhados logo após a união do Club Sportivo Pelotas e do Club Foot-Ball Pelotense, preservando a essência estética dos clubes pioneiros do Rio Grande do Sul.',
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    1908,
    1925,
    false,
    true,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 5. Pavilhão / Bandeira Oficial
INSERT INTO public.club_symbols (
    id,
    category,
    name,
    subtitle,
    description,
    image_url,
    gallery_urls,
    year_start,
    is_official,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'd1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a',
    'FLAG',
    'Pavilhão Oficial Áureo-Cerúleo',
    'A Bandeira Centenária que Tremula na Boca do Lobo',
    'A bandeira oficial do Esporte Clube Pelotas é constituída por duas faixas horizontais de proporções iguais: a superior em amarelo ouro e a inferior em azul cerúleo, trazendo ao centro o distintivo oficial do clube. Hasteada solenemente em todos os dias de partida no mastro monumental do estádio.',
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    1908,
    true,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 6. Cores Áureo-Cerúleas
INSERT INTO public.club_symbols (
    id,
    category,
    name,
    subtitle,
    description,
    image_url,
    gallery_urls,
    year_start,
    is_official,
    is_active,
    display_order,
    created_at,
    updated_at
) VALUES (
    'e1f2a3b4-c5d6-7e8f-9a0b-1c2d3e4f5a6b',
    'COLORS',
    'Azul Cerúleo & Amarelo Ouro',
    'A Nobreza e o Céu de Pelotas',
    'As cores do clube foram escolhidas para simbolizar a união perfeita: o amarelo ouro (áureo) evocando a nobreza, a energia solar e a conquista eterna; e o azul celeste (cerúleo) remetendo à imensidão do céu da Princesa do Sul e à serenidade de uma instituição construída para a eternidade.',
    '/images/pelotas-logo.png',
    '[]'::jsonb,
    1908,
    true,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;
