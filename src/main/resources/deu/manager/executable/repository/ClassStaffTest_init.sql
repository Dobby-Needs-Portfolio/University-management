drop table if exists staff_class CASCADE;

CREATE TABLE staff_class
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL ,
    staff_num       SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32)
);

INSERT INTO staff_class (name, staff_num, password, resident_num) VALUES
('직원1', 001, 'staff_password', '1111111111112'),
('직원2', 222, '1221221', '1112221231231');