-- Migration V40: Normalização Relacional e Vínculo por ID (Single Source of Truth)
-- Elimina duplicação de dados textuais e estabelece vínculos canônicos por Foreign Key

-- 1. Normalização de Squads (Elencos) -> Vínculo com Championships e Treinador (Athletes)
ALTER TABLE public.squads
    ADD COLUMN IF NOT EXISTS championship_id UUID REFERENCES public.championships(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS coach_id UUID REFERENCES public.athletes(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_squads_championship_id ON public.squads(championship_id);
CREATE INDEX IF NOT EXISTS idx_squads_coach_id ON public.squads(coach_id);

-- Vínculo retroativo de campeonatos em squads
UPDATE public.squads s
SET championship_id = c.id
FROM public.championships c
WHERE s.championship_id IS NULL
  AND c.deleted_at IS NULL
  AND LOWER(TRIM(s.season_name)) ILIKE CONCAT('%', LOWER(TRIM(c.name)), '%');

-- Vínculo retroativo de treinadores em squads
UPDATE public.squads s
SET coach_id = a.id
FROM public.athletes a
WHERE s.coach_id IS NULL
  AND s.coach_name IS NOT NULL
  AND s.coach_name <> ''
  AND a.is_active = true
  AND LOWER(TRIM(s.coach_name)) = LOWER(TRIM(a.name));

-- 2. Normalização de Club Kits (Uniformes) -> Vínculo com Championships
ALTER TABLE public.club_kits
    ADD COLUMN IF NOT EXISTS championship_id UUID REFERENCES public.championships(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_club_kits_championship_id ON public.club_kits(championship_id);

-- Vínculo retroativo de campeonatos em club_kits
UPDATE public.club_kits k
SET championship_id = c.id
FROM public.championships c
WHERE k.championship_id IS NULL
  AND k.deleted_at IS NULL
  AND c.deleted_at IS NULL
  AND (
      LOWER(TRIM(k.competition)) = LOWER(TRIM(c.name))
      OR LOWER(TRIM(k.competition)) ILIKE CONCAT('%', LOWER(TRIM(c.name)), '%')
  );

-- 3. Normalização de Match Events (Minuto a Minuto) -> Vínculo com Atletas do Pelotas
ALTER TABLE public.match_events
    ADD COLUMN IF NOT EXISTS player_athlete_id UUID REFERENCES public.athletes(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS player_out_athlete_id UUID REFERENCES public.athletes(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_match_events_player_athlete ON public.match_events(player_athlete_id);
CREATE INDEX IF NOT EXISTS idx_match_events_player_out_athlete ON public.match_events(player_out_athlete_id);
