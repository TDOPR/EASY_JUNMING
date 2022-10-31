package com.haoliang.common.utils;

import cn.hutool.core.io.FileUtil;
import com.haoliang.common.enums.ContentTypeEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * @Description
 * @Author Dominick Li
 * @CreateTime 2022/10/24 10:40
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils extends FileUtil {


    private final static Logger logger = LoggerFactory.getLogger(FileUtils.class);


    private static DecimalFormat df = new DecimalFormat("#.00");

    public static String formatFileSize(MultipartFile mFile) {
        long filesize = mFile.getSize();
        return computeFileSize(filesize);
    }

    private static String computeFileSize(long filesize) {
        StringBuffer mstrbuf = new StringBuffer();
        if (filesize <= 1024) {
            mstrbuf.append(filesize);
            mstrbuf.append(" B");
        } else if (filesize <= 1024 * 1024) {
            mstrbuf.append(df.format((double) filesize / 1024));
            mstrbuf.append(" KB");
        } else if (filesize <= 1024 * 1024 * 1024) {
            mstrbuf.append(df.format((double) filesize / 1024 / 1024));
            mstrbuf.append(" MB");
        } else {
            mstrbuf.append(df.format((double) filesize / 1024 / 1024 / 1024));
            mstrbuf.append(" G");
        }
        return mstrbuf.toString();
    }

    public static String formatFileSize(File file) {
        long filesize = getFileSize(file);
        return computeFileSize(filesize);
    }


    private static long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File flist[] = file.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        } else {
            size = size + file.length();
        }
        return size;
    }

    public static String getFileType(String fileName) {
        String[] arrays = fileName.split("\\.");
        if (arrays.length > 1) {
            return arrays[arrays.length - 1].toLowerCase();
        }
        return "";
    }

    public static String getFileName(String fileName) {
        String[] arrays = fileName.split("\\.");
        return arrays[0];
    }


    public static boolean deleteFile(String filePath) {
        return deleteFile(new File(filePath));
    }

    /**
     * 递归删除文件及文件夹
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        String parentPath = file.getAbsolutePath();
        String[] tempList = file.list();
        File temp = null;
        if (tempList != null) {
            for (int i = 0; i < tempList.length; i++) {
                temp = new File(parentPath + "/" + tempList[i]);
                if (temp.isFile()) {
                    temp.delete();
                } else if (temp.isDirectory()) {
                    deleteFile(temp);
                }
            }
        }
        boolean flag = file.delete();
        //System.out.println("删除文件夹:" + flag);
        return flag;
    }

    /**
     * 只删除目录下面的文件
     */
    public static void deleteOnlyFile(File file) {
        if (!file.exists()) {
            return;
        }
        for (File c : file.listFiles()) {
            if (c.isFile()) {
                c.delete();
            }
        }
    }


    /**
     * 根据图片的绝对路径构建图片输出流
     * 单台机器部署可以使用,分布式的情况不能这样
     */
    public static void downloadFileByLocal(HttpServletResponse response, File file, ContentTypeEnum type) throws Exception {
        downloadFileByInputStream(response, new FileInputStream(file), type);
    }

    /**
     * 根据输入流返回
     * zip
     */
    public static void downloadFileByInputStream(HttpServletResponse response, InputStream fileinput, ContentTypeEnum type) {
        BufferedInputStream bis = null;
        OutputStream out = null;
        try {
            bis = new BufferedInputStream(fileinput);
            if (type != ContentTypeEnum.OCTET_STREAM) {
                response.reset();
                response.setContentType(type.getValue() + ";charset=UTF-8");
            }
            response.addHeader("Content-Transfer-Encoding", "base64");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            logger.error("下载文件出错：未找到文件。请检查parameter.properties文件中的存储路径地址配置，或查看服务器中文件是否存在。");
        } catch (IOException e) {
            logger.error("下载文件出错：读写出错");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }

            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
            }

            try {
                if (fileinput != null) {
                    fileinput.close();
                }
            } catch (IOException e) {
            }
        }
    }


    /*
     *根据图片的url资源返回图片的输出流
     */
    public static void showPicByUrl(HttpServletResponse response, String url) {
        try (InputStream is = new URL(url).openStream()) {
            downloadFileByInputStream(response, is, ContentTypeEnum.JPG);
        } catch (Exception e) {
            logger.error("图片链接资源不存在: ,url:{},error:{}", url, e);
        }
    }

    /**
     * 根据base数据保存图片
     *
     * @param picSavePath
     * @param filedata
     * @return
     */
    public static String saveFileByBase64(String picSavePath, String filedata) {
        FileOutputStream fileOutputStream = null;
        try {
            Base64 base64 = new Base64();
            byte[] bytes = base64.decode(filedata);
            File saveFile = new File(picSavePath);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            if (!saveFile.exists()) {
                fileOutputStream = new FileOutputStream(saveFile);
                fileOutputStream.write(bytes);
            }
            return picSavePath;
        } catch (Exception e) {
            logger.error("save filedate error:", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    logger.error("save filedate close stream  error:", e);
                }
            }
        }
        return null;
    }

}