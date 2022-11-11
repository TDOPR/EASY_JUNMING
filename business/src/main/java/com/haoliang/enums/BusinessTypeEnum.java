package com.haoliang.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 资金流水业务类型
 * @Author: Chen Long
 * @Date: Created in 2018/5/13 下午2:37
 * @Modified by: Chen Long
 */
public enum BusinessTypeEnum {



    BUY_ROBOT("BUY_ROBOT","首次购买机器人","buyRobot"),
    UPGRADE_ROBOT("UPGRADE_ROBOT","升级机器人","upgradeRobot"),
    REWARD_FREEZE_ROBOT("REWARD_FREEZE_ROBOT","机器人推广奖（冻结状态）","rewardFreezeRobot"),
    REWARD_UNFREEZE_ROBOT("REWARD_UNFREEZE_ROBOT","返还冻结机器人推广奖","rewardUnFreezeRobot"),
    REWARD_ROBOT("REWARD_ROBOT","机器人推广奖","rewardRobot"),


    REWARD_COMMU_ALGEBRA("REWARD_COMMU_ALGEBRA", "社区代数奖", "rewardCommuAlgebra"),
    REWARD_COMMU_TEAM("REWARD_COMMU_TEAM", "社区团队奖", "rewardCommuTEAM"),
    RECHARGE("RECHARGE","托管","recharge");


    /**
     *
     */
    private String code;
    /**
     * 标识
     */
    private String desc;
    private String descEn;

    BusinessTypeEnum(String code, String desc, String descEn) {
        this.code = code;
        this.desc = desc;
        this.descEn = descEn;
    }

    public static Map<String, String> getDescByCode(String code) {
        HashMap<String, String> resultMap = new HashMap<>();
        for (BusinessTypeEnum type : BusinessTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                resultMap.put("desc", type.getDesc());
                resultMap.put("descEn", type.getDescEn());
            }
        }
        return resultMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEN(String descEn) {
        this.descEn = descEn;
    }

}
