INSERT INTO business (name, auto_assignment, logo_url)
VALUES ('company-test', FALSE, 'https://cdn.pixabay.com/photo/2016/09/14/20/50/tooth-1670434_1280.png');

INSERT INTO role (name, description, multiuser)
VALUES ('ADMIN', 'Rol de administrador', TRUE);

INSERT INTO role (name, description, multiuser)
VALUES ('CLIENTE', 'Rol de cliente', TRUE);

INSERT INTO user (email, password, name, lastname, nit, cui, phone, active_mfa, mfa_secret, business_id)
VALUES ('example@example.com', '', 'admin', 'istrador', '', '', '', '', FALSE, 1);

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);
