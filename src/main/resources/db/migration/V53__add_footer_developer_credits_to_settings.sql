-- V53__add_footer_developer_credits_to_settings.sql

ALTER TABLE settings
    ADD COLUMN IF NOT EXISTS developer_credit_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS developer_credit_name VARCHAR(120),
    ADD COLUMN IF NOT EXISTS developer_credit_url VARCHAR(500);

