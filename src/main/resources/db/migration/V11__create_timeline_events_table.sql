-- V11: Criação da tabela de Linha do Tempo / Marcos Históricos do E.C. Pelotas e Seed Inicial

CREATE TABLE IF NOT EXISTS public.timeline_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "year" INTEGER NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(30) NOT NULL DEFAULT 'MILESTONE',
    image_url VARCHAR(1000),
    date_formatted VARCHAR(100),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_highlighted BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by_id VARCHAR(36) REFERENCES public.users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_timeline_year ON public.timeline_events ("year");
CREATE INDEX IF NOT EXISTS idx_timeline_active_year ON public.timeline_events (is_active, "year");

-- Seed com os grandes marcos históricos oficiais do Esporte Clube Pelotas
INSERT INTO public.timeline_events (
    id, "year", title, description, category, image_url, date_formatted, display_order, is_highlighted, is_active
) VALUES 
(
    gen_random_uuid(),
    1908,
    'Fundação do Esporte Clube Pelotas',
    'Em 11 de outubro de 1908, na residência de Joaquim Luís Osório, nasce o Esporte Clube Pelotas da fusão entre o Club Sportivo Pelotense e o Club Sportivo Federal. Sob as cores azul e amarelo ouro (áureo-cerúleo), o clube iniciou sua trajetória como o eterno Lobo da Boca do Lobo.',
    'FOUNDATION',
    'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80',
    '11 de Outubro de 1908',
    1,
    true,
    true
),
(
    gen_random_uuid(),
    1911,
    'Primeiro Clássico Bra-Pel da História',
    'Em 1911 foi disputado o primeiro confronto oficial do clássico Bra-Pel, a maior rivalidade do interior do Rio Grande do Sul e uma das mais fervorosas do futebol sul-americano. Uma partida que iniciou mais de um século de disputas épicas.',
    'GLORY',
    'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80',
    '1911',
    2,
    false,
    true
),
(
    gen_random_uuid(),
    1917,
    'Inauguração do Estádio Boca do Lobo',
    'Em 1917, o Pelotas inaugurou a lendária Boca do Lobo, o mais antigo estádio de futebol em atividade ininterrupta de todo o Rio Grande do Sul. O templo centenário erguido na Avenida Bento Gonçalves transformou-se no santuário da torcida áureo-cerúlea.',
    'STADIUM',
    'https://images.unsplash.com/photo-1487466365202-1afab86e791b?auto=format&fit=crop&w=1200&q=80',
    '1917',
    3,
    true,
    true
),
(
    gen_random_uuid(),
    1930,
    'Campeão Gaúcho Histórico',
    'No ano de 1930, o Esporte Clube Pelotas conquistou a glória máxima do futebol gaúcho ao sagrar-se Campeão Gaúcho, superando os grandes da capital e cravando para sempre o nome do Lobo na galeria dos imortais do estado.',
    'TITLE',
    'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=1200&q=80',
    '1930',
    4,
    true,
    true
),
(
    gen_random_uuid(),
    1951,
    'Excursão Internacional & Taça Rio Grande',
    'Na década de 1950, o Pelotas consolidou seu prestígio internacional disputando partidas memoráveis contra equipes uruguaias e argentinas, além de conquistar títulos municipais e troféus estaduais que reforçaram a tradição do futebol pelotense.',
    'GLORY',
    'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80',
    '1951',
    5,
    false,
    true
),
(
    gen_random_uuid(),
    2008,
    'O Centenário Áureo-Cerúleo (100 Anos)',
    'Uma data inesquecível: em 2008, o Esporte Clube Pelotas completou 100 anos de fundação. Uma celebração emocionante na Boca do Lobo reuniu gerações de torcedores, ídolos de todas as épocas e confirmou o amor eterno das arquibancadas pelo manto azul e ouro.',
    'MILESTONE',
    'https://images.unsplash.com/photo-1517466787929-bc90951d0974?auto=format&fit=crop&w=1200&q=80',
    '11 de Outubro de 2008',
    6,
    true,
    true
),
(
    gen_random_uuid(),
    2013,
    'Campeão da Recopa Gaúcha & Super Copa Gaúcha',
    'Em 2013, o Pelotas ergueu a taça da Super Copa Gaúcha e, na sequência, sagrou-se Campeão da Recopa Gaúcha diante do Internacional, demonstrando mais uma vez a força da camisa azul e amarela nas grandes decisões.',
    'TITLE',
    'https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=1200&q=80',
    '2013',
    7,
    true,
    true
),
(
    gen_random_uuid(),
    2019,
    'Campeão da Copa FGF / Troféu Seu Verardi',
    'Uma campanha brilhante culminou no título da Copa FGF de 2019, garantindo vaga nas competições nacionais e inflamando a Boca do Lobo em uma festa monumental da torcida áureo-cerúlea.',
    'TITLE',
    'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80',
    '2019',
    8,
    false,
    true
),
(
    gen_random_uuid(),
    2024,
    'Acesso Histórico & Renascimento Áureo-Cerúleo',
    'Em uma caminhada de superação épica, o Lobo garantiu o retorno à elite do futebol gaúcho na Divisão de Acesso, com o estádio lotado e a certeza de que o Esporte Clube Pelotas pertence aos grandes palcos do futebol brasileiro.',
    'MILESTONE',
    'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80',
    '2024',
    9,
    true,
    true
);
