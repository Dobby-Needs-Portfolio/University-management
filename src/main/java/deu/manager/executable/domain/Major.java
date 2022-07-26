package deu.manager.executable.domain;

import lombok.*;

import javax.persistence.*;

@Entity @Getter @AllArgsConstructor @NoArgsConstructor @Builder
@Table(name = "majors")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , nullable = false)
    private String name;

}
