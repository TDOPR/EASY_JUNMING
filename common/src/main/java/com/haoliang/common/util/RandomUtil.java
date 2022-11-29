package com.haoliang.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/8 16:05
 **/
public class RandomUtil {

    private RandomUtil() {
    }

    /**
     * 生成指定范围区间内的收益率 0.35% ~ 0.55%
     *
     * @return
     */

    /**
     * 生成随机数
     * @param min 最小值
     * @param max 最大值
     * @param accuracy 小数精度
     * @param divid 除的倍率
     * @return
     */
    public static BigDecimal generate(int min, int max, int accuracy, int divid) {
        double num = (Math.random() * (min - max) + max) / divid;
        BigDecimal d = new BigDecimal(num);
        d = d.setScale(accuracy, RoundingMode.HALF_UP);
        return d;
    }

    /**
     * 生成机器人收益率
     * @param min
     * @param max
     * @return
     */
    public static BigDecimal generateRobotYieldRate(int min, int max) {
        return generate(min,max,4,10000);
    }

    /**
     * 生成 0 ~ max之间随机数 不包含max
     * @param max
     * @return
     */
    public static int generateInt(int max){
        return  (int)(Math.random() * max);
    }

    public static void main(String[] args) {
        //校验工具的准确性
        int count=0;
        int min=0;
        int max=0;

        for (int i = 0; i < 100000; i++) {
            BigDecimal num = generateRobotYieldRate(35, 55);
            if (num.doubleValue() < 0.0035 || num.doubleValue() > 0.0055) {
                count++;
            }
            if (num.compareTo(new BigDecimal(0.0035)) == 0) {
                min++;
            }
            if (num.compareTo(new BigDecimal(0.0055)) == 0) {
                max++;
            }
        }

        System.out.println(count);
        System.out.println(min);
        System.out.println(max);

    }

}
