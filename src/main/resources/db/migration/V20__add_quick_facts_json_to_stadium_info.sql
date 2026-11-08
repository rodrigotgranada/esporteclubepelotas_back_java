-- V20: Adiciona suporte a cards dinâmicos na Ficha Técnica da Boca do Lobo
ALTER TABLE public.stadium_info 
ADD COLUMN IF NOT EXISTS quick_facts_json jsonb NOT NULL DEFAULT '[]'::jsonb;

-- Preenche os 6 cards clássicos da Boca do Lobo caso a coluna esteja vazia
UPDATE public.stadium_info
SET quick_facts_json = '[
  {"id":"fact-1","label":"Fundação / Inauguração","value":"25 de Outubro de 1908","icon":"calendar","order":1},
  {"id":"fact-2","label":"Capacidade Oficial","value":"23.336 espectadores","icon":"users","order":2},
  {"id":"fact-3","label":"Dimensões do Gramado","value":"105m x 68m (Padrão Oficial)","icon":"maximize","order":3},
  {"id":"fact-4","label":"Alcunha Mística","value":"A Boca do Lobo","icon":"flame","order":4},
  {"id":"fact-5","label":"Pioneirismo de Refletores","value":"Desde 1930 (1º do interior do RS)","icon":"zap","order":5},
  {"id":"fact-6","label":"Localização Nobre","value":"Avenida Bento Gonçalves com Gonçalves Chaves","icon":"mapPin","order":6}
]'::jsonb
WHERE id = 1 AND (quick_facts_json IS NULL OR quick_facts_json = '[]'::jsonb);
