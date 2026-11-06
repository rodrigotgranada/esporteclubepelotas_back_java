-- V6: Atualiza as paletas dos temas padrão para identidade 100% Áureo-Cerúlea
-- O tema escuro passa a utilizar Azul Marinho Noite Profundo (#0b1a32 / #112344) em vez de preto
-- O tema claro passa a utilizar Amarelo bem clarinho / Creme Marfim (#fefdf0) em vez de branco/cinza

UPDATE public.themes
SET 
    dark_palette = jsonb_build_object(
        'primary', '#fbbf24',
        'secondary', '#1e3a8a',
        'buttonSecondary', '#1e3a8a',
        'background', '#0b1a32',
        'surface', '#112344',
        'header', '#0d1e3a',
        'card', '#112344',
        'drawer', '#0d1e3a',
        'table', '#112344',
        'textPrimary', '#f8fafc',
        'textSecondary', '#94a3b8',
        'border', '#1c355f',
        'success', '#22c55e',
        'error', '#ea580c',
        'warning', '#f59e0b'
    ),
    light_palette = jsonb_build_object(
        'primary', '#fbbf24',
        'secondary', '#1e3a8a',
        'buttonSecondary', '#1e3a8a',
        'background', '#fefdf0',
        'surface', '#ffffff',
        'header', '#1e3a8a',
        'card', '#ffffff',
        'drawer', '#ffffff',
        'table', '#ffffff',
        'textPrimary', '#0f172a',
        'textSecondary', '#475569',
        'border', '#fef08a',
        'success', '#22c55e',
        'error', '#ea580c',
        'warning', '#f59e0b'
    ),
    updated_at = NOW()
WHERE is_default = true OR is_active = true OR name = 'Padrão E.C. Pelotas';
