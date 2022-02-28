SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS major CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE major
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE student
(
    id		        INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name	        VARCHAR(64) NOT NULL ,
    student_num     SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
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

INSERT INTO student(name, student_num, password, resident_num, major)
VALUES ('학생1', 001, 'password', '1231231112221', 1);
INSERT INTO student(name, student_num, password, resident_num, major)
VALUES ('학생2', 002, 'password', '1231231112222', 2);
INSERT INTO student(name, student_num, password, resident_num, major)
VALUES ('학생3', 003, 'password', '1231231112223', 3);
INSERT INTO student(name, student_num, password, resident_num, major)
VALUES ('학생4', 005, 'password', '1231231112224', 1);