package com.recalot.model.rec.recommender.funksvd.helper;

/**
 * Iterator for Longs
 * Adapted from previous Apache Mahout implementation (0.4)
 */
public abstract class AbstractLongPrimitiveIterator implements LongPrimitiveIterator {

    @Override
    public Long next() {
        return nextLong();
    }

}
