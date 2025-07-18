-- Create tags table
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create translations table
CREATE TABLE translations (
    id BIGSERIAL PRIMARY KEY,
    translation_key VARCHAR(500) NOT NULL,
    locale VARCHAR(10) NOT NULL,
    content VARCHAR(5000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_translation_key_locale UNIQUE (translation_key, locale)
);

-- Create translation_tags junction table
CREATE TABLE translation_tags (
    translation_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (translation_id, tag_id),
    CONSTRAINT fk_translation_tags_translation FOREIGN KEY (translation_id) REFERENCES translations(id) ON DELETE CASCADE,
    CONSTRAINT fk_translation_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_translation_key ON translations(translation_key);
CREATE INDEX idx_locale ON translations(locale);
CREATE INDEX idx_content_fulltext ON translations USING gin(to_tsvector('english', content));
CREATE INDEX idx_updated_at ON translations(updated_at);
CREATE INDEX idx_tag_name ON tags(name);
CREATE INDEX idx_translation_tags_translation ON translation_tags(translation_id);
CREATE INDEX idx_translation_tags_tag ON translation_tags(tag_id);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_translations_updated_at BEFORE UPDATE ON translations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tags_updated_at BEFORE UPDATE ON tags
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
