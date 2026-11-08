-- V21: Adiciona campos story_title e story_subtitle para personalização da grande reportagem histórica
ALTER TABLE public.stadium_info
ADD COLUMN IF NOT EXISTS story_title character varying(255) DEFAULT 'A História Viva da Boca do Lobo',
ADD COLUMN IF NOT EXISTS story_subtitle character varying(500) DEFAULT 'Uma viagem pelas memórias, pelo pioneirismo e pela paixão do caldeirão áureo-cerúleo';

UPDATE public.stadium_info
SET 
  story_title = COALESCE(story_title, 'A História Viva da Boca do Lobo'),
  story_subtitle = COALESCE(story_subtitle, 'Uma viagem pelas memórias, pelo pioneirismo e pela paixão do caldeirão áureo-cerúleo')
WHERE id = 1;
