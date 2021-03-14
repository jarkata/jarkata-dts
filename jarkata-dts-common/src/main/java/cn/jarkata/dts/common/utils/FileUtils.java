package cn.jarkata.dts.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String FILE_SEP = "/";

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
     * 获取全路径
     *
     * @param basePath     基础路径
     * @param relativePath 相对路径
     * @return 全路径
     */
    public static String getFullPath(String basePath, String relativePath) {
        return trimPathSep(basePath + FILE_SEP + relativePath);
    }

    /**
     * 确保文件的上级目录存在
     *
     * @param fullPath 文件全路径
     */
    public static void ensureParentPath(String fullPath) {
        int lastIndex = fullPath.lastIndexOf(FILE_SEP);
        if (lastIndex <= 0) {
            logger.error("路径中不存在路径分隔符");
            return;
        }
        String parentPath = fullPath.substring(0, lastIndex);
        File dir = new File(parentPath);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            logger.debug("创建结果：{},parentPath={}", mkdirs, parentPath);
        } else {
            logger.debug("已创建目录parentPath={}", parentPath);
        }

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
        return path.replaceAll("\\\\", FILE_SEP)
                .replaceAll("//", FILE_SEP);
    }
}
