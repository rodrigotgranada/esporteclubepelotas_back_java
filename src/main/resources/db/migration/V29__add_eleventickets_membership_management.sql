-- Migration V29: Suporte à Gestão e Sincronização de Sócios ElevenTickets
-- Adiciona campos de Quadro Social e controle de sincronização na tabela users
-- Cria tabela de cache de sócios para auto-vínculo de novos cadastros

-- 1. Campos de Quadro Social e Sincronização na tabela users
ALTER TABLE public.users 
    ADD COLUMN IF NOT EXISTS eleventickets_id VARCHAR(50),
    ADD COLUMN IF NOT EXISTS is_socio BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS socio_plan_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS socio_status VARCHAR(30) DEFAULT 'INACTIVE',
    ADD COLUMN IF NOT EXISTS socio_sync_mode VARCHAR(30) NOT NULL DEFAULT 'AUTO',
    ADD COLUMN IF NOT EXISTS socio_override_reason TEXT,
    ADD COLUMN IF NOT EXISTS socio_override_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS socio_since DATE,
    ADD COLUMN IF NOT EXISTS socio_last_synced_at TIMESTAMP WITH TIME ZONE;

-- 2. Índices de busca rápida
CREATE INDEX IF NOT EXISTS idx_users_eleventickets_id ON public.users(eleventickets_id);
CREATE INDEX IF NOT EXISTS idx_users_is_socio ON public.users(is_socio);
CREATE INDEX IF NOT EXISTS idx_users_socio_status ON public.users(socio_status);

-- 3. Tabela de Cache de Sócios da ElevenTickets (para pré-autorizar novos cadastros)
CREATE TABLE IF NOT EXISTS public.eleventickets_members_cache (
    id VARCHAR(36) PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    eleventickets_id VARCHAR(50) NOT NULL,
    full_name VARCHAR(150),
    email VARCHAR(150),
    plan_name VARCHAR(100),
    status VARCHAR(30) DEFAULT 'ACTIVE',
    imported_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_et_cache_cpf ON public.eleventickets_members_cache(cpf);
CREATE INDEX IF NOT EXISTS idx_et_cache_eleventickets_id ON public.eleventickets_members_cache(eleventickets_id);
CREATE INDEX IF NOT EXISTS idx_et_cache_status ON public.eleventickets_members_cache(status);

