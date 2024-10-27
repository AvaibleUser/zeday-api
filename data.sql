INSERT INTO business (name, auto_assignment, logo_url)
VALUES ('ZeroDay', FALSE, 'https://www.creativefabrica.com/wp-content/uploads/2019/03/Monogram-ZD-Logo-Design-by-Greenlines-Studios.jpg');

INSERT INTO role (name, description, multiuser)
VALUES ('ADMIN', 'Rol de administrador', TRUE);

INSERT INTO role (name, description, multiuser)
VALUES ('CLIENTE', 'Rol de cliente', TRUE);

INSERT INTO user (email, password, name, lastname, nit, cui, phone, active_mfa, mfa_secret, business_id)
VALUES ('zeroday@example.com', '', 'admin', 'istrador', '', '', '', '', FALSE, 1);

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);
