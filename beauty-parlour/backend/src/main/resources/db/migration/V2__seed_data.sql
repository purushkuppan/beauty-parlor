-- Seed services
INSERT INTO services (name, description, category, price, duration_mins) VALUES
('Classic Haircut',      'Wash, cut and blow-dry',              'HAIR',   25.00, 60),
('Hair Colouring',       'Full colour with professional dye',   'HAIR',   65.00, 120),
('Facial Treatment',     'Deep cleansing facial',               'SKIN',   40.00, 60),
('Manicure',             'Shape, buff, and polish',             'NAILS',  20.00, 45),
('Pedicure',             'Full foot care and polish',           'NAILS',  30.00, 60),
('Bridal Makeup',        'Full bridal look with trial',         'MAKEUP', 120.00, 120),
('Eyebrow Threading',    'Precise shaping with thread',         'SKIN',   10.00, 20);

-- Admin user (password: Admin@1234 — change on first login)
-- Hash generated with BCrypt cost 12
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin', 'admin@beautyparlour.com',
 '$2a$12$K8f1bHGqiVTCsKHLFMEhBuJm3.bUEQKgI3pVxcGFHHCkTxaQNKrBe',
 'ADMIN');
