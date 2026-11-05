-- ==========================================================
-- Migration V2: Adiciona token_version na tabela users com default 1
-- Garante compatibilidade com bancos criados anteriormente sem a coluna
-- ==========================================================

ALTER TABLE IF EXISTS public.users 
    ADD COLUMN IF NOT EXISTS token_version integer NOT NULL DEFAULT 1;

UPDATE public.users 
    SET token_version = 1 
    WHERE token_version IS NULL;

