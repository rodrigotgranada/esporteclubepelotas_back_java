-- V14: Adiciona colunas para configurações dinâmicas das Categorias de Base (Pilares, Peneiras e Contatos)
ALTER TABLE public.settings
ADD COLUMN IF NOT EXISTS youth_pillars JSONB DEFAULT '[]'::jsonb,
ADD COLUMN IF NOT EXISTS youth_tryouts JSONB DEFAULT '{}'::jsonb;

-- Atualiza as configurações existentes com os valores padrão oficiais das Crias da Boca
UPDATE public.settings
SET 
    youth_pillars = jsonb_build_array(
        jsonb_build_object(
            'title', 'DNA Áureo-Cerúleo',
            'description', 'Formação com raça, determinação e compromisso com o manto azul e amarelo do Lobo.'
        ),
        jsonb_build_object(
            'title', 'Desenvolvimento Integral',
            'description', 'Acompanhamento escolar, disciplina, valores éticos e cidadania para os jovens atletas.'
        ),
        jsonb_build_object(
            'title', 'Metodologia Moderna',
            'description', 'Treinamento tático e físico alinhado às exigências do futebol profissional.'
        ),
        jsonb_build_object(
            'title', 'Transição para o Profissional',
            'description', 'Integração direta com o elenco principal e oportunidades reais em jogos oficiais.'
        )
    ),
    youth_tryouts = jsonb_build_object(
        'requirements', jsonb_build_array(
            'Estar regularmente matriculado e com frequência comprovada na rede de ensino',
            'Atestado médico de aptidão física específico para futebol de alto rendimento (validade de 6 meses)',
            'Documento oficial com foto (RG ou Certidão de Nascimento com RG) e CPF',
            'Termo de autorização assinado pelos pais ou responsáveis legais com firma reconhecida',
            'Eletrocardiograma com laudo médico especializado atualizado'
        ),
        'contactEmail', 'base@esporteclubepelotas.com.br',
        'contactPhone', '(53) 99999-1908',
        'coordinatorName', 'Coordenação das Categorias de Base'
    )
WHERE youth_pillars IS NULL OR youth_pillars = '[]'::jsonb;

