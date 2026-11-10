-- Migration V37: Atualizar distintivo padrão de clubes adversários e partidas para silhueta/sombra genérica
-- Evita a exibição do distintivo do Pelotas como escudo de clubes rivais e adversários

UPDATE opponent_clubs
SET badge_url = '/images/default-club-badge.svg'
WHERE badge_url = '/images/pelotas-logo.png' 
   OR badge_url IS NULL 
   OR TRIM(badge_url) = '';

UPDATE matches
SET opponent_logo_url = '/images/default-club-badge.svg'
WHERE opponent_logo_url = '/images/pelotas-logo.png' 
   OR opponent_logo_url IS NULL 
   OR TRIM(opponent_logo_url) = '';

ALTER TABLE opponent_clubs 
    ALTER COLUMN badge_url SET DEFAULT '/images/default-club-badge.svg';
