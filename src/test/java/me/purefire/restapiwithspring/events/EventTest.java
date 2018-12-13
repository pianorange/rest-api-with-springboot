package me.purefire.restapiwithspring.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnitParamsRunner
 * Parameterised tests that don't suck
 * provides much easier and readable parametrised tests for JUnit >= 4.12.
 */
@RunWith(JUnitParamsRunner.class)
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
    @Parameters({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false"
    })
    public void testFree(int basePrice, int maxPrice, boolean isFree) {

        // Given
        Event event = Event.builder()
                    .basePrice(basePrice)
                    .maxPrice(maxPrice)
                    .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    /**
     * @Parameters使う時、Type安全性確報するために使うメソッド
     * 使わないでParameter宣言するとjava.lang.RuntimeException: java.lang.Exception: Method testOffLine2 should have no parameters
     * 使い方
     * １．@Parameters(method = "paramsForTestFree")
     * ２．Using convention   parametersFor(target testMethod) <-First Character is UpperCase
     *  @return params[] paramsForTestFree
     */
    private Object[] parametersForTestFreeUsingMethod() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    @Test
    @Parameters(method = "parametersForTestFreeUsingMethod")
    public void testFreeUsingMethod(int basePrice, int maxPrice, boolean isFree) {

        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }
    private Object[] parametersForTestFree2() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }
    @Test
    @Parameters
    public void testFree2(int basePrice, int maxPrice, boolean isFree) {

        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
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

    private Object[] parametersForTestOffLine2(){
        return new Object[] {
                new Object[] {"Sibuya Tokyo", true},
                new Object[] {"   ", false}
        };
    }

    @Test
    @Parameters
    public void testOffLine2(String location, boolean isOffLine) {
        // Given
        Event event = Event.builder()
                .location(location)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isOffline()).isEqualTo(isOffLine);
    }
}