package cn.jarkata.dts.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtils {


    /**
     * 获取相对路径
     *
     * @param basePath 根路径
     * @param fullPath 文件的全路径
     * @return 相对路径
     */
    public static String getRelativePath(String basePath, String fullPath) {
        return fullPath.replaceFirst(basePath, "");
    }

    /**
     * 获取根目录下所有的文件信息
     *
     * @param basePath 根目录
     * @return 文件列表
     */
    public static List<String> getFileList(String basePath) {
        List<String> fileList = new ArrayList<>();
        makeFileList(fileList, basePath);
        return fileList;
    }

    /**
     * 组织文件列表
     *
     * @param fileList 文件列表
     * @param filePath 跟文件路径
     */
    public static void makeFileList(List<String> fileList, String filePath) {
        Objects.requireNonNull(filePath, "File Path not null");
        File tmpFile = new File(filePath);
        if (!tmpFile.exists()) {
            return;
        }
        if (!tmpFile.isDirectory()) {
            fileList.add(trimPathSep(tmpFile.getPath()));
        }
        File[] files = tmpFile.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            return;
        }
        for (File file : files) {
            makeFileList(fileList, file.getPath());
        }
    }

    /**
     * 去除文件路径分隔符
     *
     * @param path 文件目录
     * @return 文件路径
     */
    public static String trimPathSep(String path) {
        return path.replaceAll("\\\\", "/")
                .replaceAll("//", "/");
    }
}
