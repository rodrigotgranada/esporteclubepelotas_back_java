-- V52__add_newsletter_unsubscribe_reason.sql

ALTER TABLE newsletter_subscribers
    ADD COLUMN IF NOT EXISTS unsubscribe_reason VARCHAR(255);

ALTER TABLE newsletter_subscribers
    ADD COLUMN IF NOT EXISTS unsubscribed_at TIMESTAMPTZ;

