-- V51__add_newsletter_dispatch_and_unsubscribe.sql

ALTER TABLE newsletter_subscribers
    ADD COLUMN IF NOT EXISTS unsubscribe_token VARCHAR(64);

UPDATE newsletter_subscribers
SET unsubscribe_token = gen_random_uuid()::text
WHERE unsubscribe_token IS NULL OR unsubscribe_token = '';

ALTER TABLE newsletter_subscribers
    ALTER COLUMN unsubscribe_token SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_newsletter_unsubscribe_token
    ON newsletter_subscribers(unsubscribe_token);

ALTER TABLE news_articles
    ADD COLUMN IF NOT EXISTS send_newsletter BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE news_articles
    ADD COLUMN IF NOT EXISTS newsletter_sent_at TIMESTAMPTZ;

