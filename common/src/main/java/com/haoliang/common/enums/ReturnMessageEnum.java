package com.haoliang.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnMessageEnum {

    OK("ok","请求成功！","request success！"),
    ERROR("error","系统内部错误！","system inner error！"),
    VERIFICATION_CODE_EXPIRE("code_expire","验证码过期！","Verification code expired！"),
    VERIFICATION_CODE_ERROR("code_error","验证码错误！","Verification code error！"),
    EMAIL_NOT_EXISTS("email_not_exists","邮箱号未被注册！","Email number is not registered！"),
    EMAIL_EXISTS("email_esists","邮箱号已被注册！","Email number has been registered！"),
    ACCOUNT_EXISTS("account_esists","账号已存在！","The account already exists！"),
    ACCOUNT_NOT_EXISTS("account_not_esists","账号不存在！","The account does not exist！"),
    ACCOUNT_DISABLED("account_disabled","账号已被禁用！","The account has been disabled！"),
    ACCOUNT_LOCK("account_lock","账号已被锁定3分钟,请稍后重试！","The account has been locked for 3 minutes, please try again later！"),
    PASSWORD_ERROR("pwd_error","密码错误！","Password error！"),
    ORIGINAL_PASSWORD_ERROR("original_pwd_error","原密码错误！","original Password error！"),
    INVITE_CODE_ERROR("inviteCode_error","邀请码错误！","Invitation code error！"),
    MENU_EXISTS_USE("menu_exists_use","菜单已被角色使用，不能删除！","The menu has been used by the role and cannot be deleted！"),
    ROLE_EXISTS_USE("role_exists_use","角色已被用户使用，不能删除！","The role has been used by the user and cannot be deleted！"),
    ROLE_NAME_EXISTS("role_name_exists","角色名称不能重复！","The role name cannot be duplicate！"),
    ROLE_CODE_EXISTS("role_code_exists","角色编码不能重复！","The role code cannot be repeated！"),
    BLOCK_ADDRESS_EMPTY("block_address_empty","区块链地址池没有可用的地址！","Blockchain address pool has no available address！"),
    BEYOND_ROBOT_UPPER_LIMIT("beyond_robot_upper_limit","需要托管的总金额超出机器人支持的上限！","The total amount to be managed exceeds the upper limit of robot support！"),
    AMOUNT_EXCEEDS_BALANCE("amount_exceeds_balance","余额不足！","Sorry, your credit is running low"),
    MIN_AMOUNT("min_amount","托管金额不能低于10$！","The trust amount cannot be less than 10$！"),
    SEND_EMAIL_ERROR("send_eamil_error","发送验证码失败！","Failed to send verification code！"),
    REBOT_LEVEL_ERROR("rebot_level_error","升级的机器人等级必须比原有等级高！","The upgraded robot level must be higher than the original level！"),
    REPEAT_PURCHASE_REBOT("repeat_purchase_rebot","不能重复购买机器人！","Cannot buy robots repeatedly！")
    ;
    private String key;

    private String cnValue;

    private String enValue;
}
