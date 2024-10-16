INSERT INTO business (name, auto_assignement)
VALUES ('company-test', FALSE);

INSERT INTO role (name, description, multiuser)
VALUES ('ADMIN', 'Rol de administrador', TRUE);

INSERT INTO role (name, description, multiuser)
VALUES ('CLIENTE', 'Rol de cliente', TRUE);

INSERT INTO user (email, password, name, lastname, business_id)
VALUES ("example@example.com", "", "admin", "istrador", 1);

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);
