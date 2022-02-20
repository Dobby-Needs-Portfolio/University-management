-- Flush all database tables
SET FOREIGN_KEY_CHECKS = 0;

drop table if exists major CASCADE;
drop table if exists lecture_listener CASCADE;
drop table if exists lecture CASCADE;

drop table if exists professor CASCADE;
drop table if exists student CASCADE;
drop table if exists staff_class CASCADE;
drop table if exists staff_admin CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- Recreate table
CREATE TABLE major
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE staff_class
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL ,
    staff_num       SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32)
);

CREATE TABLE staff_admin
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL ,
    staff_num       SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32)
);

CREATE TABLE professor
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name            VARCHAR(64) NOT NULL ,
    prof_num        SMALLINT(3) ZEROFILL NOT NULL UNIQUE ,
    password        VARCHAR(128) NOT NULL ,
    resident_num    VARCHAR(32) ,
    major           INT(10),
    FOREIGN KEY (major) REFERENCES major(id)
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

CREATE TABLE lecture
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    lecture_num     INT(6) ZEROFILL NOT NULL UNIQUE ,
    name            VARCHAR(64) NOT NULL ,
    professor       INT(10) NOT NULL ,
    max_student     INT(5) NOT NULL ,
    min_student     INT(5) NOT NULL ,
    is_opened       BOOLEAN DEFAULT 0,
    FOREIGN KEY (professor) REFERENCES professor(id)
);

CREATE TABLE lecture_listener
(
    id              INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    lecture_id      INT(10) NOT NULL ,
    student_id      INT(10) NOT NULL ,
    bills_price     INT(11) DEFAULT(0) ,
    is_billed       BOOLEAN DEFAULT 0,
    FOREIGN KEY (lecture_id) REFERENCES lecture(id),
    FOREIGN KEY (student_id) REFERENCES student(id)
);
