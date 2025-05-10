-- Create accounts table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nickname VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    email_confirmation_token VARCHAR(255),
    email_confirmation_token_expiry TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_login_at TIMESTAMP WITH TIME ZONE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create index on email for faster authentication lookups
CREATE INDEX idx_accounts_email ON accounts(email);

-- Create index on nickname for faster authentication lookups
CREATE INDEX idx_accounts_nickname ON accounts(nickname);

-- Create profiles table
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    private_profile BOOLEAN NOT NULL DEFAULT FALSE,
    bio TEXT,
    avatar_url VARCHAR(512),
    location VARCHAR(255),
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    theme VARCHAR(20) NOT NULL DEFAULT 'light',
    auto_play_videos BOOLEAN NOT NULL DEFAULT TRUE,
    save_data_mode BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create unique index to ensure one profile per account
CREATE UNIQUE INDEX idx_profiles_account_id ON profiles(account_id);