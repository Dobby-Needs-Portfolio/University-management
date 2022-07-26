package deu.manager.executable.domain;


import deu.manager.executable.config.LazyFetcher;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 강좌 정보를 저장하는 도메인 클래스입니다.
 */

@Entity @Getter @AllArgsConstructor @NoArgsConstructor @Builder
@DynamicInsert // https://eocoding.tistory.com/71
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , nullable = false)
    private Integer lectureNum;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_lecture_to_professor"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Professor professor;

    @Column(nullable = false)
    private Integer maxStudent;

    @Column(nullable = false)
    private Integer minStudent;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isOpened;

    @Column(nullable = false)
    private Integer creditUnit;

    @OneToMany(mappedBy = "lecture")
    private Collection<LectureListener> studentList = new ArrayList<>();


}
