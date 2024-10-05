package com.ayds.zeday.util;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

@Component
public class RandomUtils {

    private RandomGenerator generator = RandomGenerator.getDefault();

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

    public List<String> nextStrings() {
        return IntStream.range(0, generator.nextInt())
                .mapToObj(i -> nextString())
                .toList();
    }
}
