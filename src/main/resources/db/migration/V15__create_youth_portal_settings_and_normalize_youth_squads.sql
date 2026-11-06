-- V15: Criação da tabela dedicada de configurações dinâmicas do Portal da Base (Pilares 0..4, Critérios de Peneiras e Contatos)
-- e normalização dos nomes de campeonatos da base e URLs sintéticas de fotos.

CREATE TABLE IF NOT EXISTS public.youth_portal_settings (
    id bigint NOT NULL PRIMARY KEY,
    pillars jsonb NOT NULL DEFAULT '[]'::jsonb,
    peneiras_criteria jsonb NOT NULL DEFAULT '[]'::jsonb,
    contact_title character varying(100) DEFAULT 'Departamento de Futebol de Base',
    contact_description character varying(500),
    contact_email character varying(150),
    contact_phone character varying(80),
    created_at timestamp(6) with time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp(6) with time zone NOT NULL DEFAULT NOW()
);

INSERT INTO public.youth_portal_settings (
    id,
    pillars,
    peneiras_criteria,
    contact_title,
    contact_description,
    contact_email,
    contact_phone,
    created_at,
    updated_at
)
VALUES (
    1,
    jsonb_build_array(
        jsonb_build_object(
            'title', 'DNA Competitivo Áureo-Cerúleo',
            'description', 'Ensinamos o respeito à história centenária do Pelotas, a dedicação intransigente em campo e o orgulho de defender as cores azul e amarela na Boca do Lobo.'
        ),
        jsonb_build_object(
            'title', 'Metodologia Tática & Intensidade',
            'description', 'Treinamentos modernos focados em rápida leitura de jogo, tomada de decisão sob pressão, transições velozes e alto rigor físico.'
        ),
        jsonb_build_object(
            'title', 'Acompanhamento Cidadão & Escolar',
            'description', 'O atleta do Pelotas deve manter bom rendimento escolar comprovado. Oferecemos suporte pedagógico e orientação para a vida fora das quatro linhas.'
        ),
        jsonb_build_object(
            'title', 'Integração com o Profissional',
            'description', 'Comunicação direta entre as comissões técnicas da base e a equipe principal, possibilitando treinos integrados e promoção gradual dos jovens talentos.'
        )
    ),
    jsonb_build_array(
        'Atestado médico cardiológico de aptidão física original (emitido nos últimos 6 meses).',
        'Documento de identidade oficial com foto (RG) e CPF do atleta e do responsável.',
        'Declaração de matrícula e frequência escolar ativa no ano letivo corrente.',
        'Material de treino próprio: chuteiras, caneleiras, meiões e calção (azul, amarelo, branco ou preto; proibido uniforme de rivais).',
        'Termo de autorização de participação preenchido e assinado pelos pais ou responsáveis legais.'
    ),
    'Departamento de Futebol de Base',
    'As datas oficiais de avaliações abertas e peneiras são divulgadas periodicamente nos canais oficiais do clube. Fique atento às redes sociais do Pelotas!',
    'base@esporteclubepelotas.com.br',
    NULL,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- Normaliza os nomes dos campeonatos da base criados no seed V7 para agrupar por competição limpa
UPDATE public.squads
SET season_name = 'Gauchão 2026'
WHERE id = '9cd6151f-c1ae-4009-af5e-ac3d9ead1725'
  AND season_name = 'Gauchão Sub-20 2026';

UPDATE public.squads
SET season_name = 'Copa FGF 2026'
WHERE id = '8d185cec-97f2-4924-a180-4b813e3efa4c'
  AND season_name = 'Copa FGF Sub-17 2026';

-- Limpa URLs sintéticas inexistentes do Unsplash nos atletas do seed para evitar 404 de imagem
UPDATE public.athletes
SET photo_url = NULL
WHERE photo_url LIKE 'https://images.unsplash.com/photo-1500%'
   OR photo_url LIKE 'https://images.unsplash.com/photo-1510%'
   OR photo_url LIKE 'https://images.unsplash.com/photo-1520%'
   OR photo_url LIKE 'https://images.unsplash.com/photo-1530%';
