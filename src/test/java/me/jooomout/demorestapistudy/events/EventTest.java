package me.jooomout.demorestapistudy.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;


class EventTest {

    @Test
    void builder() {
        Event event = Event.builder()
                .name("Rest Study")
                .description("REST API study - Spring")
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    void isJavaBean() {
        Event event = new Event();
        String name = "Event";
        event.setName(name);
        String description = "Spring";
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @ParameterizedTest(name = "{index} =>  basePrice={0}, maxPrice={1}, isFree={2}")
    @MethodSource("paramsForTestFree")
    void testFree(int basePrice, int maxPrice, boolean isFree) {
        // GIVEN
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // WHEN
        event.update();
        // THEN
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Object[] paramsForTestFree() {
        return new Object[] {
                new Object[] {0,0,true},
                new Object[] {100, 0, false},
                new Object[] {100, 200, false}
        };
    }


    @ParameterizedTest(name = "{index} =>  location={0}, isOffline={1}")
    @MethodSource("paramsForTestOffline")
    void testOffline(String location, boolean isOffline){
        Event event = Event.builder()
                .location(location)
                .build();
        event.update();
        assertThat(event.isOffline()).isEqualTo(isOffline);

    }
    private static Object[] paramsForTestOffline() {
        return new Object[] {
                new Object[] {"", false},
                new Object[] {"우리집", true},
                new Object[] {null, false}
        };
    }


}