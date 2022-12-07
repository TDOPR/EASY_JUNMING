package com.haoliang.model.vo;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/12/7 16:51
 **/
@Data
public class AppDownloadAddressVO {

    /**
     * 下载地址
     */
    private String downloadAddress;

    public AppDownloadAddressVO(String downloadAddress) {
        this.downloadAddress = downloadAddress;
    }
}
