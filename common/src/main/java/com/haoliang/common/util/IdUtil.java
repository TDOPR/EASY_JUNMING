package com.haoliang.common.util;



/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/4 11:38
 **/
public class IdUtil extends cn.hutool.core.util.IdUtil {

    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    /**
     * 获取短位数UUID
     */
    public static String generateShortUUID(int size) {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = simpleUUID();
        for (int i = 0; i < size; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    public static void main(String[] args) {
        //获取雪花算法生成的全局唯一Id
        System.out.println(IdUtil.getSnowflakeNextId());
    }

}
