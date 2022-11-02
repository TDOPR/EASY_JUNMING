package com.haoliang.config;

import com.haoliang.enums.DictionaryKeyEnum;

import java.util.HashMap;

public class DictionaryParam {

    private static boolean loading = false;

    private static final HashMap<String, String> DICTIONARY_PARAM = new HashMap<String, String>();

    public static HashMap<String, String> getDictionaryParam() {
        return DICTIONARY_PARAM;
    }

    public static void put(String key, String value) {
        DICTIONARY_PARAM.put(key, value);
    }

    public static boolean isLoading() {
        return loading;
    }

    public static void setLoading(boolean loading) {
        DictionaryParam.loading = loading;
    }

    public static Integer getLoginLogSaveDay() {
        return Integer.parseInt(DICTIONARY_PARAM.get(DictionaryKeyEnum.LOGIN_LOG_SAVE_DAY.getKey()));
    }

    public static Integer getOperationLogSaveDay() {
        return Integer.parseInt(DICTIONARY_PARAM.get(DictionaryKeyEnum.OPERATION_LOG_SAVE_DAY.getKey()));
    }

    public static Integer getErrorLogSaveDay() {
        return Integer.parseInt(DICTIONARY_PARAM.get(DictionaryKeyEnum.ERROR_LOG_SAVE_DAY.getKey()));
    }


    public static boolean isEnableSso() {
        return Boolean.parseBoolean(DICTIONARY_PARAM.get(DictionaryKeyEnum.ENABLE_SSO.getKey()));
    }


    public static Integer getThresholdSize() {
        return Integer.parseInt(DICTIONARY_PARAM.get(DictionaryKeyEnum.THRESHOLD_SIZE.getKey()));
    }

}
