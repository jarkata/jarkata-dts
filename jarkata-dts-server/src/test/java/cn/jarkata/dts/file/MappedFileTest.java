package cn.jarkata.dts.file;

import org.junit.Test;

public class MappedFileTest {

    @Test
    public void testMappedFile() {
        MappedFile mappedFile = new MappedFile("/Users/vkata/logs/tmp.txt", 1024 * 1024);
        mappedFile.put("testsdfafasdfs\n");
        mappedFile.put("testsdfafasdfs\n");
        mappedFile.flush();
    }
}