package com.haoliang.common.enums;

import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.MessageUtil;
import lombok.Getter;

@Getter
public enum ReturnMessageEnum {

    OK("ok", "操作成功！"),
    FAILED("failed", "操作失败！"),
    ERROR("error", "系统内部错误，请联系管理员！"),
    VERIFICATION_CODE_EXPIRE("code_expire", "验证码过期！"),
    VERIFICATION_CODE_ERROR("code_error", "验证码错误！"),
    EMAIL_NOT_EXISTS("email_not_exists", "邮箱号未注册账号！"),
    EMAIL_EXISTS("email_esists", "邮箱号已被注册！"),
    SEND_EMAIL_ERROR("send_eamil_error", "发送验证码失败！"),
    ACCOUNT_DISABLED("account_disabled", "账号已被禁用！"),
    PASSWORD_ERROR("pwd_error", "密码错误d！"),
    ORIGINAL_PASSWORD_ERROR("original_pwd_error", "原密码错误！"),
    INVITE_CODE_ERROR("inviteCode_error", "邀请码错误，请确认无误后再输入！"),
    BLOCK_ADDRESS_EMPTY("block_address_empty", "没有可用的充值地址"),
    BEYOND_ROBOT_UPPER_LIMIT("beyond_robot_upper_limit", "托管的总金额不超出已购买机器人支持的上限{0}$！"),
    AMOUNT_EXCEEDS_BALANCE("amount_exceeds_balance", "余额不足！"),
    MIN_AMOUNT("min_amount", "托管金额存入不能低于{0}$！"),
    WITHDRAW_MIN_AMOUNT("withdraw_min_amount", "USDT提现的金额未达到提现最低额度{0}$！"),
    REBOT_LEVEL_ERROR("rebot_level_error", "升级的机器人等级必须比原有等级高！"),
    REPEAT_PURCHASE_REBOT("repeat_purchase_rebot", "不能重复购买机器人！"),
    UB_SUPPORT_NETWORD("un_support_netword", "不支持的网络类型"),
    REPEATED_SUBMISSION("repeated_submission", "请求太频繁，请稍后再试！"),

    UNAUTHORIZED("unauthorized", "登录已超时，请重新登录！"),
    ACCOUNT_EXISTS("account_esists", "账号已注册！"),
    ACCOUNT_NOT_EXISTS("account_not_esists", "账号不存在！"),
    ACCOUNT_LOCK("account_lock", "账号已被锁定{0}分钟，请稍后重试！"),
    MENU_EXISTS_USE("menu_exists_use", "菜单已被角色使用，不能删除！"),
    ROLE_EXISTS_USE("role_exists_use", "角色已被用户使用，不能删除！"),
    ROLE_NAME_EXISTS("role_name_exists", "角色名称不能重复！"),
    ROLE_CODE_EXISTS("role_code_exists", "角色编码不能重复！"),
    ;

    /**
     * 国际化信息文件里的Key前缀
     */
    private final static String prefix = "return.";

    /**
     * 返回的国际化key信息
     */
    private String key;

    private String cnMessage;

    ReturnMessageEnum(String key, String cnMessage) {
        this.key = prefix + key;
        this.cnMessage = cnMessage;
    }

    @Override
    public String toString() {
        return MessageUtil.get(key, ThreadLocalManager.getLanguage());
    }

    /**
     * 替换占位符中的参数
     *
     * @param params 需要替换的参数值 长度可变
     */
    public String setAndToString(Object... params) {
        return MessageUtil.get(key, params, ThreadLocalManager.getLanguage());
    }

}
