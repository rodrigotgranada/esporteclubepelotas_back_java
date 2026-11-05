-- Migration V4: Add staff support to athletes and squad members

-- 1. Relax preferred_foot not null constraint for staff members
ALTER TABLE public.athletes ALTER COLUMN preferred_foot DROP NOT NULL;

-- 2. Add staff_role column to athletes
ALTER TABLE public.athletes ADD COLUMN IF NOT EXISTS staff_role character varying(100);

-- 3. Update athletes_type_check to allow 'STAFF'
ALTER TABLE public.athletes DROP CONSTRAINT IF EXISTS athletes_type_check;
ALTER TABLE public.athletes ADD CONSTRAINT athletes_type_check
    CHECK (((athlete_type)::text = ANY ((ARRAY['PROFESSIONAL'::character varying, 'YOUTH'::character varying, 'STAFF'::character varying])::text[])));

-- 4. Relax jersey_number in squad_members for staff
ALTER TABLE public.squad_members ALTER COLUMN jersey_number DROP NOT NULL;

-- 5. Update squad_members_sector_check to allow 'STAFF'
ALTER TABLE public.squad_members DROP CONSTRAINT IF EXISTS squad_members_sector_check;
ALTER TABLE public.squad_members ADD CONSTRAINT squad_members_sector_check
    CHECK (((sector)::text = ANY ((ARRAY['GOALKEEPER'::character varying, 'DEFENDER'::character varying, 'MIDFIELDER'::character varying, 'FORWARD'::character varying, 'STAFF'::character varying])::text[])));
