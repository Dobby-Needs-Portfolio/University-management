SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS professor CASCADE;
DROP TABLE IF EXISTS major CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE major
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE professor
(
    id		        INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name	        VARCHAR(64) NOT NULL ,
    prof_num     SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32) ,
    major           INT(10) ,
    FOREIGN KEY (major) REFERENCES major(id)
);

INSERT INTO major(name)
VALUES ('학과1');
INSERT INTO major(name)
VALUES ('학과2');
INSERT INTO major(name)
VALUES ('학과3');

INSERT INTO professor(name, prof_num, password, resident_num, major)
VALUES ('교수이름1', 001, '1111111', '1111111', 1);
INSERT INTO professor(name, prof_num, password, resident_num, major)
VALUES ('교수이름2', 002, '2222222', '2222222', 2);
INSERT INTO professor(name, prof_num, password, resident_num, major)
VALUES ('교수이름3', 003, '3333333', '3333333', 3);
INSERT INTO professor(name, prof_num, password, resident_num, major)
VALUES ('교수이름4', 004, '4444444', '4444444', 1);