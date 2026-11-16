ALTER TABLE public.idols
    ADD COLUMN IF NOT EXISTS is_in_memoriam boolean NOT NULL DEFAULT false;

UPDATE public.idols
SET is_in_memoriam = true
WHERE death_date IS NOT NULL;

