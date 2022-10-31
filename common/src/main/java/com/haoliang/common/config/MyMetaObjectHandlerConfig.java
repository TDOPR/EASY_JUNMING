package com.haoliang.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Dominick Li
 * @Description 数据填充
 * @CreateTime 2022/10/26 10:24
 **/
@Component
public class MyMetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 该属性为空，可以进行填充
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("lastmodifiedTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("lastmodifiedTime",new Date(), metaObject);
    }

}
