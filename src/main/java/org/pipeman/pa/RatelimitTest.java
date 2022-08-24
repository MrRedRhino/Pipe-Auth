package org.pipeman.pa;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RatelimitTest {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public static void main(String[] args) {

    }

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        return new LocalBucketBuilder()
                .addLimit(Bandwidth.classic(3, Refill.greedy(1, Duration.ofSeconds(2))))
                .build();
    }
}
