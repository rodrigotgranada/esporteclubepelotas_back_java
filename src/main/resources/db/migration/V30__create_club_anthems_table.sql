-- V30__create_club_anthems_table.sql
-- Módulo 11: Hino Oficial do Esporte Clube Pelotas (Cadastro, Gestão de Versões, Letra, Áudio e Histórico)

CREATE TABLE IF NOT EXISTS public.club_anthems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    subtitle VARCHAR(300),
    lyrics TEXT NOT NULL,
    author_lyrics VARCHAR(150),
    author_music VARCHAR(150),
    composition_year INTEGER,
    audio_url TEXT,
    video_url TEXT,
    cover_image_url TEXT,
    history_title VARCHAR(200),
    history_text TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_official BOOLEAN NOT NULL DEFAULT false,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_club_anthems_active_deleted ON public.club_anthems (is_active, deleted_at);
CREATE INDEX IF NOT EXISTS idx_club_anthems_official ON public.club_anthems (is_official, deleted_at);

-- Inclusão do Hino Oficial em system_modules (Feature Flag)
INSERT INTO public.system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES (
    'm-anthem-001',
    'Hino Oficial',
    'anthem',
    'Hino oficial, letras, versões de áudio e história dos compositores',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- Seed inicial da Versão Oficial Centenária do Hino
INSERT INTO public.club_anthems (
    id,
    title,
    subtitle,
    lyrics,
    author_lyrics,
    author_music,
    composition_year,
    audio_url,
    video_url,
    cover_image_url,
    history_title,
    history_text,
    is_active,
    is_official,
    display_order,
    created_at,
    updated_at
) VALUES (
    'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
    'Hino Oficial do Esporte Clube Pelotas',
    'Orgulho-me de ser Áureo-Cerúleo',
    'Orgulho-me de ser áureo-cerúleo
Pela grandeza do ideal!
Ufano-me de ser áureo-cerúleo
Pelo que tens de emocional!

Exulto ao ver as cores gloriosas
Que lembram toda uma tradição!
Azul e amarelo são as cores
Que moram no meu coração!

Salve o Pelotas! Salve o glorioso!
Quem não te ama nunca sentiu emoção!
Salve o Pelotas! Vitorioso!
É a vitória o teu maior galardão!',
    'José Walter de Oliveira (Valmúrio)',
    'José Walter de Oliveira (Valmúrio)',
    1918,
    'https://ia800908.us.archive.org/16/items/hino-pelotas/hino-esporte-clube-pelotas.mp3',
    'https://www.youtube.com/watch?v=kY3j_x6c3y8',
    '/images/pelotas-logo.png',
    'A Poesia Imortal de Valmúrio',
    'Composto pelo saudoso músico, radialista e torcedor apaixonado José Walter de Oliveira, eternizado pelo pseudônimo Valmúrio, o hino do Esporte Clube Pelotas é uma das obras mais emblemáticas do futebol brasileiro. Seus versos tocam a alma de cada torcedor áureo-cerúleo, exaltando a tradição azul e amarela e a vibração inigualável da Boca do Lobo.',
    true,
    true,
    0,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;
