package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.util.ExceptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

class UtilTest {

    @Test
    public void testFetch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
        }).start();
        latch.await();
        System.out.println("OK!");
    }
    @Test
    public void executeUntilNoException() {
        long start = System.currentTimeMillis();
        int k = ExceptionUtil.executeUntilNoException(() -> {
            if (System.currentTimeMillis() - start < 3000) {
                throw new RuntimeException();
            }
            return 1;
        }, 5000);
        Assertions.assertEquals(1, k);
    }
}