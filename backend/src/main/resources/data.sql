-- Sample test accounts for money-transfer-system
-- Insert 5 test accounts with varying balances and statuses

INSERT INTO ACCOUNTS (id, holder_name, email, balance, status, version, last_updated) VALUES
('FIDPY100001', 'John Doe', 'trishanu8295@gmail.com', 10000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('FIDPY100002', 'Jane Smith', 'trishanu8295@gmail.com', 25000.50, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('FIDPY100003', 'Bob Johnson', 'bob.johnson@example.com', 5000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('FIDPY100004', 'Alice Williams', 'alice.williams@example.com', 15000.75, 'ACTIVE', 0, CURRENT_TIMESTAMP),
('FIDPY100005', 'Charlie Brown', 'charlie.brown@example.com', 8500.25, 'ACTIVE', 0, CURRENT_TIMESTAMP);
