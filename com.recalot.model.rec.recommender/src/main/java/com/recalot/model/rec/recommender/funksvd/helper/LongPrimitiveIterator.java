package com.recalot.model.rec.recommender.funksvd.helper;

import java.util.Iterator;

/**
 * From previous Apache Mahout implementation (0.4)
 * Adds notion of iterating over <code>long</code> primitives in the style of an {@link Iterator} -- as
 * opposed to iterating over {@link Long}. Implementations of this interface however also implement
 * {@link Iterator} and {@link Iterable} over {@link Long} for convenience.
 */
public interface LongPrimitiveIterator extends SkippingIterator<Long> {

    /**
     * @return next <code>long</code> in iteration
     * @throws java.util.NoSuchElementException if no more elements exist in the iteration
     */
    long nextLong();

    /**
     * @return next <code>long</code> in iteration without advancing iteration
     */
    long peek();

}