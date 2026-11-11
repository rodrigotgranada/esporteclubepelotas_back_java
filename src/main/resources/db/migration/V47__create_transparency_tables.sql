-- V47__create_transparency_tables.sql

CREATE TABLE IF NOT EXISTS transparency_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size VARCHAR(50),
    is_published BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transparency_docs_category ON transparency_documents(category);
CREATE INDEX IF NOT EXISTS idx_transparency_docs_year ON transparency_documents(year);
CREATE INDEX IF NOT EXISTS idx_transparency_docs_published ON transparency_documents(is_published);

CREATE TABLE IF NOT EXISTS transparency_board_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    role_title VARCHAR(150) NOT NULL,
    board_type VARCHAR(100) NOT NULL,
    term_period VARCHAR(100),
    photo_url VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transparency_board_type ON transparency_board_members(board_type);
CREATE INDEX IF NOT EXISTS idx_transparency_board_active ON transparency_board_members(is_active);

-- Registrar módulo de transparência na tabela system_modules
INSERT INTO public.system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES (
    'm-transparency-001',
    'Transparência & Governança',
    'transparency',
    'Portal de prestação de contas, estatutos, balanços patrimoniais e conselhos do clube',
    true,
    NOW(),
    NOW()
) ON CONFLICT (slug) DO UPDATE
SET name = EXCLUDED.name,
    description = EXCLUDED.description,
    updated_at = NOW();
