package deu.manager.executable.domain;


import deu.manager.executable.config.LazyFetcher;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 교수 정보를 저장하는 도메인 클래스입니다.
 */

@Entity @Getter @AllArgsConstructor @NoArgsConstructor @Builder
@Table(name = "professors")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true , nullable = false)
    private Integer professorNum;

    @Column(unique = true , nullable = false)
    private String password;

    @Column(unique = true , nullable = false)
    private String residentNum;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_professor_to_major"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Major major;

    @OneToMany(mappedBy = "professor")
    private Collection<Lecture> lectures = new ArrayList<>();


}
