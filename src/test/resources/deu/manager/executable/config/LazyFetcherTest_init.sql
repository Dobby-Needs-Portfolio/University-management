CREATE DATABASE test_db;
USE test_db;

CREATE TABLE test_table (
    id      INT(7) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text    VARCHAR(30) NOT NULL
);

INSERT INTO test_table (text)
VALUES ('Test1');
INSERT INTO test_table (text)
VALUES ('Test2');
INSERT INTO test_table (text)
VALUES ('Test3');
INSERT INTO test_table (text)
VALUES ('Test4');