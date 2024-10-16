package com.ayds.zeday.util;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

@Component
public class RandomUtils {

    private RandomGenerator generator = RandomGenerator.getDefault();

    public int nextPositiveInt() {
        return generator.nextInt(Integer.MAX_VALUE) + 1;
    }

    public long nextPositiveLong() {
        return generator.nextLong(Long.MAX_VALUE) + 1;
    }

    public boolean nextBoolean() {
        return generator.nextBoolean();
    }

    public String nextString() {
        return UUID.randomUUID().toString();
    }

    public Instant nextInstant() {
        return Instant.ofEpochMilli(nextPositiveLong());
    }

    public Duration nextDuration() {
        return Duration.ofSeconds(nextPositiveLong());
    }

    public List<String> nextStrings() {
        return IntStream.range(0, generator.nextInt())
                .mapToObj(i -> nextString())
                .toList();
    }

    public <T> List<T> nextObjects(Supplier<T> supplier) {
        return IntStream.range(0, generator.nextInt(5, 100))
                .mapToObj(i -> supplier.get())
                .toList();
    }
}
