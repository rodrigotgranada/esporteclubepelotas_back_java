-- V7: Permite capa nula em notícias e alimenta o banco com dados ricos de demonstração
-- Notícias (com e sem capa, gerais e da base), 3 Campeonatos Profissionais Masculinos (50 atletas),
-- 2 Campeonatos Femininos (30 atletas) e 2 Campeonatos de Base (30 atletas)

-- 1. Relaxa a obrigatoriedade da imagem de capa em matérias
ALTER TABLE public.news_articles ALTER COLUMN cover_image DROP NOT NULL;

-- 2. Garante papéis e usuário padrão de redação
INSERT INTO public.roles (id, name, label, description, is_immutable, created_at, updated_at)
VALUES 
    ('role-owner-001', 'OWNER', 'Proprietário', 'Acesso total irrestrito', true, NOW(), NOW()),
    ('role-admin-001', 'ADMIN', 'Administrador', 'Acesso administrativo', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.users (
    id, first_name, last_name, email, email_verified, failed_login_attempts, 
    is_active, must_change_password, password_hash, cpf, status, token_version, role_id, created_at, updated_at
)
VALUES (
    'user-assessoria-001', 'Assessoria', 'Pelotas', 'assessoria@ecpelotas.com.br', true, 0,
    true, false, '$2a$10$7EqJtq98hPqEX7fNZaFWoO0L/jVzQZpM7d7YFk5fO8L6Vb0X6qZae',
    '000.000.000-01', 'ACTIVE', 1,
    (SELECT id FROM public.roles WHERE name IN ('ADMIN', 'OWNER') LIMIT 1),
    NOW(), NOW()
)
ON CONFLICT (email) DO NOTHING;

-- 3. Categorias de Notícias
INSERT INTO public.news_categories (id, name, slug, description, display_order, is_active, created_at, updated_at)
VALUES
    ('88ca86a9-d055-434d-a322-0a0ca9447ce8', 'Futebol Profissional', 'futebol-profissional', 'Cobertura completa do time profissional do Lobo', 1, true, NOW(), NOW()),
    ('db662f02-f0b6-48da-a7c7-4cf06b2600ba', 'Categorias de Base', 'categorias-de-base', 'Notícias das Crias da Boca (Sub-20, Sub-17 e Sub-15)', 2, true, NOW(), NOW()),
    ('68cba24c-0b6e-4834-a858-37741472a75f', 'Institucional', 'institucional', 'Comunicados oficiais, diretoria e patrimônio centenário', 3, true, NOW(), NOW()),
    ('634d611f-95b1-4eca-a202-0f891b06632c', 'Quadro Social', 'quadro-social', 'Vantagens, ingressos e novidades para sócios áureo-cerúleos', 4, true, NOW(), NOW())
ON CONFLICT (slug) DO NOTHING;

-- 4. Inserção de Matérias
INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    'cf05ecc0-b3f5-477d-ae9e-85957bb9982c', 'Pelotas vence clássico Farroupilha por 2 a 0 na Boca do Lobo e assume liderança', 'Com grande apoio da torcida na Avenida Bento Gonçalves, o Lobo dominou as ações e garantiu vitória convincente no segundo tempo.', 'pelotas-vence-classico-farroupilha-por-2-a-0-na-boca-do-lobo',
    'Vitória imponente do Lobo na Boca do Lobo garante três pontos cruciais na disputa pela ponta da tabela.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Vitória imponente do Lobo na Boca do Lobo garante três pontos cruciais na disputa pela ponta da tabela."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80', 'Festa áureo-cerúlea na Boca do Lobo com mais de 7 mil torcedores presentes',
    'Rodrigo Granada', 'Assessoria / E.C. Pelotas', 'PUBLISHED', 'MAIN', true, false,
    false, true, true, true, 342,
    '["Gauchão","ClássicoFarroupilha","BocaDoLobo","Liderança"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'futebol-profissional' LIMIT 1),
    NOW() - INTERVAL '0 hours', NOW() - INTERVAL '0 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    '0f8d87e3-a1c6-4229-a719-3d90d8b4a383', 'Quadro Social lança campanha de anuidade com benefícios exclusivos para sócios', 'Torcedores que quitarem a anuidade antecipada garantem camisa oficial comemorativa e acesso livre a todos os jogos de 2026.', 'quadro-social-lanca-campanha-de-anuidade-com-beneficios-exclusivos',
    'Novos planos trazem vantagens na compra de produtos oficiais, prioridade na compra de ingressos e benefícios com parceiros locais.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Novos planos trazem vantagens na compra de produtos oficiais, prioridade na compra de ingressos e benefícios com parceiros locais."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=1200&q=80', 'Secretaria do clube preparada para atender torcedores áureo-cerúleos',
    'Assessoria de Comunicação', 'Arquivo / ECP', 'PUBLISHED', 'SECONDARY', true, false,
    false, true, true, true, 189,
    '["QuadroSocial","SejaSócio","Alcatéia","BocaDoLobo"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'quadro-social' LIMIT 1),
    NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    'bdde64ba-775d-4a38-afa3-5e9d3168d101', 'Crias da Boca: Sub-20 do Pelotas goleia em clássico regional pela Copa FGF', 'Com atuação brilhante no segundo tempo, jovens talentos áureo-cerúleos aplicaram 4 a 1 e asseguraram classificação antecipada.', 'crias-da-boca-sub-20-do-pelotas-goleia-em-classico-regional',
    'Sub-20 áureo-cerúleo mostra força da base e avança de fase com 100% de aproveitamento na Boca do Lobo.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Sub-20 áureo-cerúleo mostra força da base e avança de fase com 100% de aproveitamento na Boca do Lobo."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1517649763962-0c623266ddc0?auto=format&fit=crop&w=1200&q=80', 'Comemoração dos atletas da base após o terceiro gol no clássico',
    'Lucas Ferreira', 'Divulgação / Base ECP', 'PUBLISHED', 'SECONDARY', true, false,
    true, true, true, true, 215,
    '["CategoriasDeBase","Sub20","CriasDaBoca","CopaFGF"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'categorias-de-base' LIMIT 1),
    NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    '45a72663-8d6f-429a-af89-dc58db640192', 'Departamento de Formação abre inscrições para peneiras oficiais do Sub-17 e Sub-15', 'Testes ocorrem no Paráque Lobo ao longo da próxima semana. Atletas nascidos entre 2009 e 2012 podem se inscrever gratuitamente.', 'departamento-de-formacao-abre-inscricoes-para-peneiras-oficiais',
    'Avaíliações técnicas gratuitas visam compor os elencos de base para as disputas estaduais da temporada.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Avaíliações técnicas gratuitas visam compor os elencos de base para as disputas estaduais da temporada."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1526232761682-d26e03ac148e?auto=format&fit=crop&w=1200&q=80', 'Campos de treinamento do Paráque Lobo sediarão as avaliações',
    'Assessoria de Base', 'Base / ECP', 'PUBLISHED', 'NONE', false, false,
    true, false, true, true, 167,
    '["Peneiras","Testes","Base","Sub17","Sub15"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'categorias-de-base' LIMIT 1),
    NOW() - INTERVAL '18 hours', NOW() - INTERVAL '18 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    '0560747d-7282-4373-aabb-10f4a976df61', 'Metodologia Áureo-Cerúlea: Coordenador detalha os 4 pilares de formação dos jovens atletas', 'Trabalho foca em intensidade tática, disciplina comportamental, preparação física progressiva e identificação com as cores do clube.', 'metodologia-aureo-cerulea-coordenador-detalha-4-pilares-de-formacao',
    'Apresentação do plano diretor das categorias de base para os próximos três anos na Boca do Lobo.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Apresentação do plano diretor das categorias de base para os próximos três anos na Boca do Lobo."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?auto=format&fit=crop&w=1200&q=80', 'Treinamento integrado da comissão técnica e jovens atletas',
    'Assessoria de Imprensa', 'Base / ECP', 'PUBLISHED', 'NONE', false, false,
    true, false, true, true, 142,
    '["Metodologia","Formação","DNAAzulEAmarelo","Base"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'categorias-de-base' LIMIT 1),
    NOW() - INTERVAL '24 hours', NOW() - INTERVAL '24 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    '2a72d00c-b9c6-4883-a071-2452ac6c7f57', 'Joia do Sub-20 assina primeiro contrato profissional e integra pré-temporada principal', 'Meio-campista Matheusinho, destaque da Copa FGF Sub-20, firma vínculo de três anos com multa rescisória e integração ao elenco profissional.', 'joia-do-sub-20-assina-primeiro-contrato-profissional-com-o-pelotas',
    'Transição da base para o profissional valoriza talentos lapidados no Paráque Lobo.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Transição da base para o profissional valoriza talentos lapidados no Paráque Lobo."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80', 'Matheusinho ao lado da diretoria durante a assinatura do contrato',
    'Redação Áureo-Cerúlea', 'Assessoria / E.C. Pelotas', 'PUBLISHED', 'NONE', false, false,
    true, true, true, true, 298,
    '["CriasDaBoca","ContratoProfissional","Futuro","Sub20"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'categorias-de-base' LIMIT 1),
    NOW() - INTERVAL '30 hours', NOW() - INTERVAL '30 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    '1d0d0ae1-6987-4750-af66-f4c914515ec3', 'Nota Oficial: Esporte Clube Pelotas se manifesta sobre obras nas arquibancadas da Boca do Lobo', 'Diretoria Executiva detalha cronograma de melhorias estruturais, pintura e novos acessos para o conforto dos torcedores.', 'nota-oficial-reformas-e-melhorias-nas-arquibancadas-da-boca-do-lobo',
    'Comunicado detalha os reparos concluídos no setor de cadeiras e arquibancada geral do Estádio Boca do Lobo.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Comunicado detalha os reparos concluídos no setor de cadeiras e arquibancada geral do Estádio Boca do Lobo."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, NULL, NULL,
    'Diretoria Executiva', NULL, 'PUBLISHED', 'NONE', false, false,
    false, true, true, true, 412,
    '["NotaOficial","BocaDoLobo","Patrimônio","Estádio"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'institucional' LIMIT 1),
    NOW() - INTERVAL '36 hours', NOW() - INTERVAL '36 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    'fed1678f-46f0-4957-a9d1-2154f39c164e', 'Comunicado da Diretoria: Horários de atendimento e funcionamento da Secretaria durante o feriado', 'Atendimento presencial e entrega de carteirinhas de sócio terão horários especiais nesta sexta-feira e sábado.', 'comunicado-horarios-de-atendimento-secretaria-durante-feriado',
    'Informações de atendimento ao torcedor e canais de contato digital via WhatsApp e portal oficial.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Informações de atendimento ao torcedor e canais de contato digital via WhatsApp e portal oficial."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, NULL, NULL,
    'Secretaria Geral', NULL, 'PUBLISHED', 'NONE', false, false,
    false, true, true, true, 153,
    '["Comunicado","Secretaria","Atendimento","Sócios"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'institucional' LIMIT 1),
    NOW() - INTERVAL '42 hours', NOW() - INTERVAL '42 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    'b47af209-cf6d-4c92-adb4-d64245aa9230', 'Comandante Leocir Dall''Astra projeta estreia no Gauchão Série A: "Foco total na intensidade"', 'Técnico áureo-cerúleo elogia entrega do plantel durante os trabalhos com bola e confirma time base para o confronto de abertura.', 'comandante-leocir-dall-astra-projeta-estreia-no-gauchao-serie-a',
    'Entrevista coletiva do treinador destaca preparação física, entrosamento e postura ofensiva para a temporada.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Entrevista coletiva do treinador destaca preparação física, entrosamento e postura ofensiva para a temporada."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1543326727-cf6c39e8f84c?auto=format&fit=crop&w=1200&q=80', 'Treinador orienta os atletas no gramado da Boca do Lobo',
    'Rodrigo Granada', 'Assessoria / ECP', 'PUBLISHED', 'NONE', false, false,
    false, true, true, true, 280,
    '["LeocirDallAstra","Gauchão","Treinador","Futebol"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'futebol-profissional' LIMIT 1),
    NOW() - INTERVAL '48 hours', NOW() - INTERVAL '48 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    '1e4bd82e-cf4c-4070-a61d-0710f64e5ad0', 'Lobo anuncia pacote de reforços para a disputa do Campeonato Brasileiro Série D', 'Zagueiro com bagagem na Série B e volante com passagens pela elite do futebol paulista chegam para encorpar o elenco.', 'lobo-anuncia-pacote-de-reforcos-para-campeonato-brasileiro-serie-d',
    'Apresentação oficial dos novos contratados acontece nesta quinta-feira na sala de imprensa do clube.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Apresentação oficial dos novos contratados acontece nesta quinta-feira na sala de imprensa do clube."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1489944440615-453fc2b6a9a9?auto=format&fit=crop&w=1200&q=80', 'Novos contratados realizam exames médicos e físicos no clube',
    'Redação', 'Assessoria / E.C. Pelotas', 'PUBLISHED', 'NONE', false, false,
    false, true, true, true, 310,
    '["Reforços","SérieD","MercadoDaBola","Futebol"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'futebol-profissional' LIMIT 1),
    NOW() - INTERVAL '54 hours', NOW() - INTERVAL '54 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    'a16ebec7-6202-46ae-aa6a-99ad259d5635', 'Léobas da Boca iniciam pré-temporada visando o Gauchão Feminino 2026', 'Equipe feminina do Pelotas realizou avaliações biométricas e primeiros treinamentos no gramado principal com novo comando técnico.', 'lobas-da-boca-iniciam-pre-temporada-visando-o-gauchao-feminino-2026',
    'Comandadas por Cristiane Gambaróé, atletas buscam campanha histórica no campeonato estadual.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Comandadas por Cristiane Gambaróé, atletas buscam campanha histórica no campeonato estadual."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=1200&q=80', 'Plantel feminino reunido no gramado da Boca do Lobo',
    'Assessoria Feminina', 'Divulgação / ECP', 'PUBLISHED', 'NONE', false, false,
    false, true, true, true, 220,
    '["FutebolFeminino","LéobasDaBoca","GauchãoFeminino","ElasNoFutebol"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'futebol-profissional' LIMIT 1),
    NOW() - INTERVAL '60 hours', NOW() - INTERVAL '60 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO public.news_articles (
    id, title, subtitle, slug, summary, content, cover_image, cover_caption,
    reporter_name, photographer_name, status, featured_slot, is_featured, is_pinned,
    is_youth, show_on_homepage, allow_comments, allow_reactions, views_count,
    tags, gallery, reactions, author_id, category_id, published_at, created_at, updated_at
)
VALUES (
    'c32c774e-bb79-4133-ac50-07c36209a03e', 'Memorial Áureo-Cerúleo recebe visita especial de escolas municipais de Pelotas', 'Mais de 120 crianças conheceram a sala de troféus, o túnel de acesso e a rica história centenária do Lobo da Boca do Lobo.', 'memorial-aureo-ceruleo-recebe-visita-especial-de-escolas-municipais',
    'Ação social aproxima novas gerações da história e da paixão pelo maior clube do interior gaúcho.', '{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Ação social aproxima novas gerações da história e da paixão pelo maior clube do interior gaúcho."}]},{"type":"paragraph","content":[{"type":"text","text":"O Esporte Clube Pelotas segue trabalhando intensamente para honrar a sua torcida centenária e buscar as melhores colocações em todas as competições que disputa."}]}]}'::jsonb, 'https://images.unsplash.com/photo-1577495508048-b635879837f1?auto=format&fit=crop&w=1200&q=80', 'Alunos encantados com as taças históricas do Pelotas',
    'Departamento Social', 'Assessoria / ECP', 'PUBLISHED', 'NONE', false, false,
    false, true, true, true, 195,
    '["Memorial","História","Educação","PelotasParáaTodos"]'::jsonb, '[]'::jsonb, '{"cheer": 15, "fire": 22, "applause": 18, "heart": 40}'::jsonb,
    (SELECT id FROM public.users LIMIT 1),
    (SELECT id FROM public.news_categories WHERE slug = 'institucional' LIMIT 1),
    NOW() - INTERVAL '66 hours', NOW() - INTERVAL '66 hours', NOW()
)
ON CONFLICT (slug) DO NOTHING;

-- 5. Inserção de Campeonatos / Elencos (Squads)
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('6f2d2d79-6f18-4c13-a69d-772067e59993', 'Gauchão Série A 2026', 'PROFESSIONAL', 2026, true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('8f517076-7b8e-4544-a6bb-13915018e382', 'Copa FGF 2026', 'PROFESSIONAL', 2026, false, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'Campeonato Brasileiro Série D 2026', 'PROFESSIONAL', 2026, false, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'Gauchão Feminino 2026', 'FEMININE', 2026, true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'Copa Gaúcha Feminina 2026', 'FEMININE', 2026, false, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('9cd6151f-c1ae-4009-af5e-ac3d9ead1725', 'Gauchão Sub-20 2026', 'SUB_20', 2026, true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squads (id, season_name, category, year, is_current, is_active, created_at, updated_at)
VALUES ('8d185cec-97f2-4924-a180-4b813e3efa4c', 'Copa FGF Sub-17 2026', 'SUB_17', 2026, true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 6. Inserção de Atletas Profissionais Masculinos (50 atletas)
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'fa38d8f4-0890-4ca4-a689-20abbabf0ab9', 'Jacsson Antonio Wichnovski', 'Jacsson', 'atleta-jacsson-0',
    'MALE', 'PROFESSIONAL', 'Goleiro', 189, 86, '1994-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500000000000?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Internacional","São Caetano","Ypiranga"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'dc6e0460-0c8c-4dce-a256-55f3c3ddfa35', 'Rodrigo Josviaki', 'Rodrigo', 'atleta-rodrigo-1',
    'MALE', 'PROFESSIONAL', 'Goleiro', 191, 88, '1995-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500001234567?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Juventude","Novo Hamburgo"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd9e2c2b0-4cfa-4b0f-a021-224e60a9d9eb', 'Enzo Gabriel Silva', 'Enzo', 'atleta-enzo-2',
    'MALE', 'PROFESSIONAL', 'Goleiro', 188, 83, '2001-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500002469134?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Caxias","Pelotas Base"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c8e93acd-c7f7-497e-a027-6d616c4d7e32', 'Alan Thiago Becker', 'Alan', 'atleta-alan-3',
    'MALE', 'PROFESSIONAL', 'Goleiro', 192, 89, '1998-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500003703701?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Chapecoense","São Luiz"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd2614387-c355-40cf-a50c-c8f658ffad8e', 'Gabriel Bonet', 'Bonet', 'atleta-bonet-4',
    'MALE', 'PROFESSIONAL', 'Goleiro', 187, 82, '2000-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500004938268?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Passo Fundo","Glória"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'b505de2b-5f28-46e7-a3cd-f6f66853ff48', 'Lucas Maciel', 'Maciel', 'atleta-maciel-5',
    'MALE', 'PROFESSIONAL', 'Goleiro', 186, 80, '2002-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500006172835?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Brasil-FAR","Avenida"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'f78ca3bd-5c29-4944-a03f-dea74a4917d0', 'Heverton Durães Coutinho Alves', 'Heverton', 'atleta-heverton-6',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 188, 84, '1988-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500007407402?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Juventude","Fortaleza","Cuiabáá"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'e430d24a-f7ab-40b8-a404-00e910341402', 'Leonardo Kanu', 'Léo Kanu', 'atleta-l-o-kanu-7',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 194, 91, '1988-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500008641969?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Náutico","Ponte Preta","Caxias"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '5033b2c3-fadd-459c-aab7-4df27f4162ad', 'Bruno Cardoso', 'Cardoso', 'atleta-cardoso-8',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 186, 82, '1995-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500009876536?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["São Paulo-RG","Aimoréé"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '26c4c0ce-fc3f-4d16-acb4-aaf1fb0ca1b5', 'Yuri Oliveira Silva', 'Yuri', 'atleta-yuri-9',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 187, 83, '1997-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500011111103?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Botafogo-SP","Inter-SM"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'ebe865cc-db0d-4700-ad48-cfc0c16e0b5b', 'João Marcus Silveira', 'João Marcus', 'atleta-jo-o-marcus-10',
    'MALE', 'PROFESSIONAL', 'Lateral-Esquerdo', 180, 76, '1996-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500012345670?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["São Luiz","Caxias","Aimoréé"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c890fdaa-11e6-403d-af71-7569e5e9f9d2', 'Nicolas Schmitt', 'Nicolas', 'atleta-nicolas-11',
    'MALE', 'PROFESSIONAL', 'Lateral-Esquerdo', 178, 74, '2000-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500013580237?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Juventude","Grêmio"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'bb2423da-73ea-4bef-a143-273acd812419', 'Douglas Silva', 'D. Silva', 'atleta-d--silva-12',
    'MALE', 'PROFESSIONAL', 'Lateral-Direito', 181, 77, '1996-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500014814804?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Ypiranga","Novo Hamburgo"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'de3abdfe-5eee-4f73-ad95-d2515f39ce60', 'Fernando Bezerra', 'Fernando', 'atleta-fernando-13',
    'MALE', 'PROFESSIONAL', 'Lateral-Direito', 179, 75, '1998-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500016049371?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Veranópolis","São Gabriel"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '20676165-c158-411b-a9bd-1b4d4dc17d7a', 'Mateus Mendes', 'Mendes', 'atleta-mendes-14',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 189, 85, '1999-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500017283938?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Guarany-BA","Caxias"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '0de40a4b-e041-4103-ab45-6cd186147674', 'Rafael Dumas', 'Dumas', 'atleta-dumas-15',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 188, 84, '1995-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500018518505?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Flamengo","Brasil-RS"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'a386d629-0be0-4a76-ae83-4670f2d4fb80', 'Anderson Santos', 'Anderson', 'atleta-anderson-16',
    'MALE', 'PROFESSIONAL', 'Lateral-Esquerdo', 177, 72, '1999-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500019753072?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["São José","Lajeadense"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'e8a44599-d67b-4b5b-a667-2f0aecac5c3e', 'William da Silva', 'William', 'atleta-william-17',
    'MALE', 'PROFESSIONAL', 'Lateral-Direito', 180, 76, '2001-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500020987639?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Monsoon","Santa Cruz"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'afb8b334-cbab-43ce-a2cf-d0f6d42ba509', 'Caio César', 'Caio', 'atleta-caio-18',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 190, 86, '1997-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500022222206?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Criciúma","Hercílio Luz"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c8cf329f-0fe9-4afd-a6be-ea44f36d93ab', 'Patrick Borges', 'Patrick', 'atleta-patrick-19',
    'MALE', 'PROFESSIONAL', 'Zagueiro', 187, 83, '1996-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500023456773?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Joinville","Marcílio Dias"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd4f6a350-61cb-48a8-aac2-2cb5f2c5a568', 'Marcelo Amaral', 'Marcelo', 'atleta-marcelo-20',
    'MALE', 'PROFESSIONAL', 'Lateral-Esquerdo', 179, 73, '2002-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500024691340?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Cruzeiro-RS","Pelotas Base"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '424173b2-b55e-459c-a3dc-c66bff5c6d77', 'Diego Martins', 'Martins', 'atleta-martins-21',
    'MALE', 'PROFESSIONAL', 'Lateral-Direito', 182, 78, '1998-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500025925907?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["União Frederiquense","Gaúcho"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '64499ae9-ba85-4a01-a197-933638919d61', 'Christian Silva de Oliveira', 'Christian', 'atleta-christian-22',
    'MALE', 'PROFESSIONAL', 'Volante', 182, 79, '1995-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500027160474?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Grêmio","América-RN"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'a90dfcdd-8500-40b9-af89-b68a10b49f31', 'Robson de Oliveira', 'Robson', 'atleta-robson-23',
    'MALE', 'PROFESSIONAL', 'Volante', 184, 82, '1993-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500028395041?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Figueirense","Léondrina"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'cb1e9b9f-c51f-4081-ac45-5788f8ac9bc7', 'Germano Borovicz', 'Germano', 'atleta-germano-24',
    'MALE', 'PROFESSIONAL', 'Volante', 180, 77, '1994-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500029629608?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Coritiba","Paráanáá"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '828d634a-5a59-4687-a28b-4529069e0192', 'Paulo Tinga', 'Tinga', 'atleta-tinga-25',
    'MALE', 'PROFESSIONAL', 'Meia-Central', 178, 75, '1996-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500030864175?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Fortaleza","Botafogo-PB"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '066717b9-3956-4f72-a43e-88749c7d2f2d', 'Vitor Silva Assis de Oliveira Júnior', 'Vitor Júnior', 'atleta-vitor-j-nior-26',
    'MALE', 'PROFESSIONAL', 'Meia-Atacante', 172, 70, '1986-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500032098742?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Internacional","Corinthians","Botafogo"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '7bc5f6ee-b4f8-40ff-a121-9c1fd58f89ef', 'Juninho Silva', 'Juninho', 'atleta-juninho-27',
    'MALE', 'PROFESSIONAL', 'Meia-Atacante', 174, 71, '1999-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500033333309?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Avaíí","Brasil-RS"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'e52c9ae0-b185-486b-a12e-4552f36e447b', 'Clayton da Rosa', 'Clayton', 'atleta-clayton-28',
    'MALE', 'PROFESSIONAL', 'Volante', 183, 80, '1997-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500034567876?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["São Luiz","São José"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'cf63a8a6-9ed4-4bf1-a11b-79b988fec4e3', 'Maurício Garcez', 'Maurício', 'atleta-maur-cio-29',
    'MALE', 'PROFESSIONAL', 'Meia-Central', 180, 76, '2000-05-15', 'Pelotas - RS', 'BOTH',
    'https://images.unsplash.com/photo-1500035802443?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Brusque","Avaíí"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '1a24429c-3477-42b2-a6fa-23e41b261190', 'Jean Roberto', 'Jean', 'atleta-jean-30',
    'MALE', 'PROFESSIONAL', 'Meia-Atacante', 175, 72, '1994-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500037037010?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Caxias","Tombense"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '652e5b46-e132-4362-a966-eee229cd103c', 'Renan Pedrada', 'Pedrada', 'atleta-pedrada-31',
    'MALE', 'PROFESSIONAL', 'Volante', 185, 83, '1998-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500038271577?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Novo Hamburgo","Aimoréé"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c8916b30-f8c3-4a11-a3cd-d6adce160caf', 'Darlan Mendes', 'Darlan', 'atleta-darlan-32',
    'MALE', 'PROFESSIONAL', 'Volante', 176, 73, '1998-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500039506144?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Grêmio","Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '8fe9b4f7-994d-40d7-a428-003c30edbeb7', 'Igor Silva', 'Igor', 'atleta-igor-33',
    'MALE', 'PROFESSIONAL', 'Meia-Central', 178, 74, '2001-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500040740711?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Pelotas Base","Avenida"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '99d52361-fa3e-46bf-aeba-7d3685fda405', 'Fabinho Sãouza', 'Fabinho', 'atleta-fabinho-34',
    'MALE', 'PROFESSIONAL', 'Volante', 181, 79, '1997-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500041975278?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["São Caetano","XV de Piracicaba"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '936645c4-04bb-4b71-a1b8-98d226d94eff', 'Tales Lisandro', 'Tales', 'atleta-tales-35',
    'MALE', 'PROFESSIONAL', 'Meia-Atacante', 173, 69, '2002-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500043209845?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Internacional","Ypiranga"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '654da821-31b3-4894-ab08-aa8040d1db23', 'Lucas Ramos', 'Ramos', 'atleta-ramos-36',
    'MALE', 'PROFESSIONAL', 'Meia-Central', 182, 78, '2001-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500044444412?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Juventude","Caxias"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '4adda3d4-f4c1-4008-aff5-2638410d3752', 'Guilherme Garréé', 'Garréé', 'atleta-garr---37',
    'MALE', 'PROFESSIONAL', 'Meia-Atacante', 177, 73, '1993-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500045678979?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Santo Andréé","Figueirense"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '468a9041-cadc-4cd4-ae2e-4777d975f6ba', 'Walter Henrique de Oliveira', 'Walter', 'atleta-walter-38',
    'MALE', 'PROFESSIONAL', 'Centroavante', 178, 88, '1989-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500046913546?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Internacional","Porto","Fluminense","Goiás"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '5ef97ba6-3086-46ea-a936-1b6a5350f81a', 'Michel das Chagas', 'Michel', 'atleta-michel-39',
    'MALE', 'PROFESSIONAL', 'Centroavante', 185, 84, '1989-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500048148113?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Caxias","São Bento","Brasil-RS"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '3418a93d-b5a6-4002-a008-be74d2283a4c', 'Warley Silva', 'Warley', 'atleta-warley-40',
    'MALE', 'PROFESSIONAL', 'Ponta-Direita', 175, 71, '1996-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500049382680?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Botafogo","Coritiba"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '6aa62c87-6d19-49ee-a9a3-c04aaedba73f', 'Gustavo Sapeka', 'Sapeka', 'atleta-sapeka-41',
    'MALE', 'PROFESSIONAL', 'Ponta-Esquerda', 173, 69, '1992-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500050617247?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["São Luiz","Novo Hamburgo"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '7badd2d9-6e37-4c1f-a68d-32c2f851efcc', 'Emerson Bacas', 'Bacas', 'atleta-bacas-42',
    'MALE', 'PROFESSIONAL', 'Ponta-Esquerda', 176, 72, '1995-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500051851814?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Porto Velho","Fast Clube"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '646f9c47-df48-4a3a-a8b3-f1f3102db92b', 'Léo Santos', 'Léo Santos', 'atleta-l-o-santos-43',
    'MALE', 'PROFESSIONAL', 'Centroavante', 188, 86, '1998-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500053086381?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Ituano","Ponte Preta"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c08808b1-9449-4e20-ade2-d48e4b54c92d', 'Rildo de Andrade', 'Rildo', 'atleta-rildo-44',
    'MALE', 'PROFESSIONAL', 'Ponta-Direita', 177, 73, '1989-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500054320948?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Santos","Corinthians","Vasco"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '7e0903f1-e16f-4021-a957-613b0fefb84a', 'Danielzinho Pereira', 'Danielzinho', 'atleta-danielzinho-45',
    'MALE', 'PROFESSIONAL', 'Ponta-Esquerda', 171, 67, '2001-05-15', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1500055555515?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Pelotas Base","Monsoon"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '0d3925e3-2726-4d7b-a6fd-a0873299dea6', 'Felipe Paráá', 'Paráá', 'atleta-par---46',
    'MALE', 'PROFESSIONAL', 'Ponta-Direita', 174, 70, '1995-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500056790082?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Remo","Paysandu","São José"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c086bb6b-86dd-401f-af64-271659c9e0e3', 'Jardel Oliveira', 'Jardel', 'atleta-jardel-47',
    'MALE', 'PROFESSIONAL', 'Centroavante', 190, 87, '1997-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500058024649?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Glória","Veranópolis"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '134c683d-e34e-46ab-aa6c-3c86bea1d0f3', 'Romário Silva', 'Romarinho', 'atleta-romarinho-48',
    'MALE', 'PROFESSIONAL', 'Ponta-Direita', 172, 68, '2000-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500059259216?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Passo Fundo","Gaúcho"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '89dba2b7-79b2-4a4e-aec2-20525170ab45', 'Alison Araújo', 'Alison', 'atleta-alison-49',
    'MALE', 'PROFESSIONAL', 'Centroavante', 186, 83, '1999-05-15', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1500060493783?auto=format&fit=crop&w=600&q=80', 'Atleta profissional dedicado à que defende com orgulho a camisa áureo-cerúlea na Boca do Lobo.',
    '["Caxias","Ypiranga"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd1427628-473a-4911-a081-efd1fd8d2c0b', 'Leocir Dall''Astra', 'Leocir', 'comissao-leocir-0', 'MALE', 'STAFF',
    'Treinador', 'TREINADOR', '1978-08-20', 'Rio Grande do Sul', 'https://images.unsplash.com/photo-1510000000000?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica permanente do Esporte Clube Pelotas.', '["Ypiranga", "Caxias", "Juventude"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '8d4b5c30-5991-4cc4-ae04-c1ef58010f3d', 'Felipe Endres', 'Endres', 'comissao-endres-1', 'MALE', 'STAFF',
    'Auxiliar Técnico', 'AUXILIAR_TECNICO', '1978-08-20', 'Rio Grande do Sul', 'https://images.unsplash.com/photo-1510000987654?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica permanente do Esporte Clube Pelotas.', '["Ypiranga", "Caxias", "Juventude"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '24267d89-46e4-4dcc-af6e-c42ec68e847a', 'Lucas Pereira', 'Lucas', 'comissao-lucas-2', 'MALE', 'STAFF',
    'Preparador Físico', 'PREPARADOR_FISICO', '1978-08-20', 'Rio Grande do Sul', 'https://images.unsplash.com/photo-1510001975308?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica permanente do Esporte Clube Pelotas.', '["Ypiranga", "Caxias", "Juventude"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '6e59baf9-6829-4fe9-a51c-2dc6e901763d', 'Marcelo Rohling', 'Rohling', 'comissao-rohling-3', 'MALE', 'STAFF',
    'Preparador de Goleiros', 'PREPARADOR_GOLEIROS', '1978-08-20', 'Rio Grande do Sul', 'https://images.unsplash.com/photo-1510002962962?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica permanente do Esporte Clube Pelotas.', '["Ypiranga", "Caxias", "Juventude"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '38d703b8-1a72-4a43-a564-41ce7b9deedd', 'Dr. Rafael Silva', 'Dr. Rafael', 'comissao-dr--rafael-4', 'MALE', 'STAFF',
    'Fisioterapeuta', 'FISIOTERAPEUTA', '1978-08-20', 'Rio Grande do Sul', 'https://images.unsplash.com/photo-1510003950616?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica permanente do Esporte Clube Pelotas.', '["Ypiranga", "Caxias", "Juventude"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'e383c52f-404d-4afe-ac0f-ace982cd6017', 'Matheus Lima', 'Matheus', 'comissao-matheus-5', 'MALE', 'STAFF',
    'Analista de Desempenho', 'ANALISTA_DESEMPENHO', '1978-08-20', 'Rio Grande do Sul', 'https://images.unsplash.com/photo-1510004938270?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica permanente do Esporte Clube Pelotas.', '["Ypiranga", "Caxias", "Juventude"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
-- 8. Alocação nos 3 Elencos Profissionais Masculinos
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c35dc59e-78ba-4437-ad6e-7e4416f055e6', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'fa38d8f4-0890-4ca4-a689-20abbabf0ab9', 1, 'GOALKEEPER', 'STARTER', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('5292a66e-2bcc-4750-a663-3ccc431e48b5', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'dc6e0460-0c8c-4dce-a256-55f3c3ddfa35', 2, 'GOALKEEPER', 'CAPTAIN', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2b21ccf6-2767-4e97-a280-b866ebdef1ad', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'd9e2c2b0-4cfa-4b0f-a021-224e60a9d9eb', 3, 'GOALKEEPER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b199e3ef-1714-494e-afda-ced9293c3d0a', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'c8e93acd-c7f7-497e-a027-6d616c4d7e32', 4, 'GOALKEEPER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e8725f11-63cd-41a4-a06b-e2a168cddb14', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'd2614387-c355-40cf-a50c-c8f658ffad8e', 5, 'GOALKEEPER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1fb91584-93b9-480b-a13c-85344db10f9b', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'b505de2b-5f28-46e7-a3cd-f6f66853ff48', 6, 'GOALKEEPER', 'STARTER', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1e5dd54f-4352-4c47-a99e-112fb72da134', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'f78ca3bd-5c29-4944-a03f-dea74a4917d0', 7, 'DEFENDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('394a88e6-e5a8-4d05-a33c-c8be1d65e793', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'e430d24a-f7ab-40b8-a404-00e910341402', 8, 'DEFENDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4d4b0505-5ecb-409e-ab56-84378df44df4', '6f2d2d79-6f18-4c13-a69d-772067e59993', '5033b2c3-fadd-459c-aab7-4df27f4162ad', 9, 'DEFENDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4a7f86ef-9be7-4a72-a328-3518ad8702aa', '6f2d2d79-6f18-4c13-a69d-772067e59993', '26c4c0ce-fc3f-4d16-acb4-aaf1fb0ca1b5', 10, 'DEFENDER', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('afbe6a73-7e78-4c7c-ae64-71808f7e51a5', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'ebe865cc-db0d-4700-ad48-cfc0c16e0b5b', 11, 'DEFENDER', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('af0d0065-f594-49b9-ab8d-8e05846db27a', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'c890fdaa-11e6-403d-af71-7569e5e9f9d2', 12, 'DEFENDER', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('65ffb5f5-0153-4e95-af56-7f3d9d4f7da9', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'bb2423da-73ea-4bef-a143-273acd812419', 13, 'DEFENDER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('0058c8bd-2953-42f4-a497-98e43f400b4c', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'de3abdfe-5eee-4f73-ad95-d2515f39ce60', 14, 'DEFENDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8888ab9c-6a4d-4e30-a7a1-670142b5fdde', '6f2d2d79-6f18-4c13-a69d-772067e59993', '20676165-c158-411b-a9bd-1b4d4dc17d7a', 15, 'DEFENDER', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('779b9c5c-3f43-4734-af30-fd1c2a8cefde', '6f2d2d79-6f18-4c13-a69d-772067e59993', '0de40a4b-e041-4103-ab45-6cd186147674', 16, 'DEFENDER', 'SUBSTITUTE', 16, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('db6370eb-bcf0-4c03-a1a9-24693dc2d870', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'a386d629-0be0-4a76-ae83-4670f2d4fb80', 17, 'DEFENDER', 'SUBSTITUTE', 17, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('01735b83-fa0e-4535-adae-24589d3d3db4', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'e8a44599-d67b-4b5b-a667-2f0aecac5c3e', 18, 'DEFENDER', 'SUBSTITUTE', 18, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('57a66071-a7d8-4e01-a32b-3907dd14580c', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'afb8b334-cbab-43ce-a2cf-d0f6d42ba509', 19, 'DEFENDER', 'SUBSTITUTE', 19, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('179d24c0-1b37-48f8-a44a-06c5c7b36377', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'c8cf329f-0fe9-4afd-a6be-ea44f36d93ab', 20, 'DEFENDER', 'SUBSTITUTE', 20, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('51b622be-21f0-4e5f-abd8-c0b8fb4a6d9d', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'd4f6a350-61cb-48a8-aac2-2cb5f2c5a568', 21, 'DEFENDER', 'SUBSTITUTE', 21, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e294664a-a501-44d6-a7a7-ff06f642688d', '6f2d2d79-6f18-4c13-a69d-772067e59993', '424173b2-b55e-459c-a3dc-c66bff5c6d77', 22, 'DEFENDER', 'SUBSTITUTE', 22, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('24f7bde8-f23e-4163-a675-7d5084abd37c', '6f2d2d79-6f18-4c13-a69d-772067e59993', '64499ae9-ba85-4a01-a197-933638919d61', 23, 'MIDFIELDER', 'SUBSTITUTE', 23, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('fad8d93f-badc-4735-a1c3-4b3632dfa2b6', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'a90dfcdd-8500-40b9-af89-b68a10b49f31', 24, 'MIDFIELDER', 'SUBSTITUTE', 24, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('d7d3028d-f4d1-40cd-a0f0-f6034545ca56', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'cb1e9b9f-c51f-4081-ac45-5788f8ac9bc7', 25, 'MIDFIELDER', 'SUBSTITUTE', 25, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2871e688-b2cb-442d-aed5-048e30c5b525', '6f2d2d79-6f18-4c13-a69d-772067e59993', '828d634a-5a59-4687-a28b-4529069e0192', 26, 'MIDFIELDER', 'SUBSTITUTE', 26, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c78cea43-ced9-4bed-a23d-f2b1cc151a3b', '6f2d2d79-6f18-4c13-a69d-772067e59993', '066717b9-3956-4f72-a43e-88749c7d2f2d', 27, 'MIDFIELDER', 'SUBSTITUTE', 27, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8fe72787-748f-4763-a5ef-38c05a89c7ab', '6f2d2d79-6f18-4c13-a69d-772067e59993', '7bc5f6ee-b4f8-40ff-a121-9c1fd58f89ef', 28, 'MIDFIELDER', 'SUBSTITUTE', 28, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e6c3b5e9-7762-43de-a6da-44ea796016a4', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'e52c9ae0-b185-486b-a12e-4552f36e447b', 29, 'MIDFIELDER', 'SUBSTITUTE', 29, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('934bd1ab-23bd-40c2-a26c-444061ad1ad1', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'cf63a8a6-9ed4-4bf1-a11b-79b988fec4e3', 30, 'MIDFIELDER', 'SUBSTITUTE', 30, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('63ecfa98-2fea-411b-a7a4-f39304f2459b', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'd1427628-473a-4911-a081-efd1fd8d2c0b', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('db9d0d4f-a844-4999-a48c-9c181d0e80c2', '6f2d2d79-6f18-4c13-a69d-772067e59993', '8d4b5c30-5991-4cc4-ae04-c1ef58010f3d', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('878c2d6d-bdce-4b52-acad-33efd337ea1b', '6f2d2d79-6f18-4c13-a69d-772067e59993', '24267d89-46e4-4dcc-af6e-c42ec68e847a', NULL, 'STAFF', 'STARTER', 102, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e68efd75-5f8d-484f-aea6-fd4556d03624', '6f2d2d79-6f18-4c13-a69d-772067e59993', '6e59baf9-6829-4fe9-a51c-2dc6e901763d', NULL, 'STAFF', 'STARTER', 103, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2c955648-ad98-4050-a1eb-cb8bca203cb9', '6f2d2d79-6f18-4c13-a69d-772067e59993', '38d703b8-1a72-4a43-a564-41ce7b9deedd', NULL, 'STAFF', 'STARTER', 104, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e4f6351c-1c24-427e-aca8-539ace6ee4ee', '6f2d2d79-6f18-4c13-a69d-772067e59993', 'e383c52f-404d-4afe-ac0f-ace982cd6017', NULL, 'STAFF', 'STARTER', 105, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6f2afac1-2f8d-4fe3-a337-8b059127507c', '8f517076-7b8e-4544-a6bb-13915018e382', 'ebe865cc-db0d-4700-ad48-cfc0c16e0b5b', 1, 'DEFENDER', 'CAPTAIN', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('5460830b-feb3-4104-ac0f-c39d98d1d8a3', '8f517076-7b8e-4544-a6bb-13915018e382', 'c890fdaa-11e6-403d-af71-7569e5e9f9d2', 2, 'DEFENDER', 'STARTER', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e08ecc77-13dd-4090-aded-473429653be0', '8f517076-7b8e-4544-a6bb-13915018e382', 'bb2423da-73ea-4bef-a143-273acd812419', 3, 'DEFENDER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('3cc14d03-0ad1-4278-a2e1-f3a8ef1f687c', '8f517076-7b8e-4544-a6bb-13915018e382', 'de3abdfe-5eee-4f73-ad95-d2515f39ce60', 4, 'DEFENDER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c0c73c3a-84cd-43a6-abfc-aceef4dfe8dd', '8f517076-7b8e-4544-a6bb-13915018e382', '20676165-c158-411b-a9bd-1b4d4dc17d7a', 5, 'DEFENDER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('70a39697-b25f-4044-a477-90af05479a18', '8f517076-7b8e-4544-a6bb-13915018e382', '0de40a4b-e041-4103-ab45-6cd186147674', 6, 'DEFENDER', 'STARTER', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f9c33b93-72db-4397-ae1c-1b4f8594d8a0', '8f517076-7b8e-4544-a6bb-13915018e382', 'a386d629-0be0-4a76-ae83-4670f2d4fb80', 7, 'DEFENDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2ef93fff-31bf-40a2-ac6d-cf981fe66b35', '8f517076-7b8e-4544-a6bb-13915018e382', 'e8a44599-d67b-4b5b-a667-2f0aecac5c3e', 8, 'DEFENDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f94f05b3-8179-43fe-a1a1-30375ae1aa28', '8f517076-7b8e-4544-a6bb-13915018e382', 'afb8b334-cbab-43ce-a2cf-d0f6d42ba509', 9, 'DEFENDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('7692a2e1-d52e-46f2-a535-b6c693f5cca0', '8f517076-7b8e-4544-a6bb-13915018e382', 'c8cf329f-0fe9-4afd-a6be-ea44f36d93ab', 10, 'DEFENDER', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('d3f695b0-5c5f-4295-a5b2-50ba5ad7fe3b', '8f517076-7b8e-4544-a6bb-13915018e382', 'd4f6a350-61cb-48a8-aac2-2cb5f2c5a568', 11, 'DEFENDER', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('82c4ec20-112e-4745-ad30-80584557925a', '8f517076-7b8e-4544-a6bb-13915018e382', '424173b2-b55e-459c-a3dc-c66bff5c6d77', 12, 'DEFENDER', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('7996ea5d-42b8-4285-ab03-692be519160b', '8f517076-7b8e-4544-a6bb-13915018e382', '64499ae9-ba85-4a01-a197-933638919d61', 13, 'MIDFIELDER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f2228843-56d1-42ae-a37f-0aa10c98644a', '8f517076-7b8e-4544-a6bb-13915018e382', 'a90dfcdd-8500-40b9-af89-b68a10b49f31', 14, 'MIDFIELDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('37cbf26a-6ff9-4dae-a07f-2ed03c4f64d7', '8f517076-7b8e-4544-a6bb-13915018e382', 'cb1e9b9f-c51f-4081-ac45-5788f8ac9bc7', 15, 'MIDFIELDER', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('dce133b0-6f7f-4131-afcc-a457040c6370', '8f517076-7b8e-4544-a6bb-13915018e382', '828d634a-5a59-4687-a28b-4529069e0192', 16, 'MIDFIELDER', 'SUBSTITUTE', 16, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4ce9f0b6-3311-4c4f-adf8-4d69f1e1fa87', '8f517076-7b8e-4544-a6bb-13915018e382', '066717b9-3956-4f72-a43e-88749c7d2f2d', 17, 'MIDFIELDER', 'SUBSTITUTE', 17, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1f2638de-3504-4f5a-aa5e-b950edeacc9f', '8f517076-7b8e-4544-a6bb-13915018e382', '7bc5f6ee-b4f8-40ff-a121-9c1fd58f89ef', 18, 'MIDFIELDER', 'SUBSTITUTE', 18, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('214e20ff-2083-4616-aa5f-0e99978a3f44', '8f517076-7b8e-4544-a6bb-13915018e382', 'e52c9ae0-b185-486b-a12e-4552f36e447b', 19, 'MIDFIELDER', 'SUBSTITUTE', 19, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('042cd4af-794f-4bba-a968-5b264166e874', '8f517076-7b8e-4544-a6bb-13915018e382', 'cf63a8a6-9ed4-4bf1-a11b-79b988fec4e3', 20, 'MIDFIELDER', 'SUBSTITUTE', 20, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f278cf3a-83f2-4609-a21f-48a6e6aeb452', '8f517076-7b8e-4544-a6bb-13915018e382', '1a24429c-3477-42b2-a6fa-23e41b261190', 21, 'MIDFIELDER', 'SUBSTITUTE', 21, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('719fa95d-a9ba-48f9-a7eb-4c5c91d517e1', '8f517076-7b8e-4544-a6bb-13915018e382', '652e5b46-e132-4362-a966-eee229cd103c', 22, 'MIDFIELDER', 'SUBSTITUTE', 22, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('451f29d3-1432-40a5-a543-b39ca7e8a513', '8f517076-7b8e-4544-a6bb-13915018e382', 'c8916b30-f8c3-4a11-a3cd-d6adce160caf', 23, 'MIDFIELDER', 'SUBSTITUTE', 23, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9e3550f2-5773-445f-a385-5181de749a82', '8f517076-7b8e-4544-a6bb-13915018e382', '8fe9b4f7-994d-40d7-a428-003c30edbeb7', 24, 'MIDFIELDER', 'SUBSTITUTE', 24, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b114c066-256e-496f-a075-a7d69103b200', '8f517076-7b8e-4544-a6bb-13915018e382', '99d52361-fa3e-46bf-aeba-7d3685fda405', 25, 'MIDFIELDER', 'SUBSTITUTE', 25, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('0e68600e-4128-4baf-a68a-9a87fc37807b', '8f517076-7b8e-4544-a6bb-13915018e382', '936645c4-04bb-4b71-a1b8-98d226d94eff', 26, 'MIDFIELDER', 'SUBSTITUTE', 26, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4d570ac9-bc52-4112-abc5-937bff09395c', '8f517076-7b8e-4544-a6bb-13915018e382', '654da821-31b3-4894-ab08-aa8040d1db23', 27, 'MIDFIELDER', 'SUBSTITUTE', 27, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('d60a0c3e-00e9-417a-a8fd-eb15897ba591', '8f517076-7b8e-4544-a6bb-13915018e382', '4adda3d4-f4c1-4008-aff5-2638410d3752', 28, 'MIDFIELDER', 'SUBSTITUTE', 28, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e70d4d35-9633-48a2-a621-08f3da1a4e22', '8f517076-7b8e-4544-a6bb-13915018e382', '468a9041-cadc-4cd4-ae2e-4777d975f6ba', 29, 'FORWARD', 'SUBSTITUTE', 29, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('13bad2e3-9e6d-4b0a-aae5-e7e45cb32c78', '8f517076-7b8e-4544-a6bb-13915018e382', '5ef97ba6-3086-46ea-a936-1b6a5350f81a', 30, 'FORWARD', 'SUBSTITUTE', 30, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('71da4958-b0c4-4e8f-a1b0-76cd9f268a33', '8f517076-7b8e-4544-a6bb-13915018e382', 'd1427628-473a-4911-a081-efd1fd8d2c0b', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6744b30a-0031-4057-afbd-84825e2f1375', '8f517076-7b8e-4544-a6bb-13915018e382', '8d4b5c30-5991-4cc4-ae04-c1ef58010f3d', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('596a04c4-c54d-45a9-a877-a7c9e9258d95', '8f517076-7b8e-4544-a6bb-13915018e382', '24267d89-46e4-4dcc-af6e-c42ec68e847a', NULL, 'STAFF', 'STARTER', 102, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9cb9b359-8971-4caa-aeaf-1bc5fea3e0ff', '8f517076-7b8e-4544-a6bb-13915018e382', '6e59baf9-6829-4fe9-a51c-2dc6e901763d', NULL, 'STAFF', 'STARTER', 103, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('067ec03e-138d-47ea-ae0e-790f183374b0', '8f517076-7b8e-4544-a6bb-13915018e382', '38d703b8-1a72-4a43-a564-41ce7b9deedd', NULL, 'STAFF', 'STARTER', 104, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c15b7abd-55e9-4b07-a5de-afd9306bbfd7', '8f517076-7b8e-4544-a6bb-13915018e382', 'e383c52f-404d-4afe-ac0f-ace982cd6017', NULL, 'STAFF', 'STARTER', 105, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f088239c-c9dd-438a-a15d-d29a6a7121fd', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'd4f6a350-61cb-48a8-aac2-2cb5f2c5a568', 1, 'DEFENDER', 'STARTER', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('7601324b-46a2-4a3e-acf7-11134b6eebb5', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '424173b2-b55e-459c-a3dc-c66bff5c6d77', 2, 'DEFENDER', 'STARTER', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ffbe281b-b3ca-4c84-a0f6-792fd9ffe31a', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '64499ae9-ba85-4a01-a197-933638919d61', 3, 'MIDFIELDER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('3fbe6582-005a-4d6a-af2f-4e20da07419c', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'a90dfcdd-8500-40b9-af89-b68a10b49f31', 4, 'MIDFIELDER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9c4d5e91-1656-4fd0-a090-abb2fe8a8b62', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'cb1e9b9f-c51f-4081-ac45-5788f8ac9bc7', 5, 'MIDFIELDER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f5c24717-283b-41ff-a1e8-f15895e72187', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '828d634a-5a59-4687-a28b-4529069e0192', 6, 'MIDFIELDER', 'CAPTAIN', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('16b14858-b817-4799-a221-0f56e5ece4aa', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '066717b9-3956-4f72-a43e-88749c7d2f2d', 7, 'MIDFIELDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('aead2061-9fad-4326-a76f-d14ef475a9c8', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '7bc5f6ee-b4f8-40ff-a121-9c1fd58f89ef', 8, 'MIDFIELDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('30e8820f-2430-4e5a-a628-dfc0bc925903', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'e52c9ae0-b185-486b-a12e-4552f36e447b', 9, 'MIDFIELDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6d41532a-81e6-4b1d-a36a-6d9d6c6c7c8b', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'cf63a8a6-9ed4-4bf1-a11b-79b988fec4e3', 10, 'MIDFIELDER', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('5c907437-b02d-48ca-ae37-fc5e18311012', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '1a24429c-3477-42b2-a6fa-23e41b261190', 11, 'MIDFIELDER', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b92389c1-4775-4970-a129-ccb8c8662521', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '652e5b46-e132-4362-a966-eee229cd103c', 12, 'MIDFIELDER', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8f09602b-ac85-48ff-ae5d-c137530c31d3', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'c8916b30-f8c3-4a11-a3cd-d6adce160caf', 13, 'MIDFIELDER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('311f785f-72b4-493c-ab93-5bd93b0e6837', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '8fe9b4f7-994d-40d7-a428-003c30edbeb7', 14, 'MIDFIELDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('23d35aca-27e4-4b88-abfb-4768cd8e0166', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '99d52361-fa3e-46bf-aeba-7d3685fda405', 15, 'MIDFIELDER', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('10fc7e43-2763-4a01-aa35-893057deda96', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '936645c4-04bb-4b71-a1b8-98d226d94eff', 16, 'MIDFIELDER', 'SUBSTITUTE', 16, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6cc8ab44-9a01-4335-ac55-b5c6bea5021d', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '654da821-31b3-4894-ab08-aa8040d1db23', 17, 'MIDFIELDER', 'SUBSTITUTE', 17, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('da39817c-9469-4b6f-a7a4-253813c9f31f', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '4adda3d4-f4c1-4008-aff5-2638410d3752', 18, 'MIDFIELDER', 'SUBSTITUTE', 18, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('86b90913-5f13-4752-a4ca-07263e3161c2', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '468a9041-cadc-4cd4-ae2e-4777d975f6ba', 19, 'FORWARD', 'SUBSTITUTE', 19, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b5c39d91-5450-4f96-ac61-7c1fd9092b24', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '5ef97ba6-3086-46ea-a936-1b6a5350f81a', 20, 'FORWARD', 'SUBSTITUTE', 20, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e8a9562b-6a99-4671-a275-6ce441ca7eea', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '3418a93d-b5a6-4002-a008-be74d2283a4c', 21, 'FORWARD', 'SUBSTITUTE', 21, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1e64949e-14a0-48ab-a945-2f7737f81036', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '6aa62c87-6d19-49ee-a9a3-c04aaedba73f', 22, 'FORWARD', 'SUBSTITUTE', 22, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('71f0d486-b42c-4e93-adb3-5eec0bc53ea8', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '7badd2d9-6e37-4c1f-a68d-32c2f851efcc', 23, 'FORWARD', 'SUBSTITUTE', 23, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e8165b0f-2d50-4e91-a6fd-d456b1a0916b', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '646f9c47-df48-4a3a-a8b3-f1f3102db92b', 24, 'FORWARD', 'SUBSTITUTE', 24, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('3e7dc305-d9b8-4b94-a166-4a30cd814cc6', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'c08808b1-9449-4e20-ade2-d48e4b54c92d', 25, 'FORWARD', 'SUBSTITUTE', 25, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ab77b905-fd29-4d86-aafa-8bdb59959ee6', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '7e0903f1-e16f-4021-a957-613b0fefb84a', 26, 'FORWARD', 'SUBSTITUTE', 26, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4bf523a5-a888-4dfc-ab8b-bb963b23acba', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '0d3925e3-2726-4d7b-a6fd-a0873299dea6', 27, 'FORWARD', 'SUBSTITUTE', 27, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4771f864-9b4c-4da9-aabc-8da5d6ab90df', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'c086bb6b-86dd-401f-af64-271659c9e0e3', 28, 'FORWARD', 'SUBSTITUTE', 28, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ec264ea0-3695-4c85-a414-187e890b7b2f', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '134c683d-e34e-46ab-aa6c-3c86bea1d0f3', 29, 'FORWARD', 'SUBSTITUTE', 29, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e2ef01ab-82cf-4905-a9de-eefa912998b1', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '89dba2b7-79b2-4a4e-aec2-20525170ab45', 30, 'FORWARD', 'SUBSTITUTE', 30, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('69f0a75e-c1c5-4485-afd2-6cef052047dd', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'd1427628-473a-4911-a081-efd1fd8d2c0b', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4cc97d9c-aa7f-4d45-ac02-943a87210d74', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '8d4b5c30-5991-4cc4-ae04-c1ef58010f3d', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('7e828f13-e9d7-4b2e-a02a-b9ff38007f25', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '24267d89-46e4-4dcc-af6e-c42ec68e847a', NULL, 'STAFF', 'STARTER', 102, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('838de6ae-85d1-4ba4-af29-278ed47a03f8', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '6e59baf9-6829-4fe9-a51c-2dc6e901763d', NULL, 'STAFF', 'STARTER', 103, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ca1c32fc-74cd-499c-a162-a422aedc6bf6', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', '38d703b8-1a72-4a43-a564-41ce7b9deedd', NULL, 'STAFF', 'STARTER', 104, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f2d6c93e-5a09-4b52-af2c-e480749e0339', '9355549c-7656-4f14-a6dc-f6b603cb0ba2', 'e383c52f-404d-4afe-ac0f-ace982cd6017', NULL, 'STAFF', 'STARTER', 105, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
-- 9. Inserção de Atletas Femininas (30 atletas)
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd85652f8-d86d-44b2-a14c-ac3d847f1db9', 'Camila Barbosa da Silva', 'Camila', 'atleta-fem-camila-0',
    'FEMALE', 'PROFESSIONAL', 'Goleira', 178, 68, '1997-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530000000000?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'a95f95bd-6007-4e9d-a337-613abd2c1eb9', 'Gabriela Ramos', 'Gabi', 'atleta-fem-gabi-1',
    'FEMALE', 'PROFESSIONAL', 'Goleira', 176, 67, '2000-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530002345678?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '2758453e-5591-426e-a904-260f4b5d6fc6', 'Fernanda Lima', 'Fê Lima', 'atleta-fem-f--lima-2',
    'FEMALE', 'PROFESSIONAL', 'Goleira', 175, 66, '2002-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530004691356?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '77c89b44-016a-41dc-a553-ffebeeacbdf6', 'Luana Sãouza', 'Luana', 'atleta-fem-luana-3',
    'FEMALE', 'PROFESSIONAL', 'Goleira', 177, 68, '1999-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530007037034?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '11369b51-a488-488c-a9a2-e6cf40238a1b', 'Carla Peixoto', 'Carla', 'atleta-fem-carla-4',
    'FEMALE', 'PROFESSIONAL', 'Zagueira', 174, 65, '1996-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530009382712?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '1fa9024f-9753-4314-ad29-4b8ef13a4ca3', 'Jéssica Medeiros', 'Jéssica', 'atleta-fem-j-ssica-5',
    'FEMALE', 'PROFESSIONAL', 'Zagueira', 175, 66, '1995-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530011728390?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '5fd7fd6d-9164-410a-aa3d-99bd751e3870', 'Bruna Castilho', 'Bruna', 'atleta-fem-bruna-6',
    'FEMALE', 'PROFESSIONAL', 'Lateral-Esquerda', 168, 60, '1998-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530014074068?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'de7262dd-1099-4783-a0c1-c2aa316aae99', 'Mariana Rocha', 'Mari', 'atleta-fem-mari-7',
    'FEMALE', 'PROFESSIONAL', 'Lateral-Direita', 169, 61, '1999-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530016419746?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'de383a38-b31d-431a-adc3-4aba392b4b3c', 'Beatriz Alencar', 'Bia', 'atleta-fem-bia-8',
    'FEMALE', 'PROFESSIONAL', 'Zagueira', 176, 67, '2001-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530018765424?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '0c6c44d9-d979-4e89-ad6c-d0430bcc0e7e', 'Letícia Fontes', 'Lê', 'atleta-fem-l--9',
    'FEMALE', 'PROFESSIONAL', 'Lateral-Esquerda', 167, 59, '2002-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530021111102?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd198540a-b82c-418f-a717-40783426c2f0', 'Larissa Nunes', 'Larissa', 'atleta-fem-larissa-10',
    'FEMALE', 'PROFESSIONAL', 'Lateral-Direita', 170, 62, '1997-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530023456780?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '86bc1ce4-d6e2-4eed-ab47-6bb37053f205', 'Amanda Diniz', 'Amanda', 'atleta-fem-amanda-11',
    'FEMALE', 'PROFESSIONAL', 'Zagueira', 173, 64, '2000-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530025802458?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '110468ac-db98-439c-a42f-0f90aa59a592', 'Renata Silveira', 'Rê', 'atleta-fem-r--12',
    'FEMALE', 'PROFESSIONAL', 'Zagueira', 175, 65, '1998-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530028148136?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'fda8519d-a441-4d01-a49a-e3616d9b2086', 'Vanessa Pires', 'Vanessa', 'atleta-fem-vanessa-13',
    'FEMALE', 'PROFESSIONAL', 'Lateral-Direita', 168, 60, '2003-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530030493814?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '46e7fd11-c772-4e21-a4b2-e909c333b32f', 'Juliana Ferreira', 'Jú', 'atleta-fem-j--14',
    'FEMALE', 'PROFESSIONAL', 'Volante', 170, 63, '1996-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530032839492?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'a27ae5d4-982b-4fbb-aeb6-6864a14e2a7e', 'Patrícia Camargo', 'Paty', 'atleta-fem-paty-15',
    'FEMALE', 'PROFESSIONAL', 'Volante', 172, 64, '1997-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530035185170?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '2cdf8a28-d93f-4f81-a7a7-3aea0b01215e', 'Carolina Bastos', 'Carol', 'atleta-fem-carol-16',
    'FEMALE', 'PROFESSIONAL', 'Meia-Armadora', 166, 58, '1998-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530037530848?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd2e02610-9697-4d63-a5d8-e183c38a3767', 'Natália Guimarães', 'Naty', 'atleta-fem-naty-17',
    'FEMALE', 'PROFESSIONAL', 'Meia-Atacante', 165, 57, '2000-07-10', 'Rio Grande do Sul', 'BOTH',
    'https://images.unsplash.com/photo-1530039876526?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'c2ad027c-7a63-4bc7-a221-7c9bc5b17801', 'Thais Amaral', 'Thais', 'atleta-fem-thais-18',
    'FEMALE', 'PROFESSIONAL', 'Volante', 171, 63, '1999-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530042222204?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'a7b3a179-a589-43b1-a6b6-06093e657743', 'Débora Fagundes', 'Débora', 'atleta-fem-d-bora-19',
    'FEMALE', 'PROFESSIONAL', 'Meia-Central', 167, 59, '2001-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530044567882?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'edcccd84-3729-4b8b-a138-7bbdb5593ea3', 'Aline Moreira', 'Aline', 'atleta-fem-aline-20',
    'FEMALE', 'PROFESSIONAL', 'Meia-Atacante', 169, 60, '2002-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530046913560?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'ae1345ee-2cac-43cb-ae7f-269bf5d449af', 'Cristiane Becker', 'Cris', 'atleta-fem-cris-21',
    'FEMALE', 'PROFESSIONAL', 'Volante', 173, 65, '1995-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530049259238?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '37a1852c-d8a5-482c-a065-07b2f3ef6b93', 'Rafaela Dornelles', 'Rafa', 'atleta-fem-rafa-22',
    'FEMALE', 'PROFESSIONAL', 'Meia-Central', 168, 61, '2003-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530051604916?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '8ed4bd3d-9939-4a87-a741-cfc976db071a', 'Sãofia Ribeiro', 'Sãofia', 'atleta-fem-s-ofia-23',
    'FEMALE', 'PROFESSIONAL', 'Meia-Armadora', 165, 57, '2004-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530053950594?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '63c99ede-9a68-43f7-a581-7af713186aea', 'Andressa Alves', 'Dessa', 'atleta-fem-dessa-24',
    'FEMALE', 'PROFESSIONAL', 'Centroavante', 174, 66, '1997-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530056296272?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '771867b9-54d6-4304-a04f-bd7e5193886e', 'Milena Santos', 'Milena', 'atleta-fem-milena-25',
    'FEMALE', 'PROFESSIONAL', 'Ponta-Direita', 166, 58, '1999-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530058641950?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'a0d8f068-5220-4817-a300-fceaf1fcdef5', 'Kelly Cristine', 'Kelly', 'atleta-fem-kelly-26',
    'FEMALE', 'PROFESSIONAL', 'Ponta-Esquerda', 167, 59, '2000-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530060987628?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'ccb580da-38b3-498e-a21b-6538119230a1', 'Priscila Guedes', 'Pri', 'atleta-fem-pri-27',
    'FEMALE', 'PROFESSIONAL', 'Centroavante', 175, 67, '1998-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530063333306?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '0cb0c4a9-6327-43f7-a357-895b50233d4a', 'Nicole Zanetti', 'Nick', 'atleta-fem-nick-28',
    'FEMALE', 'PROFESSIONAL', 'Ponta-Esquerda', 165, 57, '2002-07-10', 'Rio Grande do Sul', 'LEFT',
    'https://images.unsplash.com/photo-1530065678984?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    'd9468919-e0c0-4a59-a782-c606202816b1', 'Bianca Toledo', 'Bianca', 'atleta-fem-bianca-29',
    'FEMALE', 'PROFESSIONAL', 'Ponta-Direita', 168, 60, '2001-07-10', 'Rio Grande do Sul', 'RIGHT',
    'https://images.unsplash.com/photo-1530068024662?auto=format&fit=crop&w=600&q=80', 'Atleta das Léobas da Boca com determinação, garra e paixão pelo Esporte Clube Pelotas.',
    '["Grêmio", "Internacional", "Juventude"]'::jsonb, true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '9d22c7b9-d7ec-420c-a8aa-9176f3f11f81', 'Cristiane Gambaróé', 'Cristiane', 'comissao-fem-cristiane-0', 'FEMALE', 'STAFF',
    'Treinadora', 'TREINADOR', '1982-04-12', 'Porto Alegre - RS', 'https://images.unsplash.com/photo-1540000000000?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica das Léobas da Boca no Esporte Clube Pelotas.', '["Corinthians Feminino", "Ferroviária"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '44e61e13-706e-44e1-a228-5e1bdaac76be', 'Paula Fernandes', 'Paula', 'comissao-fem-paula-1', 'FEMALE', 'STAFF',
    'Auxiliar Técnica', 'AUXILIAR_TECNICO', '1982-04-12', 'Porto Alegre - RS', 'https://images.unsplash.com/photo-1540000135791?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica das Léobas da Boca no Esporte Clube Pelotas.', '["Corinthians Feminino", "Ferroviária"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '016852e5-d141-496e-a726-5ea94f1f1955', 'Monique Rocha', 'Monique', 'comissao-fem-monique-2', 'FEMALE', 'STAFF',
    'Preparadora Física', 'PREPARADOR_FISICO', '1982-04-12', 'Porto Alegre - RS', 'https://images.unsplash.com/photo-1540000271582?auto=format&fit=crop&w=600&q=80',
    'Membro da comissão técnica das Léobas da Boca no Esporte Clube Pelotas.', '["Corinthians Feminino", "Ferroviária"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
-- 10. Alocação nos 2 Elencos Femininos
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('03b120d8-3279-42cc-a8d1-8d1fb53dae15', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'd85652f8-d86d-44b2-a14c-ac3d847f1db9', 1, 'GOALKEEPER', 'STARTER', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('432cee18-647b-4ab7-ac58-ed06290448ae', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'a95f95bd-6007-4e9d-a337-613abd2c1eb9', 2, 'GOALKEEPER', 'CAPTAIN', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('370cc1fc-f9fe-410b-a1dd-342927f7dc97', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '2758453e-5591-426e-a904-260f4b5d6fc6', 3, 'GOALKEEPER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('195e149e-6c49-4199-a64a-10ddbcb8a4f0', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '77c89b44-016a-41dc-a553-ffebeeacbdf6', 4, 'GOALKEEPER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c45ee615-d170-40e8-a1c4-02560205c071', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '11369b51-a488-488c-a9a2-e6cf40238a1b', 5, 'DEFENDER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6b6eaaec-2424-4398-aadf-4cdfb08b4fe5', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '1fa9024f-9753-4314-ad29-4b8ef13a4ca3', 6, 'DEFENDER', 'STARTER', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1a2b7204-3400-4e85-a83f-0b0b2300e2f8', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '5fd7fd6d-9164-410a-aa3d-99bd751e3870', 7, 'DEFENDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2e779d98-023c-4a3b-ad62-834c1e3d77e4', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'de7262dd-1099-4783-a0c1-c2aa316aae99', 8, 'DEFENDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('62f7ff4c-6d49-4c58-a13c-8c8adaa4690a', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'de383a38-b31d-431a-adc3-4aba392b4b3c', 9, 'DEFENDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b569e743-99f8-4071-a56a-fe524dd927c7', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '0c6c44d9-d979-4e89-ad6c-d0430bcc0e7e', 10, 'DEFENDER', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('06fa197c-999e-4308-ab93-935540ff2efa', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'd198540a-b82c-418f-a717-40783426c2f0', 11, 'DEFENDER', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('34af9dd9-bcc4-4e24-a76a-584772c8f49a', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '86bc1ce4-d6e2-4eed-ab47-6bb37053f205', 12, 'DEFENDER', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8d5cea20-4c19-43dd-aae4-c01365c1e8d4', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '110468ac-db98-439c-a42f-0f90aa59a592', 13, 'DEFENDER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e7b93c4f-8484-4350-a074-e99b931cb56b', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'fda8519d-a441-4d01-a49a-e3616d9b2086', 14, 'DEFENDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('22b1e625-74e6-4225-a63c-d7f79f7cfe43', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '46e7fd11-c772-4e21-a4b2-e909c333b32f', 15, 'MIDFIELDER', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9758d7af-fa90-4435-a252-2ae404b8b854', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'a27ae5d4-982b-4fbb-aeb6-6864a14e2a7e', 16, 'MIDFIELDER', 'SUBSTITUTE', 16, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b30f3b7d-adfd-4b7d-a1b3-45d31abca2d3', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '2cdf8a28-d93f-4f81-a7a7-3aea0b01215e', 17, 'MIDFIELDER', 'SUBSTITUTE', 17, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('614782e7-9400-4c4e-a3f1-a50659e3bd88', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'd2e02610-9697-4d63-a5d8-e183c38a3767', 18, 'MIDFIELDER', 'SUBSTITUTE', 18, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9770d1cb-c545-4e1d-a56f-b9cc3dfea0c5', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'c2ad027c-7a63-4bc7-a221-7c9bc5b17801', 19, 'MIDFIELDER', 'SUBSTITUTE', 19, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('7b205f8f-c781-42ca-a0af-0a3850313569', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'a7b3a179-a589-43b1-a6b6-06093e657743', 20, 'MIDFIELDER', 'SUBSTITUTE', 20, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e57afdc8-20b0-4a18-a4b0-de1cf1a968bf', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'edcccd84-3729-4b8b-a138-7bbdb5593ea3', 21, 'MIDFIELDER', 'SUBSTITUTE', 21, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ad65fa6a-60d8-4d99-a5f7-7bd192a84fb3', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', 'ae1345ee-2cac-43cb-ae7f-269bf5d449af', 22, 'MIDFIELDER', 'SUBSTITUTE', 22, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('3609f398-9ec8-4f45-ad9e-ff187fac4ece', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '9d22c7b9-d7ec-420c-a8aa-9176f3f11f81', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('73da4be6-8250-4e5d-a8a9-07e92cb58682', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '44e61e13-706e-44e1-a228-5e1bdaac76be', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9b53339f-4799-4ce2-a102-f5e1a32b48dd', '0b98da83-a21c-4a2c-a79d-39c2d25d1746', '016852e5-d141-496e-a726-5ea94f1f1955', NULL, 'STAFF', 'STARTER', 102, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('12f83ce3-c8ba-461a-afe0-11fa189ccbe8', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'de383a38-b31d-431a-adc3-4aba392b4b3c', 1, 'DEFENDER', 'CAPTAIN', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c942a9f4-3de2-4588-a057-a090fd9a7cca', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '0c6c44d9-d979-4e89-ad6c-d0430bcc0e7e', 2, 'DEFENDER', 'STARTER', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9c94509b-fe92-419d-a4a2-ac022bb44250', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'd198540a-b82c-418f-a717-40783426c2f0', 3, 'DEFENDER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('dcdc1931-940e-4b8d-a7ee-3c596bac9cfc', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '86bc1ce4-d6e2-4eed-ab47-6bb37053f205', 4, 'DEFENDER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e5b315c5-5273-4459-ae5c-a6f7505bb036', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '110468ac-db98-439c-a42f-0f90aa59a592', 5, 'DEFENDER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('d211a1b7-d44c-47bd-ac24-0bee14960529', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'fda8519d-a441-4d01-a49a-e3616d9b2086', 6, 'DEFENDER', 'STARTER', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('43f946aa-de3c-4ca2-ab33-90555327fb7a', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '46e7fd11-c772-4e21-a4b2-e909c333b32f', 7, 'MIDFIELDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6c4537a6-f44c-4217-a0c7-61b4b8150438', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'a27ae5d4-982b-4fbb-aeb6-6864a14e2a7e', 8, 'MIDFIELDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('6d649212-d02e-474c-a269-4d4ff0aafd14', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '2cdf8a28-d93f-4f81-a7a7-3aea0b01215e', 9, 'MIDFIELDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('b35c13b9-8551-490f-ae16-f24eadfb1dab', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'd2e02610-9697-4d63-a5d8-e183c38a3767', 10, 'MIDFIELDER', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('42c0d5a4-06b1-404c-a754-087ab8cec153', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'c2ad027c-7a63-4bc7-a221-7c9bc5b17801', 11, 'MIDFIELDER', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('3147bdc7-213f-4ba2-ad7c-8d030a54fe6e', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'a7b3a179-a589-43b1-a6b6-06093e657743', 12, 'MIDFIELDER', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('fb58b98b-930b-4938-a25a-9714b4404da7', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'edcccd84-3729-4b8b-a138-7bbdb5593ea3', 13, 'MIDFIELDER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2124a223-4c93-4dd0-a8e1-5c5a5a044609', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'ae1345ee-2cac-43cb-ae7f-269bf5d449af', 14, 'MIDFIELDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('cd7f8de3-0320-4e49-aa98-eaa3546bae47', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '37a1852c-d8a5-482c-a065-07b2f3ef6b93', 15, 'MIDFIELDER', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('7145deba-37c3-4751-a3b4-6b27742041d6', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '8ed4bd3d-9939-4a87-a741-cfc976db071a', 16, 'MIDFIELDER', 'SUBSTITUTE', 16, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ecc6241c-846b-4124-ad05-a21e65e6251b', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '63c99ede-9a68-43f7-a581-7af713186aea', 17, 'FORWARD', 'SUBSTITUTE', 17, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('690f2876-dd94-4213-a339-82df484076b0', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '771867b9-54d6-4304-a04f-bd7e5193886e', 18, 'FORWARD', 'SUBSTITUTE', 18, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('38a89f38-6fae-49fb-aadb-931d07843533', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'a0d8f068-5220-4817-a300-fceaf1fcdef5', 19, 'FORWARD', 'SUBSTITUTE', 19, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('09d8d8bf-bf82-4757-a6e0-dcc87ae47c7b', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'ccb580da-38b3-498e-a21b-6538119230a1', 20, 'FORWARD', 'SUBSTITUTE', 20, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9c88189b-1e29-473e-a789-ec495072a084', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '0cb0c4a9-6327-43f7-a357-895b50233d4a', 21, 'FORWARD', 'SUBSTITUTE', 21, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('52f71b0f-defe-4eb7-a830-8d946d39466b', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', 'd9468919-e0c0-4a59-a782-c606202816b1', 22, 'FORWARD', 'SUBSTITUTE', 22, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('997f6ede-3f78-4b78-a00a-ace13d1abbac', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '9d22c7b9-d7ec-420c-a8aa-9176f3f11f81', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8667ad00-72f1-4aed-ad74-c3ef9fa233ae', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '44e61e13-706e-44e1-a228-5e1bdaac76be', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('478b0775-4d0c-4536-a53a-49cd3fef6d3d', 'ea63133e-3cb6-4cb6-ade8-33b0ac337a41', '016852e5-d141-496e-a726-5ea94f1f1955', NULL, 'STAFF', 'STARTER', 102, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
-- 11. Inserção de Atletas de Categorias de Base (30 atletas: Sub-20 e Sub-17)
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'bb6ea546-91f8-4d56-a6f3-1678150a362c', 'Matheus Henrique Silveira', 'Matheusinho', 'atleta-base-matheusinho-0',
    'MALE', 'YOUTH', 'Meia-Atacante', 'SUB_20', 2022,
    172, 66, '2006-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520000000000?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '710f6ec8-3495-40a3-a94d-b360d270358b', 'Arthur Gabriel Fontana', 'Arthur', 'atleta-base-arthur-1',
    'MALE', 'YOUTH', 'Goleiro', 'SUB_20', 2021,
    190, 84, '2005-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520003456789?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'e827afb3-1cdc-4688-a70f-0682611e447e', 'Felipe Fagundes da Rosa', 'Fagundes', 'atleta-base-fagundes-2',
    'MALE', 'YOUTH', 'Zagueiro', 'SUB_20', 2022,
    187, 81, '2005-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520006913578?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '28abe90e-45c0-4f67-a5ee-c6ab1e06fbd7', 'Gabriel Meireles', 'Meireles', 'atleta-base-meireles-3',
    'MALE', 'YOUTH', 'Zagueiro', 'SUB_20', 2023,
    186, 80, '2006-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520010370367?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '30127dea-33b7-4838-a0a9-0d9b7e02f9a6', 'Lucas Dornelles', 'Dornelles', 'atleta-base-dornelles-4',
    'MALE', 'YOUTH', 'Lateral-Esquerdo', 'SUB_20', 2022,
    175, 71, '2005-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520013827156?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '83977266-6eb2-42e9-a2f2-78d4572f22ea', 'Gustavo Farias', 'Guga', 'atleta-base-guga-5',
    'MALE', 'YOUTH', 'Lateral-Direito', 'SUB_20', 2023,
    178, 73, '2006-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520017283945?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '3d735569-375e-453d-aea6-28951cbeb865', 'Enzo Becker', 'Enzo', 'atleta-base-enzo-6',
    'MALE', 'YOUTH', 'Volante', 'SUB_20', 2021,
    181, 77, '2005-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520020740734?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '3be3f2e2-4bf7-4b6b-ad96-a185d2b06927', 'Nicolas Pires', 'Nico', 'atleta-base-nico-7',
    'MALE', 'YOUTH', 'Volante', 'SUB_20', 2023,
    183, 79, '2006-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520024197523?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '60d33437-d60f-4b89-a25b-f2094f25ff8d', 'Vitor Hugo Costa', 'Vitinho', 'atleta-base-vitinho-8',
    'MALE', 'YOUTH', 'Meia-Central', 'SUB_20', 2022,
    176, 70, '2005-03-25', 'Pelotas - RS', 'BOTH',
    'https://images.unsplash.com/photo-1520027654312?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'd9350eba-7091-40f0-a31b-53f4c8743ade', 'Pedrão Henrique Amaral', 'Pedrinho', 'atleta-base-pedrinho-9',
    'MALE', 'YOUTH', 'Ponta-Direita', 'SUB_20', 2023,
    173, 67, '2006-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520031111101?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'f3d0d1a8-8cf2-46c9-a72d-1d5e33853f61', 'Kauêã Oliveira', 'Kauêã', 'atleta-base-kau---10',
    'MALE', 'YOUTH', 'Centroavante', 'SUB_20', 2021,
    185, 82, '2005-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520034567890?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '87b16f4a-0e36-4c52-a109-9580a4c066cb', 'Bernardo Rangel', 'Bernardo', 'atleta-base-bernardo-11',
    'MALE', 'YOUTH', 'Ponta-Esquerda', 'SUB_20', 2023,
    174, 68, '2006-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520038024679?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '119c3bc5-d482-4afe-a83e-364218af4530', 'Leonardo Brizola', 'Brizola', 'atleta-base-brizola-12',
    'MALE', 'YOUTH', 'Goleiro', 'SUB_20', 2022,
    188, 82, '2006-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520041481468?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '3b3e626f-86fc-4292-a5d6-e7bb0f85b9f9', 'Samuel Vargas', 'Samuca', 'atleta-base-samuca-13',
    'MALE', 'YOUTH', 'Volante', 'SUB_20', 2022,
    180, 75, '2005-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520044938257?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'e18846d4-a2f5-44cf-a413-afaf8f9e60c1', 'Thiago Medeiros', 'Thiaguinho', 'atleta-base-thiaguinho-14',
    'MALE', 'YOUTH', 'Ponta-Direita', 'SUB_20', 2023,
    171, 65, '2006-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520048395046?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'f0d56c14-4b8a-494f-ad93-7cede2fb6a1f', 'Caio Lucca Santos', 'Caio', 'atleta-base-caio-15',
    'MALE', 'YOUTH', 'Goleiro', 'SUB_17', 2023,
    186, 78, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520051851835?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '47babb75-3b3d-4c4c-a379-5b7271c88b64', 'Rafael Antunes', 'Rafa', 'atleta-base-rafa-16',
    'MALE', 'YOUTH', 'Zagueiro', 'SUB_17', 2024,
    184, 76, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520055308624?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'c3d71c78-7b0e-4219-a41c-6af700415fdd', 'Guilherme Siqueira', 'Gui', 'atleta-base-gui-17',
    'MALE', 'YOUTH', 'Zagueiro', 'SUB_17', 2023,
    183, 75, '2008-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520058765413?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '9e7621e8-9608-40ad-a64b-b5a34506d95a', 'Murilo Vargas', 'Murilo', 'atleta-base-murilo-18',
    'MALE', 'YOUTH', 'Lateral-Esquerdo', 'SUB_17', 2024,
    172, 66, '2009-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520062222202?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'b9ceaeaf-9fe2-454a-a30d-c93727789594', 'Davi Lucas Neves', 'Davi', 'atleta-base-davi-19',
    'MALE', 'YOUTH', 'Lateral-Direito', 'SUB_17', 2023,
    174, 68, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520065678991?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'df95b9d9-dd6c-4b07-a607-99847f1132db', 'João Pedrão Lima', 'JP', 'atleta-base-jp-20',
    'MALE', 'YOUTH', 'Volante', 'SUB_17', 2023,
    177, 72, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520069135780?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'b52a4390-d244-4fec-a4fc-ab2dcbd383a1', 'Heitor Silveira', 'Heitor', 'atleta-base-heitor-21',
    'MALE', 'YOUTH', 'Volante', 'SUB_17', 2024,
    179, 74, '2009-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520072592569?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '26a426ec-0199-4e2c-aaf6-a5ac3e33a212', 'Breno Martins', 'Breno', 'atleta-base-breno-22',
    'MALE', 'YOUTH', 'Meia-Armador', 'SUB_17', 2023,
    170, 64, '2008-03-25', 'Pelotas - RS', 'BOTH',
    'https://images.unsplash.com/photo-1520076049358?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '1c0b545c-896e-4bf3-a92b-034953681e67', 'Lucas Eduardo Rosa', 'Dudu', 'atleta-base-dudu-23',
    'MALE', 'YOUTH', 'Meia-Atacante', 'SUB_17', 2024,
    171, 65, '2009-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520079506147?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'bd8f79f0-d8ac-4d19-ac31-3523d7d5524e', 'Gabriel Alencar', 'Biel', 'atleta-base-biel-24',
    'MALE', 'YOUTH', 'Ponta-Direita', 'SUB_17', 2023,
    169, 63, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520082962936?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '4cce16c0-4f14-419f-a53f-5982118f5662', 'Matheus Trindade', 'Trindade', 'atleta-base-trindade-25',
    'MALE', 'YOUTH', 'Centroavante', 'SUB_17', 2023,
    182, 78, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520086419725?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    'c9bae41d-88cc-4c42-ada5-2ade3af1ca42', 'Enzo Gabriel Castro', 'Castro', 'atleta-base-castro-26',
    'MALE', 'YOUTH', 'Ponta-Esquerda', 'SUB_17', 2024,
    172, 66, '2009-03-25', 'Pelotas - RS', 'LEFT',
    'https://images.unsplash.com/photo-1520089876514?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '7c9177df-f21d-4e1e-aef8-21b549edd861', 'Vinícius Bonorino', 'Vini', 'atleta-base-vini-27',
    'MALE', 'YOUTH', 'Goleiro', 'SUB_17', 2023,
    185, 77, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520093333303?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '6675cf78-9d23-4e87-a333-bf1354d004d8', 'Otávio Macedo', 'Otávio', 'atleta-base-ot-vio-28',
    'MALE', 'YOUTH', 'Zagueiro', 'SUB_17', 2024,
    185, 77, '2008-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520096790092?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, youth_category, entry_year,
    height, weight, birth_date, birth_place, preferred_foot,
    photo_url, biography, is_active, created_at, updated_at
)
VALUES (
    '38b97983-4222-49d3-a755-3c9996f76edb', 'Pedrão Lucas Correa', 'Pedrão', 'atleta-base-pedr-o-29',
    'MALE', 'YOUTH', 'Centroavante', 'SUB_17', 2024,
    184, 80, '2009-03-25', 'Pelotas - RS', 'RIGHT',
    'https://images.unsplash.com/photo-1520100246881?auto=format&fit=crop&w=600&q=80', 'Cria da Boca lapidada nas categorias de formação com disciplina, talento e DNA áureo-cerúleo.',
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '0ab52841-a42c-41db-a979-1e98fb84629e', 'Tiago Pereira', 'Tiago', 'comissao-base-tiago-0', 'MALE', 'STAFF',
    'Treinador Sub-20', 'TREINADOR', '1985-09-18', 'Pelotas - RS', 'https://images.unsplash.com/photo-1505000000000?auto=format&fit=crop&w=600&q=80',
    'Profissional dedicado à à formação e lapidação das Crias da Boca no Paráque Lobo.', '["Pelotas Base"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '2c00c7f9-be67-4f17-aa67-c7425829b6e2', 'Rodrigo Sãouza', 'Rodrigo', 'comissao-base-rodrigo-1', 'MALE', 'STAFF',
    'Treinador Sub-17', 'TREINADOR', '1985-09-18', 'Pelotas - RS', 'https://images.unsplash.com/photo-1505000246810?auto=format&fit=crop&w=600&q=80',
    'Profissional dedicado à à formação e lapidação das Crias da Boca no Paráque Lobo.', '["Pelotas Base"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '7694ff82-3596-4f41-a9f1-f1fedae1ed52', 'Vinicius Silva', 'Vinicius', 'comissao-base-vinicius-2', 'MALE', 'STAFF',
    'Preparador Físico da Base', 'PREPARADOR_FISICO', '1985-09-18', 'Pelotas - RS', 'https://images.unsplash.com/photo-1505000493620?auto=format&fit=crop&w=600&q=80',
    'Profissional dedicado à à formação e lapidação das Crias da Boca no Paráque Lobo.', '["Pelotas Base"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
INSERT INTO public.athletes (
    id, name, nickname, slug, gender, athlete_type, primary_position, staff_role,
    birth_date, birth_place, photo_url, biography, past_clubs, is_active, created_at, updated_at
)
VALUES (
    '6001be60-a8c5-4538-ad98-993eb5d2b358', 'Daniel Oliveira', 'Daniel', 'comissao-base-daniel-3', 'MALE', 'STAFF',
    'Treinador de Goleiros da Base', 'PREPARADOR_GOLEIROS', '1985-09-18', 'Pelotas - RS', 'https://images.unsplash.com/photo-1505000740430?auto=format&fit=crop&w=600&q=80',
    'Profissional dedicado à à formação e lapidação das Crias da Boca no Paráque Lobo.', '["Pelotas Base"]'::jsonb,
    true, NOW(), NOW()
)
ON CONFLICT (slug) DO NOTHING;
-- 12. Alocação nos 2 Elencos das Categorias de Base
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('a892d55d-ac8f-4fc7-ada8-29586e5cc849', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', 'bb6ea546-91f8-4d56-a6f3-1678150a362c', 1, 'MIDFIELDER', 'CAPTAIN', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c78bec01-d43c-4c7c-a802-03de2576a59c', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '710f6ec8-3495-40a3-a94d-b360d270358b', 2, 'GOALKEEPER', 'STARTER', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ae7ab913-c4c9-4d5f-a141-bed54d72db9d', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', 'e827afb3-1cdc-4688-a70f-0682611e447e', 3, 'DEFENDER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e28e44e3-6a4a-4d0e-a724-820b2c3cda8c', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '28abe90e-45c0-4f67-a5ee-c6ab1e06fbd7', 4, 'DEFENDER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('509c5402-d7ac-4ce0-a113-8d4af4be939d', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '30127dea-33b7-4838-a0a9-0d9b7e02f9a6', 5, 'DEFENDER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ab7bbc64-46b1-4f57-a386-78887f98227e', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '83977266-6eb2-42e9-a2f2-78d4572f22ea', 6, 'DEFENDER', 'STARTER', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('e64ee2c8-26f8-48bc-aa1a-672f51df1af6', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '3d735569-375e-453d-aea6-28951cbeb865', 7, 'MIDFIELDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('cc7d0f67-01c7-477a-a641-61ed2f9f79b8', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '3be3f2e2-4bf7-4b6b-ad96-a185d2b06927', 8, 'MIDFIELDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('83710244-56d3-402d-ad24-217100958883', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '60d33437-d60f-4b89-a25b-f2094f25ff8d', 9, 'MIDFIELDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('921e64d8-74cd-4c34-a4dc-ab50072ea9e5', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', 'd9350eba-7091-40f0-a31b-53f4c8743ade', 10, 'FORWARD', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f4add730-9b48-4c4a-a9fe-088ffc801f22', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', 'f3d0d1a8-8cf2-46c9-a72d-1d5e33853f61', 11, 'FORWARD', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('873b1a23-c4c2-490a-a5a5-b1a95f455471', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '87b16f4a-0e36-4c52-a109-9580a4c066cb', 12, 'FORWARD', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('949d1245-5992-4a19-a7a8-5c87db6f42fc', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '119c3bc5-d482-4afe-a83e-364218af4530', 13, 'GOALKEEPER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('67898a6a-d0fa-4f21-a266-189bfd2e670f', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '3b3e626f-86fc-4292-a5d6-e7bb0f85b9f9', 14, 'MIDFIELDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('f1e203fe-1ea0-461c-a943-66a614256c68', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', 'e18846d4-a2f5-44cf-a413-afaf8f9e60c1', 15, 'FORWARD', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1dc1a67d-af50-4fdb-ab40-86fb97b66d6d', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '0ab52841-a42c-41db-a979-1e98fb84629e', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8e8072a8-45fc-467a-a8c3-96c6ccda5598', '9cd6151f-c1ae-4009-af5e-ac3d9ead1725', '7694ff82-3596-4f41-a9f1-f1fedae1ed52', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('d8daebdf-c77b-45c6-a433-7af1ca60a208', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'f0d56c14-4b8a-494f-ad93-7cede2fb6a1f', 1, 'GOALKEEPER', 'CAPTAIN', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1169eb7d-5203-4b73-ade0-fb269443cfc1', '8d185cec-97f2-4924-a180-4b813e3efa4c', '47babb75-3b3d-4c4c-a379-5b7271c88b64', 2, 'DEFENDER', 'STARTER', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('2ad2d3ea-0c99-40aa-a287-b6d1d6487d64', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'c3d71c78-7b0e-4219-a41c-6af700415fdd', 3, 'DEFENDER', 'STARTER', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('60a512e7-95f5-414c-ae5a-7bc74425d794', '8d185cec-97f2-4924-a180-4b813e3efa4c', '9e7621e8-9608-40ad-a64b-b5a34506d95a', 4, 'DEFENDER', 'STARTER', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('d22e408e-3f04-4c44-abbd-789e1a6044de', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'b9ceaeaf-9fe2-454a-a30d-c93727789594', 5, 'DEFENDER', 'STARTER', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9940bb70-9fa6-4438-af7b-bb20183e8a52', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'df95b9d9-dd6c-4b07-a607-99847f1132db', 6, 'MIDFIELDER', 'STARTER', 6, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('c29c01a8-6724-44fa-a23d-950e4f6ee126', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'b52a4390-d244-4fec-a4fc-ab2dcbd383a1', 7, 'MIDFIELDER', 'STARTER', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('9a8468f6-a01f-4fb4-a4f0-7fdb233c1123', '8d185cec-97f2-4924-a180-4b813e3efa4c', '26a426ec-0199-4e2c-aaf6-a5ac3e33a212', 8, 'MIDFIELDER', 'STARTER', 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('cf80045b-f1e5-4fb2-afb6-c8c6a86d1e6a', '8d185cec-97f2-4924-a180-4b813e3efa4c', '1c0b545c-896e-4bf3-a92b-034953681e67', 9, 'MIDFIELDER', 'STARTER', 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('5b8580b8-4ea3-45ae-a3e5-4420d93a954b', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'bd8f79f0-d8ac-4d19-ac31-3523d7d5524e', 10, 'FORWARD', 'STARTER', 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('4b010b42-844b-4140-ac45-babbe041cd61', '8d185cec-97f2-4924-a180-4b813e3efa4c', '4cce16c0-4f14-419f-a53f-5982118f5662', 11, 'FORWARD', 'STARTER', 11, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('53312bc0-54cb-467e-a3c6-329b1d2bb3c6', '8d185cec-97f2-4924-a180-4b813e3efa4c', 'c9bae41d-88cc-4c42-ada5-2ade3af1ca42', 12, 'FORWARD', 'SUBSTITUTE', 12, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('a1f7b5ae-8666-470e-aabc-f90356827cd3', '8d185cec-97f2-4924-a180-4b813e3efa4c', '7c9177df-f21d-4e1e-aef8-21b549edd861', 13, 'GOALKEEPER', 'SUBSTITUTE', 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8426ea83-ebdc-43a5-a4eb-aa4ad488f191', '8d185cec-97f2-4924-a180-4b813e3efa4c', '6675cf78-9d23-4e87-a333-bf1354d004d8', 14, 'DEFENDER', 'SUBSTITUTE', 14, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('1aaa00e5-acdc-4670-aa6e-8200f6174263', '8d185cec-97f2-4924-a180-4b813e3efa4c', '38b97983-4222-49d3-a755-3c9996f76edb', 15, 'FORWARD', 'SUBSTITUTE', 15, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('ff3a8d0b-fee5-4207-af8c-f9330d5bb890', '8d185cec-97f2-4924-a180-4b813e3efa4c', '2c00c7f9-be67-4f17-aa67-c7425829b6e2', NULL, 'STAFF', 'STARTER', 100, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.squad_members (id, squad_id, athlete_id, jersey_number, sector, role, display_order, created_at, updated_at)
VALUES ('8263b6a8-0379-42f7-a902-aab40171dba5', '8d185cec-97f2-4924-a180-4b813e3efa4c', '6001be60-a8c5-4538-ad98-993eb5d2b358', NULL, 'STAFF', 'STARTER', 101, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
