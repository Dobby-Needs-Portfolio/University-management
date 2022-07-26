package deu.manager.executable.domain;


import lombok.*;

import javax.persistence.*;

/**
 * 관리자 데이터를 저장하는 도메인 클래스입니다.
 */

@Entity @Getter @AllArgsConstructor @NoArgsConstructor @Builder @Setter
@Table(name = "admin_staff")
public class AdminStaff{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true , nullable = false)
    private Integer staffNum;

    @Column(unique = true , nullable = false)
    private String password;

    @Column(unique = true , nullable = false)
    private String residentNum;


}
