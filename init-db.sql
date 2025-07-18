-- Initialize the translation database
-- This file is used by Docker to set up the initial database structure

-- Create the database if it doesn't exist (this is usually handled by Docker environment variables)
-- CREATE DATABASE translation_db IF NOT EXISTS;

-- Create the user if it doesn't exist (this is usually handled by Docker environment variables)
-- CREATE USER translation_user WITH PASSWORD 'translation_pass';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE translation_db TO translation_user;

-- Note: The actual schema creation is handled by Flyway migrations
-- This file is mainly for any additional initialization that might be needed
-- The Flyway migrations in src/main/resources/db/migration/ will handle:
-- - Creating tables (V1__Create_initial_schema.sql)
-- - Inserting sample data (V2__Insert_sample_data.sql)

-- Set timezone
SET timezone = 'UTC';

-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log initialization
SELECT 'Database initialization completed' AS status;
