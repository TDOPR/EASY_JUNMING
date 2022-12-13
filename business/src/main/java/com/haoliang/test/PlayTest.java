package com.haoliang.test;

import com.alibaba.fastjson.JSONObject;
import com.haoliang.common.util.HttpUtil;
import com.haoliang.model.play.Transaction;

import java.util.concurrent.CountDownLatch;

/**
 * @Description  https://sandbox-portal.facilitapay.com/#/docs
 * @Author Dominick Li
 * @CreateTime 2022/12/1 14:23
 **/
public class PlayTest {

    /**
     * 正式环境
     */
    private final static String  PLAY_URL_PREV= "https://sandbox-api.facilitapay.com/api/v1";

    /**
     * 沙箱环境
     */
    private final static   String  PLAY_URL_PREV_DEV= "https://api.facilitapay.com/api/v1";

    /**
     * 认证接口用于获取jwt生成的token令牌
     */
    private final static String SIGN_IN_URL=PLAY_URL_PREV_DEV+"/sign_in";

    /**
     * 交易接口
     */
    private final static String TRANSACTIONS_URL=PLAY_URL_PREV_DEV+"/transactions";

    private static String TOKEN_KEY="Authorization";

    private static String TOKEN_VALUE="123";

    /**
     * 根据账号密码获取用户的JWT
     */
    private static void signIn(){
        JSONObject data=new JSONObject();
        JSONObject user=new JSONObject();
        user.put("username","jonh@email.com");
        user.put("password","somepass");
        data.put("user",user);
        String result=HttpUtil.postJson(SIGN_IN_URL,data.toJSONString());
        if(result.contains("jwt")){
            JSONObject jwtObject=JSONObject.parseObject(result);
            System.out.println(jwtObject.getString("jwt"));
        }
        System.out.println(result);
    }

    /**
     * 巴西币账户转账到美元账户
     * @param
     */
    public static void BRL_2_USD(){
        Transaction transaction=Transaction.builder()
                .currency("BRL")
                .exchange_currency("USD")
                .value("1000.00")
                //来自 FacilitaPay 的巴西银行帐户的 ID
                .from_bank_account_id("97313a02-bbbb-4341-bb2c-3602d185d926")
                //合作伙伴的外国银行账户的 ID（在本例中是一个美元银行账户）。
                .to_bank_account_id("766eadfc-f020-4d2a-bed3-c9a95623ed24")
                .subject_id("eb1a9eb-39d0-4d3d-ab30-bee5406d8e21")
                .build();
        JSONObject data=new JSONObject();
        data.put("transaction",transaction);
        String result=HttpUtil.postJson(TRANSACTIONS_URL,TOKEN_KEY,TOKEN_VALUE,data.toJSONString());
        System.out.println(result);
    }

    /**
     * 美元账户转账到巴西币账户
     * @param
     */
    public static void USD_2_BRL(){
        // https://api.facilitapay.com/api/v1/transactions
        Transaction transaction=Transaction.builder()
                .currency("USD")
                .exchange_currency("BRL")
                .value("1000.00")
                //来自 FacilitaPay 的外国银行账户的 ID
                .from_bank_account_id("97313a02-bbbb-4341-bb2c-3602d185d926")
                //巴西合作伙伴银行帐户的 ID
                .to_bank_account_id("766eadfc-f020-4d2a-bed3-c9a95623ed24")
                //请注意，所有 ID 必须相关，即：提供的 subject_id 必须有与其注册相关联的 BRL 银行帐户。
                .subject_id("eb1a9eb-39d0-4d3d-ab30-bee5406d8e21")
                .build();
        JSONObject data=new JSONObject();
        data.put("transaction",transaction);

        String result=HttpUtil.postJson(TRANSACTIONS_URL,TOKEN_KEY,TOKEN_VALUE,data.toJSONString());
        System.out.println(result);
    }



    public static void main(String[] args) {
        // signIn();
        //BRL_2_USD();
        USD_2_BRL();
    }



}
