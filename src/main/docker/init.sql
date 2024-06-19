-- Create database if not exists
CREATE DATABASE hsds OWNER hs;

-- Connect to the database
\c hsds;

-- Create table if not exists
CREATE TABLE IF NOT EXISTS transaction (
    transaction_id SERIAL PRIMARY KEY,
    status VARCHAR(50),
    amount DECIMAL(19, 2),
    currency VARCHAR(10),
    description TEXT,
    user_id VARCHAR(255)
);
