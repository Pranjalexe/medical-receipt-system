-- Seed data for testing
INSERT INTO app_users (email, password, full_name, user_role, created_at, updated_at) VALUES ('admin@medreceipt.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGKKPTBaVrjZCGKk.3eS', 'System Admin', 'ROLE_ADMIN', NOW(), NOW());
