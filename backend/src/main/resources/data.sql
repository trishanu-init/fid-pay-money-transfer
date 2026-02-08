-- Sample test accounts for money-transfer-system
-- Insert 5 test accounts with varying balances and statuses

INSERT INTO ACCOUNTS (holder_name, email, balance, status, version, last_updated) VALUES
('John Doe', 'trishanu8295@gmail.com', 10000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Jane Smith', 'trishanu8295@gmail.com', 25000.50, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Bob Johnson', 'bob.johnson@example.com', 5000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Alice Williams', 'alice.williams@example.com', 15000.75, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Charlie Brown', 'charlie.brown@example.com', 8500.25, 'ACTIVE', 0, CURRENT_TIMESTAMP);
