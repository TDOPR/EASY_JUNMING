package com.haoliang.common.utils;

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
    public static BigDecimal generate(int min, int max) {
        double num = (Math.random() * (min - max) + max) / 10000;
        BigDecimal d = new BigDecimal(num);
        d = d.setScale(4, RoundingMode.HALF_UP);
        return d;
    }

    public static void main(String[] args) {
        //校验工具的准确性
//        int count=0;
//        for(int i=0;i<100000;i++){
//            BigDecimal num=generate(35,55);
//            if(num.doubleValue()<0.0035 || num.doubleValue()>0.0055){
//                count++;
//            }
//        }
//        System.out.println(count);
    }

}
