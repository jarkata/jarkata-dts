package cn.jarkata.dts.file;

import org.junit.Test;

public class MappedFileTest {

    @Test
    public void testMappedFile() {
        long filesize = 100 * 1024 * 1024L;
        MappedFile mappedFile = new MappedFile("/Users/vkata/logs/tmp.txt", filesize);
        for (int i = 0; i < 2000000; i++) {
            mappedFile.put("1234567890123456789");
        }
        mappedFile.getMessage();
        mappedFile.len();

        mappedFile.flush();
    }

    @Test
    public void testGetMessage() {
        MappedFile mappedFile = new MappedFile("/Users/vkata/logs/tmp.txt", 1024 * 1024);
        String s = mappedFile.get(0, 10);
        System.out.println(s);
    }
}