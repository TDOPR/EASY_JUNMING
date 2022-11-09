package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum CoinEnum {

    RECHARGE_USDT(1,  "USDT", "https://fashiongame.oss-cn-beijing.aliyuncs.com/gpp/GPP3x.png"),
    STATIC_USDT(2, "STATIC_USDT", "https://fashiongame.oss-cn-beijing.aliyuncs.com/gpp/GPP3x.png"),
    DYNAMIC_USDT(3, "DYNAMIC_USDT", "https://fashiongame.oss-cn-beijing.aliyuncs.com/gpp/GPP3x.png"),
    ROBOT_USDT(4, "ROBOT_USDT", "https://fashiongame.oss-cn-beijing.aliyuncs.com/gpp/GPP3x.png"),
    ROBOT_FREEZE_USDT(5, "ROBOT_FREEZE_USDT", "https://fashiongame.oss-cn-beijing.aliyuncs.com/gpp/GPP3x.png"),
    SUPER_STATIC_USDT(6, "SUPER_STATIC_USDT", "https://fashiongame.oss-cn-beijing.aliyuncs.com/gpp/GPP3x.png");

    private long coinId;
    private String coinName;
    private String img;

    CoinEnum(final long coinId, final String coinName, final String img) {
        this.coinId = coinId;
        this.coinName = coinName;
        this.img = img;
    }

    public static String getImgByCoinId(long coinId) {
        for (CoinEnum coin : CoinEnum.values()) {
            if (coinId == coin.getCoinId()) {
                return coin.getImg();
            }
        }
        return "";
    }

    public static long getCoinIdByName(String coinName) {
        for (CoinEnum coin : CoinEnum.values()) {
            if (coinName.equals(coin.getCoinName())) {
                return coin.getCoinId();
            }
        }
        return 0;
    }

    public static String getNameByCoinId(Long coinId) {
        for (CoinEnum coin : CoinEnum.values()) {
            if (coinId.equals(coin.getCoinId())) {
                return coin.getCoinName();
            }
        }
        return "";
    }

    public long getCoinId() {
        return coinId;
    }


    public String getCoinName() {
        return coinName;
    }

    public String getImg() {
        return img;
    }
}
