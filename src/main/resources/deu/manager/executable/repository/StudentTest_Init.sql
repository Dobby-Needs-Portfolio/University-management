SET FOREIGN_KEY_CHECKS = 0;

drop table if exists lecture_listener CASCADE;
drop table if exists lecture CASCADE;
drop table if exists professor CASCADE;
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


-- Dependency - Lecture
CREATE TABLE professor
(
    id              INT(10) NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL ,
    prof_num        SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32) ,
    major           INT(10),
    FOREIGN KEY (major) REFERENCES major(id)
);

CREATE TABLE lecture
(
    id              INT(10) NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY ,
    lecture_num     INT(6) ZEROFILL NOT NULL UNIQUE ,
    name            VARCHAR(64) NOT NULL ,
    professor       INT(10) NOT NULL ,
    credit_unit     INT(2) NOT NULL ,
    max_student     INT(5) NOT NULL ,
    min_student     INT(5) NOT NULL ,
    is_opened       BOOLEAN DEFAULT 0,
    FOREIGN KEY (professor) REFERENCES professor(id)
);

-- Lecture listener table
CREATE TABLE lecture_listener
(
    id              INT(10) NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY ,
    lecture_id      INT(10) NOT NULL ,
    student_id      INT(10) NOT NULL ,
    bills_price     INT(11) DEFAULT(0) ,
    is_billed       BOOLEAN DEFAULT 0,
    FOREIGN KEY (lecture_id) REFERENCES lecture(id),
    FOREIGN KEY (student_id) REFERENCES student(id),
    -- https://stackoverflow.com/questions/54398907/avoid-duplicate-pairs-of-values-two-columns-in-a-sql-table
    -- https://ttend.tistory.com/630
    CONSTRAINT case_duplicate UNIQUE KEY (lecture_id, student_id)
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


INSERT INTO professor(name, prof_num, password, resident_num, major) VALUES
('교수1', 002, 'password', '1231231112222', 1),
('교수2', 001, 'password', '1231231112221', 2),
('교수3', 003, 'password', '1231231112223', 3);

INSERT INTO lecture(lecture_num, name, professor, credit_unit, max_student, min_student) VALUES
(001, '강의1', 1, 3, 30, 10),
(002, '강의2', 2, 2, 31, 11),
(003, '강의3', 3, 3, 32, 12),
(004, '강의4', 1, 3, 33, 13),
(005, '강의5', 2, 2, 33, 13);


-- Insert test case example
INSERT INTO lecture_listener (lecture_id, student_id, bills_price) VALUES
(1, 1, 100000),
(1, 2, 100000),
(2, 1, 100000),
(2, 2, 100000),
(3, 3, 100000),
(3, 4, 100000),
(4, 3, 100000),
(4, 4, 100000);