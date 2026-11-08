-- V28: Adiciona configuração do provedor do sistema de sócios (ElevenTickets vs Nativo)
ALTER TABLE settings 
ADD COLUMN IF NOT EXISTS membership_system_provider VARCHAR(50) NOT NULL DEFAULT 'ELEVENTICKETS',
ADD COLUMN IF NOT EXISTS membership_external_url TEXT NOT NULL DEFAULT 'https://socioecpelotas.eleventickets.com/#/';
