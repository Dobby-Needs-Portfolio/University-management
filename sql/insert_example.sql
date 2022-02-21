INSERT INTO staff_admin (name, staff_num, password, resident_num)
VALUE ('어드민', 111, 'admin_password', 1111111111111);
INSERT INTO staff_class (name, staff_num, password, resident_num)
VALUE ('직원1', 001, 'staff_password', 1111111111112);

INSERT INTO major (name)
VALUE ('학과1');
INSERT INTO major (name)
VALUE ('학과2');

INSERT INTO professor (name, prof_num, password, resident_num, major)
VALUE ('교수이름1', 001, '123123', '1112221231231', 1);
INSERT INTO professor (name, prof_num, password, resident_num, major)
VALUE ('교수이름2', 002, '123123', '1112221231231', 2);

INSERT INTO student (name, student_num, password, resident_num, major)
VALUE ('학생이름1', 001, '123123', '1112221231231', 1);
INSERT INTO student (name, student_num, password, resident_num, major)
VALUE ('학생이름2', 002, '123123', '1112221231232', 2);

INSERT INTO lecture (lecture_num, name, credit_unit, professor, max_student, min_student)
VALUE (1, '강좌1', 3, 1, 30, 15);
INSERT INTO lecture (lecture_num, name, credit_unit, professor, max_student, min_student)
VALUE (2, '강좌2', 2, 2, 30, 15);

INSERT INTO lecture_listener (lecture_id, student_id)
VALUE (1, 1);
INSERT INTO lecture_listener (lecture_id, student_id)
VALUE (1, 2);
INSERT INTO lecture_listener (lecture_id, student_id)
VALUE (2, 1);


