package com.recalot.model.rec.recommender.funksvd.helper;

import java.nio.charset.Charset;
import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.RepeatableRNG;

/**
 * From previous Apache Mahout implementation (0.4)
 */
public final class RandomWrapper extends Random {

    private static final byte[] STANDARD_SEED = "Mahout=Hadoop+ML".getBytes(Charset.forName("US-ASCII"));

    private static boolean testSeed;

    private Random random;
    private final Long fixedSeed;


    RandomWrapper() {
        this.fixedSeed = null;
        random = buildRandom();
    }

    RandomWrapper(long fixedSeed) {
        this.fixedSeed = fixedSeed;
        random = buildRandom();
    }

    static void useTestSeed() {
        testSeed = true;
    }

    private Random buildRandom() {
        if (testSeed) {
            return new MersenneTwisterRNG(STANDARD_SEED);
        } else if (fixedSeed == null) {
            return new MersenneTwisterRNG();
        } else {
            return new MersenneTwisterRNG(RandomUtils.longSeedtoBytes(fixedSeed));
        }
    }

    public Random getRandom() {
        return random;
    }

    void reset() {
        random = buildRandom();
    }

    public long getSeed() {
        return RandomUtils.seedBytesToLong(((RepeatableRNG) random).getSeed());
    }

    @Override
    public void setSeed(long seed) {
        // Since this will be called by the java.util.Random() constructor before we construct
        // the delegate... and because we don't actually care about the result of this for our
        // purpose:
        if (random != null) {
            random.setSeed(seed);
        }
    }

    @Override
    protected int next(int bits) {
        // Ugh, can't delegate this method -- it's protected
        // Callers can't use it and other methods are delegated, so shouldn't matter
        throw new UnsupportedOperationException();
    }

    @Override
    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int n) {
        return random.nextInt(n);
    }

    @Override
    public long nextLong() {
        return random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return random.nextGaussian();
    }
}
