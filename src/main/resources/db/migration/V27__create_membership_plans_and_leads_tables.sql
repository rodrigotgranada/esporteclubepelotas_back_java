-- V27: Criação das tabelas de planos de sócios e leads de adesão via WhatsApp

CREATE TABLE IF NOT EXISTS membership_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    billing_period VARCHAR(50) NOT NULL DEFAULT 'MENSAL',
    advantages JSONB NOT NULL DEFAULT '[]'::jsonb,
    badge_text VARCHAR(100),
    is_highlighted BOOLEAN NOT NULL DEFAULT false,
    display_order INTEGER NOT NULL DEFAULT 0,
    whatsapp_override VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE INDEX IF NOT EXISTS idx_membership_plans_active_order 
    ON membership_plans (is_active, display_order) 
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_membership_plans_slug 
    ON membership_plans (slug);

CREATE TABLE IF NOT EXISTS membership_leads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID NOT NULL REFERENCES membership_plans(id) ON DELETE RESTRICT,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    cpf VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'NOVO',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_membership_leads_status_created 
    ON membership_leads (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_membership_leads_plan 
    ON membership_leads (plan_id);

-- Seed de planos oficiais do Esporte Clube Pelotas
INSERT INTO membership_plans (
    id, name, slug, description, price, billing_period, advantages, badge_text, is_highlighted, display_order, is_active
) VALUES
(
    'c0000000-0000-0000-0000-000000000001',
    'Sócio Lobo Raça',
    'socio-lobo-raca',
    'Ideal para o torcedor de arquibancada que não perde um jogo e canta os 90 minutos empurrando o Pelotas.',
    45.00,
    'MENSAL',
    '["Acesso 100% livre à Arquibancada Geral em todos os jogos na Boca do Lobo", "10% de desconto em toda a linha oficial na Loja do Lobo", "Carteirinha digital oficial no portal do torcedor", "Participação em sorteios exclusivos de camisas oficiais autografadas", "Rede de vantagens e descontos com parceiros do clube"]'::jsonb,
    'ARQUIBANCADA',
    false,
    1,
    true
),
(
    'c0000000-0000-0000-0000-000000000002',
    'Sócio Áureo-Cerúleo Ouro',
    'socio-aureo-ceruleo-ouro',
    'A experiência mais completa do torcedor áureo-cerúleo, com conforto, visão privilegiada e voz ativa no clube.',
    95.00,
    'MENSAL',
    '["Acesso livre e irrestrito ao Pavilhão Social e Arquibancada Geral", "15% de desconto permanente em compras na Loja Oficial", "Direito a voto nas eleições do clube após cumprimento estatutário", "Prioridade máxima na reserva e compra de ingressos para clássicos e decisões", "Welcome Kit de boas-vindas com brindes oficiais do Pelotas", "Acesso a treinos abertos e eventos especiais na Boca do Lobo"]'::jsonb,
    'MAIS ESCOLHIDO',
    true,
    2,
    true
),
(
    'c0000000-0000-0000-0000-000000000003',
    'Sócio Longe da Boca',
    'socio-longe-da-boca',
    'Para o torcedor que mora fora de Pelotas ou no exterior, mas mantém o amor pelo Lobo pulsando no peito.',
    30.00,
    'MENSAL',
    '["Acesso livre a até 3 jogos por temporada na Boca do Lobo quando visitar Pelotas", "15% de desconto nas compras da Loja Online Oficial", "Carteirinha digital personalizada de Sócio do Interior", "Acesso aos conteúdos e bastidores exclusivos do clube no portal", "Participação garantida nas lives e eventos digitais comemorativos"]'::jsonb,
    'INTERIOR & EXTERIOR',
    false,
    3,
    true
),
(
    'c0000000-0000-0000-0000-000000000004',
    'Sócio Lobinho Mirim',
    'socio-lobinho-mirim',
    'Desenvolvido com carinho para crianças de até 12 anos darem seus primeiros passos de amor pelo Esporte Clube Pelotas.',
    20.00,
    'MENSAL',
    '["Entrada 100% gratuita em todos os jogos acompanhado de sócio titular", "Direito a entrar no gramado de mãos dadas com os atletas do Pelotas", "Certificado oficial e exclusivo de Cria da Boca", "Kit infantil com adesivos e brindes do Lobinho", "Descontos especiais nas escolinhas oficiais de futebol do clube"]'::jsonb,
    'ATÉ 12 ANOS',
    false,
    4,
    true
)
ON CONFLICT (id) DO NOTHING;
