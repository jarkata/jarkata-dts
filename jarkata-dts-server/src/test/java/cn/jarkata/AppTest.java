package cn.jarkata;

import static org.junit.Assert.assertTrue;

import io.netty.util.NettyRuntime;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.Objects;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws DecoderException, IOException {
        String hexstring = Hex.encodeHexString("你好，中国\n".getBytes());
        System.out.println(hexstring);
        assertTrue(true);
        Date date = new Date(1611928799996L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String format = sdf.format(date);

        long l = System.currentTimeMillis();
        System.out.println(l);

        System.out.println(format);


        byte[] bytes = "\n".getBytes();
        System.out.println(bytes[0]);
        int processors = NettyRuntime.availableProcessors();
        System.out.println(processors);
    }

    @Test
    public void testTimes() throws IOException, ParseException {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.of(2021, 1, 29), LocalTime.of(10, 0, 0));
        LocalDateTime endTime = LocalDateTime.of(LocalDate.of(2021, 2, 1), LocalTime.of(22, 0));
        FileOutputStream fos = new FileOutputStream("/Users/vkata/code/times.txt");
        while (startTime.isBefore(endTime)) {
            long epochSecond = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            epochSecond++;

            //epochSecond 就是需要的时间戳
            IOUtils.write((Objects.toString(epochSecond) + "\n").getBytes(StandardCharsets.UTF_8), fos);

            startTime = Instant.ofEpochMilli(epochSecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (startTime.getHour() > 22) {
                startTime = LocalDateTime.of(startTime.plusDays(1).toLocalDate(), LocalTime.of(10, 0, 0));
            }
        }
        fos.close();
    }

}
