package com.coralblocks.coralme.util;

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class LinkedObjectPoolTest {

    // ... (keep existing test methods)

    @Test
    public void testAdaptiveGrowthUnderMemoryPressure() {
        LinkedObjectPool<byte[]> pool =
                new LinkedObjectPool<>(2, () -> new byte[1024]); // 1MB objects
        List<byte[]> objects = new ArrayList<>();

        try {
            byte[] object = pool.get();
            while (object != null) {
                objects.add(object);
                object = pool.get();
            }
        } catch (OutOfMemoryError e) {
            Assert.fail("Unexpected OutOfMemoryError");
            // Expected behavior when memory is exhausted
        }

        Assert.assertTrue(
                "Pool should have created multiple objects before running out of memory",
                objects.size() > 2);
        Assert.assertEquals("Pool size should be zero after exhausting memory", 0, pool.size());

        // Release objects back to the pool
        for (byte[] obj : objects) {
            pool.release(obj);
        }

        // The pool size should be less than or equal to the number of objects created
        // due to memory constraints
        Assert.assertTrue(
                "Pool size should be limited by available memory", pool.size() <= objects.size());
    }

    @Test
    public void testIncreasingPoolSize() {
        LinkedObjectPool<StringBuilder> pool = new LinkedObjectPool<>(2, StringBuilder::new);
        Assert.assertEquals(2, pool.size());

        StringBuilder sb1 = pool.get();
        StringBuilder sb2 = pool.get();
        Assert.assertNotNull(sb1);
        Assert.assertNotNull(sb2);
        Assert.assertEquals(0, pool.size());

        pool.release(sb1);
        pool.release(sb2);
        Assert.assertEquals(2, pool.size());
    }

    @Test
    public void testRunOutOfInstances() {
        LinkedObjectPool<StringBuilder> pool = new LinkedObjectPool<>(2, StringBuilder::new);
        Assert.assertEquals(2, pool.size());

        StringBuilder sb1 = pool.get();
        StringBuilder sb2 = pool.get();
        Assert.assertNotNull(sb1);
        Assert.assertNotNull(sb2);
        Assert.assertEquals(0, pool.size());

        StringBuilder sb3 = pool.get();
        Assert.assertNotNull("Should be able to create new instance when pool is empty", sb3);

        pool.release(sb1);
        pool.release(sb2);
        pool.release(sb3);
        Assert.assertEquals(3, pool.size());
    }

    @Test
    public void testReleaseAndReuse() {
        LinkedObjectPool<StringBuilder> pool = new LinkedObjectPool<>(1, StringBuilder::new);
        Assert.assertEquals(1, pool.size());

        StringBuilder sb1 = pool.get();
        Assert.assertNotNull(sb1);
        Assert.assertEquals(0, pool.size());

        StringBuilder sb2 = pool.get();
        Assert.assertNotNull(sb2);
        Assert.assertTrue("Pool size should be 0", pool.size() == 0);

        pool.release(sb1);
        Assert.assertEquals(1, pool.size());

        StringBuilder sb3 = pool.get();
        Assert.assertNotNull(sb3);
        Assert.assertSame("Should reuse released instance", sb1, sb3);
        Assert.assertEquals(0, pool.size());
    }
}
