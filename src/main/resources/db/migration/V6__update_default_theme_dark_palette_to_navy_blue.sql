-- V6: Atualiza as paletas dos temas padrão para identidade 100% Áureo-Cerúlea
-- O tema escuro passa a utilizar Azul Marinho Noite Profundo (#070e1c / #0d192f) em vez de preto puro
-- O tema claro passa a utilizar off-white elegante (#f8fafc)

UPDATE public.themes
SET 
    dark_palette = jsonb_build_object(
        'primary', '#fbbf24',
        'secondary', '#1e3a8a',
        'buttonSecondary', '#1e3a8a',
        'background', '#070e1c',
        'surface', '#0d192f',
        'header', '#091427',
        'card', '#0d192f',
        'drawer', '#091325',
        'table', '#0d192f',
        'textPrimary', '#f8fafc',
        'textSecondary', '#94a3b8',
        'border', '#1c2b48',
        'success', '#22c55e',
        'error', '#ea580c',
        'warning', '#f59e0b'
    ),
    light_palette = jsonb_build_object(
        'primary', '#fbbf24',
        'secondary', '#1e3a8a',
        'buttonSecondary', '#1e3a8a',
        'background', '#f8fafc',
        'surface', '#ffffff',
        'header', '#1e3a8a',
        'card', '#ffffff',
        'drawer', '#ffffff',
        'table', '#ffffff',
        'textPrimary', '#0f172a',
        'textSecondary', '#475569',
        'border', '#e2e8f0',
        'success', '#22c55e',
        'error', '#ea580c',
        'warning', '#f59e0b'
    ),
    updated_at = NOW()
WHERE is_default = true OR is_active = true OR name = 'Padrão E.C. Pelotas';
