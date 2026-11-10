-- Migration V42: Adiciona foto de fachada da Loja Alcateia e mensagens personalizadas de canais (WhatsApp e Instagram)
-- Ajusta nome oficial da loja para 'Alcateia' e atualiza o Instagram oficial

ALTER TABLE public.settings
    ADD COLUMN IF NOT EXISTS store_facade_url VARCHAR(500) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS store_whatsapp_description VARCHAR(255) DEFAULT 'Consulte tamanhos disponíveis, envie seu comprovante e garanta seu manto com atendimento personalizado.',
    ADD COLUMN IF NOT EXISTS store_instagram_description VARCHAR(255) DEFAULT 'Acompanhe lançamentos de uniformes, bastidores da boutique, reposições e novidades diárias em primeira mão.';

-- Atualiza nome padrão e Instagram para Alcateia caso ainda esteja com os valores legados
UPDATE public.settings
SET store_name = 'Alcateia'
WHERE store_name = 'Loja Oficial Lobo Mania' OR store_name IS NULL;

UPDATE public.settings
SET store_instagram_url = 'https://www.instagram.com/alcateia.ecpelotas/'
WHERE store_instagram_url = 'https://www.instagram.com/lojadolobo/' OR store_instagram_url IS NULL;

