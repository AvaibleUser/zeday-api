INSERT INTO business (name, auto_assignment, logo_url)
VALUES ('ZeroDay', FALSE, 'https://www.creativefabrica.com/wp-content/uploads/2019/03/Monogram-ZD-Logo-Design-by-Greenlines-Studios.jpg');

INSERT INTO business (name, auto_assignment, logo_url)
VALUES ('Dentalitics', FALSE, 'https://www.diabetes.ie/wp-content/uploads/2021/05/logo-Placeholder.jpg');

INSERT INTO role (name, description, multiuser)
VALUES ('ADMIN', 'Rol de administrador', TRUE);

INSERT INTO role (name, description, multiuser)
VALUES ('CLIENTE', 'Rol de cliente', TRUE);

INSERT INTO user (email, password, name, lastname, nit, cui, phone, active_mfa, mfa_secret, business_id)
VALUES ('zeroday@example.com', '$2a$10$99KZMF9nuGRovJ6Bk6UJHerDjKVJzXHGLr3X8Ze.hGuCK9L0TEhA2', 'admin', 'istrador', '1', '1', '1', FALSE, '1', 1);

INSERT INTO user (email, password, name, lastname, nit, cui, phone, active_mfa, mfa_secret, business_id)
VALUES ('dylan.daev@gmail.com', '$2a$10$99KZMF9nuGRovJ6Bk6UJHerDjKVJzXHGLr3X8Ze.hGuCK9L0TEhA2', 'admin2', 'istrador', '2', '2', '2', FALSE, '2', 2);

INSERT INTO user (email, password, name, lastname, nit, cui, phone, active_mfa, mfa_secret, business_id)
VALUES ('dylanelais20931369@cunoc.edu.gt', '$2a$10$99KZMF9nuGRovJ6Bk6UJHerDjKVJzXHGLr3X8Ze.hGuCK9L0TEhA2', 'cliente', 'istrador', '3', '3', '3', FALSE, '3', 2);

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);

INSERT INTO user_role (user_id, role_id)
VALUES (2, 1);

INSERT INTO user_role (user_id, role_id)
VALUES (2, 2);

INSERT INTO permission (module, grant_access)
VALUES ('ADMIN', 'READ');

INSERT INTO role_permission (role_id, permission_id)
VALUES (1, 1);
