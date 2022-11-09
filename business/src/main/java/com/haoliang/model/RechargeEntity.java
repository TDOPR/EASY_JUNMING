package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@TableName("recharge")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户类型：1-普通用户
     */
    private Integer type;
    /**
     * 用户名(昵称)
     */
    private String userName;
    /**
     * 用户头像
     */
    private String images;
    /**
     * trx地址
     */
    private String address;
    /**
     * 我的算力
     */
    private BigDecimal power;
    /**
     * 我的算力上限
     */
    private BigDecimal powerLimit;
    private BigDecimal powerOverflow;
    /**
     * 团队总业绩
     */
    private BigDecimal teamPerformance;
    private Integer bigUid;
    /**
     * 我的机器人等级
     */
    private Integer robotLevel;
    /**
     * 托管上限
     */
    private BigDecimal rechargeMax;


    private BigDecimal lpTeamSmallPerformance;
    /**
     *
     */
    private String mobile;
    /**
     * 区号
     */
    private String countryCode;
    /**
     * 密码
     */
    private String password;
    /**
     * 支付密码
     */
    private String paypassword;
    /**
     * 交易密码设置状态, 0未设置,1已设置
     */
    private Integer paypassSetting;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 证件类型:1，身份证；2，军官证；3，护照；4，台湾居民通行证；5，港澳居民通行证；9，其他；
     */
    private Integer idCardType;
    /**
     * 认证状态：0-未认证；1-初级实名认证；2-高级实名认证
     */
    private Integer authStatus;
    /**
     * 审核状态,0待审核,1通过,2拒绝,
     */
    private Integer reviewsStatus;
    /**
     * 认证时间
     */
    private Date authtime;
    /**
     * -1-4对应每个会员等级
     -1~4对应每一个会员等级
     */
    private Integer level;
    /**
     * LP团队级别
     */
    private Integer lpLevel;
    private Integer netLevel;
    /**
     * 登录数
     */
    private Integer logins;
    /**
     * 状态：0禁用;1启用;
     */
    private Integer status;
    /**
     * 邀请码
     */
    private String inviteCode;
    /**
     * 直接邀请人ID
     */
    private Integer directInviteid;
    /**
     * 直接邀请人数量
     */
    private Integer directNum;
    /**
     * 累计直推返佣
     */
    private BigDecimal directCommission;
    /**
     * 累计团队返佣
     */
    private BigDecimal groupCommission;
    /**
     * 是否修改等级；0：未修改，1 修改过
     */
    private Integer mineLevel;
    /**
     * 历史最高等级
     */
    private Integer highestLevel;
    /**
     * 累计PPUSD总量
     */
    private BigDecimal ftcAmount;
    /**
     * 累计业绩
     */
    private BigDecimal promotionAmount;
    /**
     * 累计算力贡献值总和
     */
    private BigDecimal calculateAmount;
    /**
     * 谷歌秘钥
     */
    private String googleSecret;
    /**
     * 成交量
     */
    private BigDecimal volume;
    /**
     * 个推CID
     */
    private String gtClientId;
    /**
     * 用户hash
     */
    private String hash;
    /**
     * 用户注册ip
     */
    private String ip;
    /**
     * 修改时间
     */
    private Date lastUpdateTime;
    /**
     * 创建时间
     */
    private Date created;
}
