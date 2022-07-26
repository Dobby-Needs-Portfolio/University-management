package deu.manager.executable.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity @Getter @AllArgsConstructor @NoArgsConstructor @Builder
@Table(name = "lecture_listeners")
public class LectureListener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_lecture_listener_to_lecture"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_lecture_listener_to_student"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @ColumnDefault("0")
    public Boolean isPaid;

}
