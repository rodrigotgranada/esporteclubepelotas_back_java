-- ==========================================================
-- Migration V45: Inclusão de Módulos Faltantes do Sistema (Feature Flags)
-- ==========================================================

INSERT INTO public.system_modules (id, name, slug, description, is_active, created_at, updated_at)
VALUES
  ('m-female-001', 'Futebol Feminino (Lobas)', 'female', 'Hub dedicado, elenco, comissão, notícias e competições das Lobas do Pelotas', true, NOW(), NOW()),
  ('m-youth-001', 'Categorias de Base', 'youth', 'Hub dedicado, elencos sub-15, sub-17, sub-20 e formação de atletas', true, NOW(), NOW()),
  ('m-store-001', 'Loja Oficial Alcateia', 'store', 'Boutique oficial do Pelotas, catálogo de produtos, integração WhatsApp e Instagram', true, NOW(), NOW()),
  ('m-stadium-001', 'Estádio Boca do Lobo', 'stadium', 'História, mapa, setores, capacidade e tour virtual da Boca do Lobo', true, NOW(), NOW()),
  ('m-sponsors-001', 'Patrocinadores Oficiais', 'sponsors', 'Vitrine e faixas oficiais de patrocinadores do clube, base e feminino', true, NOW(), NOW()),
  ('m-membership-001', 'Quadro Social (Sócios)', 'membership', 'Portal de planos de sócio, adesão online e benefícios exclusivos', true, NOW(), NOW()),
  ('m-tickets-001', 'Ingressos & Bilheteria', 'tickets', 'Venda e informações de ingressos para partidas no Estádio Boca do Lobo', true, NOW(), NOW())
ON CONFLICT (slug) DO UPDATE
SET name = EXCLUDED.name,
    description = EXCLUDED.description,
    updated_at = NOW();

