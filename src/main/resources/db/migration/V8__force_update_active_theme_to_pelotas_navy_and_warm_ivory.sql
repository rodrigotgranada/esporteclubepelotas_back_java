-- V8: Atualiza forçadamente o tema padrão e todos os temas ativos para a autêntica identidade Áureo-Cerúlea do Pelotas
-- Dark Mode: Azul Marinho Noite Profundo (#0b1a32), superfícies navy (#112344) e bordas cerúleas (#1c355f)
-- Light Mode: Marfim Áureo Suave (#fefdf0), superfícies brancas (#ffffff) e bordas douradas (#fef08a)

-- 1. Garante que o tema padrão existe caso a tabela esteja vazia
INSERT INTO public.themes (
    id, name, is_active, is_default, is_campaign, created_at, updated_at, dark_palette, light_palette
)
VALUES (
    1, 'Padrão E.C. Pelotas', true, true, false, NOW(), NOW(),
    jsonb_build_object(
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
    jsonb_build_object(
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
    )
)
ON CONFLICT (name) DO NOTHING;

-- 2. Atualiza todos os temas padrão ou ativos para garantir que as novas cores sejam aplicadas
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
