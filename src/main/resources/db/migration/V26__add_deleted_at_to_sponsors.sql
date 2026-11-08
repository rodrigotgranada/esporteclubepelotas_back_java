-- V26: Adiciona coluna deleted_at para soft delete permanente de patrocinadores
ALTER TABLE sponsors ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE NULL;

CREATE INDEX IF NOT EXISTS idx_sponsors_deleted_at ON sponsors (deleted_at);

