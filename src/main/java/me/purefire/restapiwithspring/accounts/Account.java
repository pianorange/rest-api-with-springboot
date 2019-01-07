package me.purefire.restapiwithspring.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Event 클래스에서 참조할 수 있도록 단방향으로 연관관계 설정 (Event에 Account 타입 변수 선언되있다고)
 * ElementCollection과 fetch 설명은 ReadMe 에 메모
 */
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    // fetch 는 오브젝트가 많지 않고 Account 가져올 때마다 필요해서 EAGER설정
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;

}
