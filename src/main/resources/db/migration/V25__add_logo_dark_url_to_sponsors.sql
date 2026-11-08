-- V25: Adicionar campo logo_dark_url para suporte a temas claro e escuro independentes
ALTER TABLE sponsors ADD COLUMN IF NOT EXISTS logo_dark_url TEXT;

