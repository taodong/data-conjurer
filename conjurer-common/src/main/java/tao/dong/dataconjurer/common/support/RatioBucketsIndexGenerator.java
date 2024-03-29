package tao.dong.dataconjurer.common.support;

import jakarta.annotation.Nonnull;
import tao.dong.dataconjurer.common.model.RatioRange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Ratio bucket index generator will test RationRange with randomly generated double and return the index of the first matched bucket
 * This class doesn't check the overlapping of bucket. If no match found, -1 is returned.
 */
public class RatioBucketsIndexGenerator implements IndexValueGenerator{

    private static final int DEFAULT_INDEX = -1;
    private static final RandomGenerator randomGenerator = RandomGenerator.getDefault();
    private final List<IndexedBucket> buckets;

    public RatioBucketsIndexGenerator(@Nonnull RatioRange... ranges) {
        this.buckets = new ArrayList<>();
        for (var i = 0; i < ranges.length; i++) {
            this.buckets.add(new IndexedBucket(i, ranges[i]));
            this.buckets.sort(Comparator.comparingDouble(bucket -> bucket.range().getMin()));
        }
    }

    @Override
    public Integer generate() {
        return matchBucket(randomGenerator.nextDouble());
    }

    int matchBucket(final double key) {
        var matched = DEFAULT_INDEX;
        if (!this.buckets.isEmpty()) {
            var low = 0;
            var high = this.buckets.size() - 1;

            while (low <= high) {
                var mid = low + (high - low) / 2;
                var bucket = this.buckets.get(mid);
                if (bucket.range().getMin() >= key) {
                    high = mid - 1;
                } else if (bucket.range().getMax() < key) {
                    low = mid + 1;
                } else {
                    matched = bucket.index();
                    break;
                }
            }
        }
        return matched;
    }

    record IndexedBucket(int index, RatioRange range) {
    }
}
