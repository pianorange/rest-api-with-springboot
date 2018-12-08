package me.purefire.restapiwithspring.events;

import lombok.*;

import java.time.LocalDateTime;

//@Builder 는 모든 변수가진 생성자, 공개범위 default
//기본생성자 생성안되고 다른패키지에있는 클래스에서 불러쓰기 애매해짐
//때문에  @AllArgsConstructor @NoArgsConstructor 추가
//@EqualsAndHashCode(of = "id") 다른 엔티티와 연관관계 일때 필드를 지정해놓지 않으면
//모든 필드를 상호참조해서 스택오버플로우 발생가능성생김
//
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

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
    private EventStatus eventStatus;
}
