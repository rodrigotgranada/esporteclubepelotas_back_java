-- Migration V41: Adição das configurações da Loja Oficial Lobo Mania
-- Permite controle 100% administrativo de canais (WhatsApp, Instagram), loja virtual e desconto de sócio

ALTER TABLE public.settings
    ADD COLUMN IF NOT EXISTS store_name VARCHAR(150) DEFAULT 'Loja Oficial Lobo Mania',
    ADD COLUMN IF NOT EXISTS store_description TEXT DEFAULT 'Vista o manto áureo-cerúleo e apoie o Esporte Clube Pelotas. Produtos oficiais, camisas de jogo, linha casual e acessórios exclusivos.',
    ADD COLUMN IF NOT EXISTS store_external_url VARCHAR(500) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS store_is_online_active BOOLEAN DEFAULT false,
    ADD COLUMN IF NOT EXISTS store_instagram_url VARCHAR(255) DEFAULT 'https://www.instagram.com/lojadolobo/',
    ADD COLUMN IF NOT EXISTS store_whatsapp_number VARCHAR(50) DEFAULT '5553999999999',
    ADD COLUMN IF NOT EXISTS store_whatsapp_message VARCHAR(255) DEFAULT 'Olá! Gostaria de informações sobre produtos e mantos oficiais do E.C. Pelotas.',
    ADD COLUMN IF NOT EXISTS store_opening_hours VARCHAR(255) DEFAULT 'Segunda a Sexta: 09h às 19h | Sábado: 09h às 13h | Dias de jogos na Boca do Lobo: até o intervalo',
    ADD COLUMN IF NOT EXISTS store_address VARCHAR(255) DEFAULT 'Estádio Boca do Lobo - Av. Bento Gonçalves esquina Parque Dom Antônio Zattera - Pelotas/RS',
    ADD COLUMN IF NOT EXISTS store_has_member_discount BOOLEAN DEFAULT true,
    ADD COLUMN IF NOT EXISTS store_member_discount_text VARCHAR(255) DEFAULT 'Sócios do Pelotas em dia têm desconto exclusivo em produtos e mantos oficiais mediante apresentação da carteirinha física ou digital.';
