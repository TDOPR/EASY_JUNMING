package com.haoliang.common.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 把platform模块和recog模块jar依赖并集移动到common目录下,并删除重复的jar
 */
public class MergeJarUtils {

    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.dir"));
        String userDir=System.getProperty("user.dir")+File.separator+"release"+File.separator+"lib"+File.separator;
        File platform = new File(userDir,"platform");
        File recog = new File(userDir,"monitor");
        File common = new File(userDir,"common");
        if(!platform.exists() || !recog.exists() || !common.exists()){
            System.out.println("jar目录不存在");
            return;
        }
        List<File> platformFileList = Arrays.asList(platform.listFiles());
        List<File> recogFileList = Arrays.asList(recog.listFiles());
        List<String> platformNameList = platformFileList.stream().map(File::getName).collect(Collectors.toList());
        List<String> recogNameList = recogFileList.stream().map(File::getName).collect(Collectors.toList());
        for (File file : platformFileList) {
            //把并集放入到common目录下
            if (recogNameList.contains(file.getName())) {
                FileUtils.moveFile(file, new File(common, file.getName()));
            }
        }
        for (File file : recogFileList) {
            //删除并集
            if (platformNameList.contains(file.getName())) {
                file.delete();
            }
        }
    }
}
