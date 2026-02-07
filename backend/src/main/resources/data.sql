-- Sample test accounts for money-transfer-system
-- Insert 5 test accounts with varying balances and statuses

INSERT INTO ACCOUNTS (holder_name, balance, status, version, last_updated) VALUES
('John Doe', 10000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Jane Smith', 25000.50, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Bob Johnson', 5000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Alice Williams', 15000.75, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('Charlie Brown', 8500.25, 'ACTIVE', 0, CURRENT_TIMESTAMP);
