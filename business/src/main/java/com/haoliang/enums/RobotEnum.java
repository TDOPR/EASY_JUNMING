package com.haoliang.enums;

import com.haoliang.common.utils.NumberUtils;
import com.haoliang.common.utils.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public enum RobotEnum {

    ZERO(0, BigDecimal.ZERO, new BigDecimal(1000), new BigDecimal(0.0035), new BigDecimal(0.0055)),
    ONE(1, new BigDecimal(100), new BigDecimal(1000), new BigDecimal(0.006), new BigDecimal(0.006)),
    TW0(2, new BigDecimal(200), new BigDecimal(5000), new BigDecimal(0.006), new BigDecimal(0.007)),
    THREE(3, new BigDecimal(300), new BigDecimal(10000), new BigDecimal(0.007), new BigDecimal(0.009)),
    FOUR(4, new BigDecimal(400), new BigDecimal(30000), new BigDecimal(0.008), new BigDecimal(0.01)),
    FIVE(5, new BigDecimal(500), BigDecimal.ZERO, new BigDecimal(0.009), new BigDecimal(0.012));

    /**
     * 机器人等级
     */
    private int level;
    /**
     * 购买价格
     */
    private BigDecimal price;
    /**
     * 托管金额上限
     */
    private BigDecimal rechargeMax;
    /**
     * 日量化收益下限
     */
    private BigDecimal minProfitRate;
    /**
     * 日量化收益利率上限
     */
    private BigDecimal maxProfitRate;

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getRechargeMax() {
        return rechargeMax;
    }

    public String getName() {
        return String.format("ET0%d型AI", level);
    }

    /**
     * 根据机器人等级获取托管金额上限
     *
     * @param level
     * @return
     */
    public static BigDecimal getRechargeMaxByLevel(int level) {
        for (RobotEnum robotEnum : RobotEnum.values()) {
            if (level == robotEnum.getLevel()) {
                return robotEnum.getRechargeMax();
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取日收益区间
     */
    public String getDayRateSection() {
        if (maxProfitRate.compareTo(minProfitRate) == 0) {
            return NumberUtils.toPercentageUP(maxProfitRate);
        }
        return NumberUtils.toPercentageUP(minProfitRate) + "-" + NumberUtils.toPercentageUP(maxProfitRate);
    }

    /**
     * 获取周收益区间
     */
    public String getWeekRateSection() {
        if (maxProfitRate.compareTo(minProfitRate) == 0) {
            return maxProfitRate.multiply(new BigDecimal(7)).setScale(2, RoundingMode.HALF_UP) + "%";
        }
        BigDecimal weekMin = minProfitRate.multiply(new BigDecimal(7));
        BigDecimal weekMax = maxProfitRate.multiply(new BigDecimal(7));
        return NumberUtils.toPercentageUP(weekMin) + "-" + NumberUtils.toPercentageUP(weekMax);
    }

    /**
     * 根据机器人等级获取机器人枚举对象
     *
     * @param level 机器人等级
     * @return 枚举对象
     */
    public static RobotEnum levelOf(int level) {
        for (final RobotEnum robotEnum : RobotEnum.values()) {
            if (robotEnum.getLevel() == level) {
                return robotEnum;
            }
        }
        return ZERO;
    }

    /**
     * 根据等级
     */
    public static BigDecimal getProfitRateByLevel(Integer level) {
        for (RobotEnum robotEnum : RobotEnum.values()) {
            if (level == robotEnum.getLevel()) {
                if (robotEnum.getMaxProfitRate().compareTo(robotEnum.getMinProfitRate()) == 0) {
                    //如果为固定利率,则不用随机生成
                    return robotEnum.getMaxProfitRate().setScale(4, RoundingMode.FLOOR);
                }
                return RandomUtil.generate(robotEnum.minProfitRate.multiply(new BigDecimal(10000)).intValue(), robotEnum.maxProfitRate.multiply(new BigDecimal(10000)).intValue());
            }
        }
        return BigDecimal.ZERO;
    }

    public static void main(String[] args) {
        System.out.println(RobotEnum.ONE.getName());
        for (int i = 0; i <= 5; i++) {
            System.out.println(getProfitRateByLevel(i));
        }
    }
}
