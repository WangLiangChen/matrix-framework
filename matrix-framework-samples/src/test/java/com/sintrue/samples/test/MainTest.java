package com.sintrue.samples.test;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

public class MainTest {
    @Test
    public void testOptional() {
        Optional<String> optional = Optional.ofNullable(null);
        System.out.println(optional.orElseGet(() -> "newName"));
    }
    @Test
    public void testDuration() {
        Duration duration= Duration.ZERO;
        System.out.println(Duration.ZERO==duration);
    }
}
