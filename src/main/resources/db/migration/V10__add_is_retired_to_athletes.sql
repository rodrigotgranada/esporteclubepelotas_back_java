-- Migration V10: Add is_retired column to athletes table
ALTER TABLE public.athletes
    ADD COLUMN IF NOT EXISTS is_retired boolean DEFAULT false NOT NULL;

CREATE INDEX IF NOT EXISTS idx_athletes_retired ON public.athletes USING btree (is_active, is_retired);

