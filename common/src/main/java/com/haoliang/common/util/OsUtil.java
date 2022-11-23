package com.haoliang.common.util;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/23 18:31
 **/
public class OsUtil {

    private OsUtil(){ }

    public  static boolean isLinuxOs(){
        String os = System.getProperty("os.name").toLowerCase();
        return os != null && os.indexOf("linux") != -1;
    }

    public  static  boolean isWindowOs(){
        String os = System.getProperty("os.name").toLowerCase();
        return  os != null && os.indexOf("window") != -1;
    }

    public  static  String getOsName(){
        return System.getProperty("os.name").toLowerCase();
    }

}
