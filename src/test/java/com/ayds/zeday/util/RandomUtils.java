package com.ayds.zeday.util;

import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.Collector;
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

    public double nextPositiveDouble() {
        return generator.nextDouble(Long.MAX_VALUE) + 1;
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

    public LocalDate nextDate() {
        return LocalDate.ofEpochDay(nextPositiveInt());
    }

    public LocalTime nextTime() {
        return LocalTime.of(generator.nextInt(0, 23), generator.nextInt(0, 59));
    }

    public Duration nextDuration() {
        return Duration.ofSeconds(nextPositiveLong());
    }

    public <T extends Enum<T>> T nextEnum(Class<T> type) {
        return type.getEnumConstants()[generator.nextInt(0, type.getEnumConstants().length)];
    }

    public List<String> nextStrings() {
        return IntStream.range(0, generator.nextInt(5, 20))
                .mapToObj(i -> nextString())
                .toList();
    }

    public <T, C extends Collection<T>> C nextObjects(Supplier<T> supplier, Collector<T, ?, C> collector) {
        return IntStream.range(0, generator.nextInt(5, 20))
                .mapToObj(i -> supplier.get())
                .collect(collector);
    }

    public <T, C extends Collection<T>> C nextMocks(Class<T> type, Collector<T, ?, C> collector) {
        return IntStream.range(0, generator.nextInt(5, 20))
                .mapToObj(i -> mock(type))
                .collect(collector);
    }
}
