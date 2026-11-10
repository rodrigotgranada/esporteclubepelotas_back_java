-- Migration V36: Adicionar slug e has_live_coverage à tabela matches para suporte ao Minuto a Minuto Ao Vivo

ALTER TABLE matches ADD COLUMN IF NOT EXISTS slug VARCHAR(255);
ALTER TABLE matches ADD COLUMN IF NOT EXISTS has_live_coverage BOOLEAN NOT NULL DEFAULT FALSE;

-- Atualizar partidas existentes com slugs amigáveis
UPDATE matches
SET slug = LOWER(
    REGEXP_REPLACE(
        CASE 
            WHEN home_away = 'HOME' THEN CONCAT('pelotas-vs-', opponent_name, '-', TO_CHAR(match_date, 'YYYY-MM-DD'))
            ELSE CONCAT(opponent_name, '-vs-pelotas-', TO_CHAR(match_date, 'YYYY-MM-DD'))
        END,
        '[^a-zA-Z0-9]+', '-', 'g'
    )
),
has_live_coverage = TRUE
WHERE slug IS NULL;

-- Criar índice único para o slug das partidas ativas
CREATE UNIQUE INDEX IF NOT EXISTS idx_matches_slug 
ON matches (slug) 
WHERE deleted_at IS NULL;
