package me.purefire.restapiwithspring.events;

import lombok.*;
import me.purefire.restapiwithspring.accounts.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

//@Builder 는 모든 변수가진 생성자, 공개범위 default
//기본생성자 생성안되고 다른패키지에있는 클래스에서 불러쓰기 애매해짐
//때문에  @AllArgsConstructor @NoArgsConstructor 추가
//@EqualsAndHashCode(of = "id") 다른 엔티티와 연관관계 일때 필드를 지정해놓지 않으면
//모든 필드를 상호참조해서 스택오버플로우 발생가능성생김
//
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING) //default EnumType.ORDINAL enum에 0,1,2 순서대로 숫자부여
    private EventStatus eventStatus = EventStatus.DRAFT; //default Draft setting
    @ManyToOne
    private Account manager;

    public void update() {
        // Update free
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }

        //Update offLine
        //isEmpty()  value.length == 0;
        //isBlank()  since 11 : space、タブなど空白文字だけが含まれているのかもチェック
        if(this.location == null || this.location.isBlank()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
