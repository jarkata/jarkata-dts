package cn.jarkata.dts.monitor;

import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

public class MonitorService {

    private final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    public void report() {
        try {
            Class<?> platformDependent = PlatformDependent.class;
            Field directMemoryCounter = platformDependent.getDeclaredField("DIRECT_MEMORY_COUNTER");
            directMemoryCounter.setAccessible(true);
            AtomicLong directCounter = (AtomicLong) directMemoryCounter.get(null);
            long countL = directCounter.get() / 1024;
            if (countL > 0) {
                logger.info("DIRECT_MEMORY_COUNTER={}K", countL);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}