package me.purefire.restapiwithspring.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    //Test 항목 1 생성자를 갖고있는가
    @Test
    public void builder() {
        //Event 클래스에 달아놓은 롬복 @Builder 이 builder()받아줌
        Event event = Event.builder()
                .name("Spring REST API STUDY")
                .description("REST API development whit SpringBoot")
                .build();
        assertThat(event).isNotNull();
    }

    //JavaBean 스팩을 준수하고 있는가  getterSetter
    @Test
    public void javaBean() {
        // Given
        String name = "Event";
        String desStr = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(desStr);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(desStr);
    }

    @Test
    public void testFree() {

        // Given
        Event event = Event.builder()
                    .basePrice(0)
                    .maxPrice(0)
                    .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isTrue();

        // Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isFalse();

        // Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffLine() {
        // Given
        Event event = Event.builder()
                .location("Sibuya Tokyo")
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isOffline()).isTrue();

        // Given
        event = Event.builder()
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isOffline()).isFalse();

    }

}