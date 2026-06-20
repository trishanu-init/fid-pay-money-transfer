-- Sample test accounts for money-transfer-system
-- Insert 5 test accounts with varying balances and statuses

INSERT INTO ACCOUNTS (id, holder_name, email, balance, status, version, last_updated, reward_points, role, password) VALUES
('FIDPY100000', 'System Admin', 'admin@example.com', 0.00, 'ACTIVE', 0, CURRENT_TIMESTAMP, 0, 'ADMIN', '$2a$10$8.Je5.VvXh7X24Qv87kKduK8m.vF8Wd5.3Q/F6kPz6fK1hB7pWfJy'),
('FIDPY100001', 'John Doe', 'john.doe@example.com', 10000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP, 0, 'USER', '$2a$10$8.Je5.VvXh7X24Qv87kKduK8m.vF8Wd5.3Q/F6kPz6fK1hB7pWfJy'),
('FIDPY100002', 'Jane Smith', 'trishanu8295@gmail.com', 25000.50, 'ACTIVE', 0, CURRENT_TIMESTAMP, 0, 'USER', '$2a$10$8.Je5.VvXh7X24Qv87kKduK8m.vF8Wd5.3Q/F6kPz6fK1hB7pWfJy'),
('FIDPY100003', 'Bob Johnson', 'bob.johnson@example.com', 5000.00, 'ACTIVE', 0, CURRENT_TIMESTAMP, 0, 'USER', '$2a$10$8.Je5.VvXh7X24Qv87kKduK8m.vF8Wd5.3Q/F6kPz6fK1hB7pWfJy'),
('FIDPY100004', 'Alice Williams', 'alice.williams@example.com', 15000.75, 'ACTIVE', 0, CURRENT_TIMESTAMP, 0, 'USER', '$2a$10$8.Je5.VvXh7X24Qv87kKduK8m.vF8Wd5.3Q/F6kPz6fK1hB7pWfJy'),
('FIDPY100005', 'Charlie Brown', 'charlie.brown@example.com', 8500.25, 'ACTIVE', 0, CURRENT_TIMESTAMP, 0, 'USER', '$2a$10$8.Je5.VvXh7X24Qv87kKduK8m.vF8Wd5.3Q/F6kPz6fK1hB7pWfJy');
