-- Migration V39: Sincronização Dinâmica e Vínculo com Clubes Adversários (opponent_clubs)
-- Adiciona vínculo canônico com opponent_clubs e sincroniza os distintivos das partidas

-- 1. Adicionar coluna opponent_club_id na tabela matches
ALTER TABLE matches ADD COLUMN IF NOT EXISTS opponent_club_id UUID REFERENCES opponent_clubs(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_matches_opponent_club_id ON matches(opponent_club_id) WHERE deleted_at IS NULL;

-- 2. Sincronizar vínculos e distintivos existentes a partir da tabela opponent_clubs
UPDATE matches m
SET opponent_club_id = c.id,
    opponent_logo_url = CASE 
        WHEN (c.badge_url IS NOT NULL AND c.badge_url <> '' AND c.badge_url <> '/images/default-club-badge.svg' AND c.badge_url <> '/images/pelotas-logo.png')
        THEN c.badge_url
        ELSE m.opponent_logo_url
    END
FROM opponent_clubs c
WHERE m.deleted_at IS NULL
  AND c.deleted_at IS NULL
  AND (
      LOWER(TRIM(m.opponent_name)) = LOWER(TRIM(c.name))
      OR LOWER(TRIM(m.opponent_name)) = LOWER(TRIM(c.short_name))
  );
