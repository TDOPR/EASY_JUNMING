package com.haoliang.model.meta;

import com.haoliang.model.vo.MetaColumnVO;
import lombok.Data;

import java.util.List;

/**
 * @author Dominick Li
 * @description 通用保存字段模型
 **/
@Data
public class SaveBO {

    /**
     * 保存元数据
     */
    private Long metaId;

    /**
     * 字段内容
     */
    private List<MetaColumnVO> data;

}
