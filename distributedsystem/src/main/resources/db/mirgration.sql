CREATE TABLE IF NOT EXISTS transaction ( transaction_id VARCHAR(255) PRIMARY KEY, status VARCHAR(50), amount DECIMAL(19, 2), currency VARCHAR(10), description TEXT, user_id VARCHAR(255)); 
