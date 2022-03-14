drop table if exists staff_admin CASCADE;

CREATE TABLE staff_admin
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL ,
    staff_num       SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32)
);

INSERT INTO staff_admin (name, staff_num, password, resident_num) VALUES
('어드민', 111, 'admin_password', 1111111111111),
('TestName1', 321, '1221221', '1112221231231');