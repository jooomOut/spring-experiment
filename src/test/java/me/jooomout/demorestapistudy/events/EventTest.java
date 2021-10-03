package me.jooomout.demorestapistudy.events;

import org.junit.jupiter.api.Test;

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

    @Test
    void testFree() {
        // GIVEN
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // WHEN
        event.update();

        // THEN
        assertThat(event.isFree()).isTrue();

        //GIVEN
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // WHEN
        event.update();

        // THEN
        assertThat(event.isFree()).isFalse();
    }

    @Test
    void testOffline(){
        Event event = Event.builder()
                .location("우리집")
                .build();
        event.update();
        assertThat(event.isOffline()).isTrue();

        event = Event.builder()
                .build();
        event.update();
        assertThat(event.isOffline()).isFalse();
    }




}