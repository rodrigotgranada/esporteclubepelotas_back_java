-- V23: Criar tabela de patrocinadores e parceiros oficiais (Módulo 6)

CREATE TABLE IF NOT EXISTS sponsors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    logo_url TEXT NOT NULL,
    website_url TEXT,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    contract_start_date DATE,
    contract_end_date DATE,
    clicks_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sponsors_active_dates 
    ON sponsors (is_active, contract_start_date, contract_end_date);

CREATE INDEX IF NOT EXISTS idx_sponsors_featured 
    ON sponsors (is_featured, display_order);

-- Seed de parceiros e patrocinadores oficiais representativos
INSERT INTO sponsors (id, name, logo_url, website_url, is_featured, is_active, display_order, contract_start_date, contract_end_date, clicks_count)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Banrisul', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Banrisul_logo.svg/512px-Banrisul_logo.svg.png', 'https://www.banrisul.com.br', true, true, 1, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '365 days', 1420),
    ('a0000000-0000-0000-0000-000000000002', 'Biscoitos Zezé', 'https://zeze.com.br/wp-content/themes/zeze/assets/images/logo-zeze.png', 'https://zeze.com.br', true, true, 2, CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE + INTERVAL '300 days', 980),
    ('a0000000-0000-0000-0000-000000000003', 'Unimed Pelotas', 'https://www.unimed.coop.br/site/unimed-default-theme/images/logo.svg', 'https://www.unimed.coop.br/site/web/pelotas', true, true, 3, CURRENT_DATE - INTERVAL '90 days', CURRENT_DATE + INTERVAL '270 days', 612),
    ('a0000000-0000-0000-0000-000000000004', 'Lifemed', 'https://www.lifemed.com.br/wp-content/uploads/2021/05/logo-lifemed.png', 'https://www.lifemed.com.br', false, true, 4, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '180 days', 340),
    ('a0000000-0000-0000-0000-000000000005', 'Postos Buffon', 'https://buffon.com.br/wp-content/uploads/2021/04/logo-buffon.png', 'https://buffon.com.br', false, true, 5, CURRENT_DATE - INTERVAL '45 days', CURRENT_DATE + INTERVAL '200 days', 290),
    ('a0000000-0000-0000-0000-000000000006', 'Kappesberg', 'https://www.kappesberg.com.br/assets/images/logo.svg', 'https://www.kappesberg.com.br', false, true, 6, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '120 days', 185)
ON CONFLICT (id) DO NOTHING;
