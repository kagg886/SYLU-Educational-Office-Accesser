package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.util.ExceptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilTest {

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