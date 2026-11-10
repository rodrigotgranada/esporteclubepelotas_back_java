-- ==============================================================================
-- Migration V38: Criação da tabela de eventos de partida (Minuto a Minuto Ao Vivo)
-- ==============================================================================

-- 1. Novos campos em matches para controle do Minuto a Minuto
ALTER TABLE matches 
    ADD COLUMN IF NOT EXISTS current_period VARCHAR(30) NOT NULL DEFAULT 'PRE_MATCH',
    ADD COLUMN IF NOT EXISTS current_minute VARCHAR(20) NOT NULL DEFAULT '0''',
    ADD COLUMN IF NOT EXISTS timer_started_at TIMESTAMP WITH TIME ZONE;

-- 2. Tabela de Eventos de Partida (Lance a Lance)
CREATE TABLE IF NOT EXISTS match_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id UUID NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
    minute INT NOT NULL DEFAULT 0,
    minute_extra INT,
    period VARCHAR(30) NOT NULL DEFAULT 'FIRST_HALF',
    event_type VARCHAR(50) NOT NULL,
    team VARCHAR(20) NOT NULL DEFAULT 'NEUTRAL',
    player_name VARCHAR(150),
    player_out_name VARCHAR(150),
    title VARCHAR(150) NOT NULL,
    description TEXT,
    score_pelotas_moment INT,
    score_opponent_moment INT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_match_events_match_order 
ON match_events (match_id, display_order ASC, minute ASC, created_at ASC);

CREATE INDEX IF NOT EXISTS idx_match_events_match_period 
ON match_events (match_id, period);
