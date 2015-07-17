package com.recalot.model.rec.recommender.funksvd.helper;

import java.util.Iterator;

/**
 * From previous Apache Mahout implementation (0.4)
 * Adds ability to skip ahead in an iterator, perhaps more efficiently than by calling {@link #next()}
 * repeatedly.
 */
public interface SkippingIterator<V> extends Iterator<V> {

    /**
     * Skip the next n elements supplied by this {@link Iterator}. If there are less than n elements remaining,
     * this skips all remaining elements in the {@link Iterator}. This method has the same effect as calling
     * {@link #next()} n times, except that it will never throw {@link java.util.NoSuchElementException}.
     */
    void skip(int n);

}
