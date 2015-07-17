package com.recalot.model.rec.recommender.funksvd.helper;

import java.util.NoSuchElementException;

/**
 * From previous Apache Mahout implementation (0.4)
 * While long[] is an Iterable, it is not an Iterable&lt;Long&gt;. This adapter class addresses that.
 */
public final class LongPrimitiveArrayIterator implements LongPrimitiveIterator {

    private final long[] array;
    private int position;
    private final int max;

    /**
     * <p>
     * Creates an  over an entire array.
     * </p>
     *
     * @param array array to iterate over
     */
    public LongPrimitiveArrayIterator(long[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array is null");
        }
        this.array = array; // yeah, not going to copy the array here, for performance
        this.position = 0;
        this.max = array.length;
    }

    @Override
    public boolean hasNext() {
        return position < max;
    }

    @Override
    public Long next() {
        return nextLong();
    }

    @Override
    public long nextLong() {
        if (position >= array.length) {
            throw new NoSuchElementException();
        }
        return array[position++];
    }

    @Override
    public long peek() {
        if (position >= array.length) {
            throw new NoSuchElementException();
        }
        return array[position];
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void skip(int n) {
        if (n > 0) {
            position += n;
        }
    }

    @Override
    public String toString() {
        return "LongPrimitiveArrayIterator";
    }

}