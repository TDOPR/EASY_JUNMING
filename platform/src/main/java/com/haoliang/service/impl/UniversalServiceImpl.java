package com.haoliang.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.IdUtil;
import com.haoliang.common.util.StringUtil;
import com.haoliang.common.util.excel.ExcelUtil;
import com.haoliang.enums.ConditionTypeEnum;
import com.haoliang.model.MetaColumn;
import com.haoliang.model.MetaInfo;
import com.haoliang.model.meta.ConditionFiled;
import com.haoliang.model.meta.DeleteBO;
import com.haoliang.model.meta.SaveBO;
import com.haoliang.model.meta.UniversalQueryParamBO;
import com.haoliang.model.vo.MetaColumnVO;
import com.haoliang.server.NativeQueryServer;
import com.haoliang.service.MetaInfoService;
import com.haoliang.service.UniversalService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Dominick Li
 * @CreateTime 2022/6/12 16:13
 * @Description 封控通用的CRUD操作
 **/
@Service
public class UniversalServiceImpl implements UniversalService {

    @Autowired
    private MetaInfoService metaInfoService;

    @Autowired
    private NativeQueryServer nativeQueryServer;

    @Override
    public JsonResult<JSONObject> query(UniversalQueryParamBO universalQueryParam) {
        MetaInfo metaInfo = metaInfoService.getMetaInfo(universalQueryParam.getMetaId());

        //组装查询的sql
        StringBuilder querySql = new StringBuilder("select id,");
        for (MetaColumn metaColumn : metaInfo.getMetaColumnList()) {
            if (metaColumn.isViewShow()) {
                querySql.append(metaColumn.getColumnCode()).append(",");
            }
        }
        querySql.deleteCharAt(querySql.length() - 1);
        querySql.append(" from ").append(metaInfo.getTableCode()).append(" where 1=1");
        java.util.List<ConditionFiled> conditionFiledList = universalQueryParam.getConditionList();
        //对查询条件排序 优化查询性能
        conditionFiledList = conditionFiledList.stream().sorted().collect(Collectors.toList());
        List<Object> condition = new ArrayList<>();
        //拼接查询条件
        for (ConditionFiled conditionFiled : conditionFiledList) {
            if (StringUtil.isNotBlank(conditionFiled.getValue())) {
                if (conditionFiled.getType() == ConditionTypeEnum.EQUALS) {
                    querySql.append(" and ").append(conditionFiled.getCode());
                    querySql.append(" =?");
                    condition.add(conditionFiled.getValue());
                } else {
                    //模糊查询
                    querySql.append(" and ").append(conditionFiled.getCode());
                    querySql.append(" like CONCAT('%',?").append(",'%')");
                    condition.add(conditionFiled.getValue());
                }
            } else if (!Collections.isEmpty(conditionFiled.getBetweenValue())) {
                //日期范围格式的
                querySql.append(" and ").append(conditionFiled.getCode()).append(">=?");
                condition.add(conditionFiled.getBetweenValue().get(0));
                querySql.append(" and ").append(conditionFiled.getCode()).append("<=?");
                //condition.add(DateUtil.getDateStrIncrement(conditionFiled.getBetweenValue().get(1)));
            }
        }

        String sql = querySql.toString();
        String countSql = "select count(id) " + sql.substring(sql.indexOf("from"));
        //拼接分页的条件
        querySql.append(" limit ").append((universalQueryParam.getCurrentPage() - 1) * universalQueryParam.getPageSize()).append(",").append(universalQueryParam.getPageSize());
        //执行查询sql获取执行结果
        List<List<Object>> result = nativeQueryServer.query(querySql.toString(), condition);
        //执行获取总记录数的sql结果用于分页使用
        List<List<Object>> pageResult = nativeQueryServer.query(countSql, condition);
        //计算总记录数
        Integer totalElements = Integer.parseInt(pageResult.get(0).get(0).toString());
        //计算总页数
        Integer totalPages = totalElements % universalQueryParam.getPageSize() == 0 ? totalElements / universalQueryParam.getPageSize() : totalElements / universalQueryParam.getPageSize() + 1;
        //把结果和字段映射起来
        JSONArray tabel = new JSONArray();
        JSONObject tr;
        //列名称用于显示表格的title信息
        List<String> shouColumnCode = metaInfo.getMetaColumnList().stream().filter(MetaColumn::isViewShow).map(MetaColumn::getColumnCode).collect(Collectors.toList());
        for (List<Object> objects : result) {
            tr = new JSONObject(new LinkedHashMap<>());
            tr.put("id", objects.get(0).toString());
            for (int i = 1; i < objects.size(); i++) {
                tr.put(shouColumnCode.get(i - 1), objects.get(i));
            }
            tabel.add(tr);
        }

        JSONObject jsonObject = new JSONObject();
        if (universalQueryParam.isFirstInit()) {
            //获取当前表的所有字段,用于在添加和修改页面进行使用
            List<MetaColumnVO> metaColumnVOList = new ArrayList<>();
            metaInfo.getMetaColumnList().forEach(metaColumn -> metaColumnVOList.add(new MetaColumnVO(metaColumn)));
            jsonObject.put("metaColumnList", metaColumnVOList);
            //组装列表页的查询条件
            List<List<MetaColumnVO>> searchMetaColumnList = new ArrayList<>();
            List<MetaColumnVO> allSearchMetaColumnList = metaColumnVOList.stream().filter(metaColumnVO -> metaColumnVO.isSearch()).collect(Collectors.toList());
            int count = 0, size = allSearchMetaColumnList.size();
            while (count < size) {
                searchMetaColumnList.add(allSearchMetaColumnList.subList(count, Math.min((count + 3), size)));
                count += 3;
            }
            //封装成查询条件对象
            List<List<ConditionFiled>> conditionList = new ArrayList<>();
            List<ConditionFiled> conditionFileds;
            for (List<MetaColumnVO> columnVOList : searchMetaColumnList) {
                conditionFileds = new ArrayList<>();
                conditionList.add(conditionFileds);
                for (MetaColumnVO mc : columnVOList) {
                    conditionFileds.add(new ConditionFiled(mc.getName(), mc.getCode(), mc.getDataType()));
                }
            }
            jsonObject.put("searchMetaColumnList", conditionList);
        }
        jsonObject.put("metaName", metaInfo.getMetaName());
        jsonObject.put("tabelData", tabel);
        jsonObject.put("totalElements", totalElements);
        jsonObject.put("totalPages", totalPages);
        return JsonResult.successResult(jsonObject);
    }

    @Override
    public JsonResult save(SaveBO saveBO) {
        MetaInfo metaInfo = metaInfoService.getMetaInfo(saveBO.getMetaId());
        StringBuilder sql = new StringBuilder();
        String title;
        Optional<MetaColumnVO> metaColumnVOOptional = saveBO.getData().stream().filter(metaColumnVO1 -> "id".equals(metaColumnVO1.getCode())).findFirst();
        if (metaColumnVOOptional.isPresent()) {
            title = "修改";
            //修改
            sql.append("update ").append(metaInfo.getTableCode()).append(" set ");
            for (MetaColumnVO metaColumnVO : saveBO.getData()) {
                sql.append(metaColumnVO.getCode()).append("='").append(metaColumnVO.getValue()).append("',");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" where id=").append(metaColumnVOOptional.get().getValue());
        } else {
            title = "新增";
            //新增
            sql.append("insert into ").append(metaInfo.getTableCode()).append("(");
            if (!metaInfo.isIncrement()) {
                sql.append("id,");
            }
            StringBuilder valueSb = new StringBuilder();
            for (MetaColumnVO metaColumnVO : saveBO.getData()) {
                sql.append(metaColumnVO.getCode()).append(",");
                valueSb.append("'").append(metaColumnVO.getValue()).append("',");
            }
            sql.deleteCharAt(sql.length() - 1);
            valueSb.deleteCharAt(valueSb.length() - 1);
            sql.append(") values(");
            if (!metaInfo.isIncrement()) {
                sql.append("'").append(IdUtil.getSnowflakeNextId()).append("',");
            }
            sql.append(valueSb);
            sql.append(")");
        }
        boolean flag = nativeQueryServer.executeUpdate(sql.toString());
        if (flag) {
            return JsonResult.successResult(title + "成功");
        }
        return JsonResult.failureResult(title + "失败");
    }

    @Override
    public JsonResult delete(DeleteBO deleteBO) {
        MetaInfo metaInfo = metaInfoService.getById(deleteBO.getMetaId());
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(metaInfo.getTableCode()).append(" where id in (");
        for (Object o : deleteBO.getIdList()) {
            sb.append(o);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        boolean flag = nativeQueryServer.executeUpdate(sb.toString());
        if (flag) {
            return JsonResult.successResult("删除成功");
        }
        return JsonResult.failureResult("删除失败");
    }

    @Override
    public void export(UniversalQueryParamBO universalQueryParam, HttpServletResponse response) {
        universalQueryParam.setFirstInit(true);
        JsonResult<JSONObject> jsonResult = this.query(universalQueryParam);
        //获取通用数据
        JSONObject object = jsonResult.getData();
        JSONArray tabelData = object.getJSONArray("tabelData");
        List<MetaColumnVO> metaColumnList = object.getObject("metaColumnList", List.class);

        //构建表头信息
        List<List<String>> headList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        keyList.add("id");
        headList.add(Arrays.asList("唯一ID"));
        for (MetaColumnVO metaColumnVO : metaColumnList) {
            if (metaColumnVO.isViewShow()) {
                headList.add(Arrays.asList(metaColumnVO.getName()));
                keyList.add(metaColumnVO.getCode());
            }
        }

        //构建正文信息
        List<List<Object>> bodyList = new ArrayList<>();
        int bodySize = tabelData.size();
        JSONObject tr;
        List<Object> body;
        for (int i = 0; i < bodySize; i++) {
            tr = tabelData.getJSONObject(i);
            body = new ArrayList<>();
            bodyList.add(body);
            for (String key : keyList) {
                body.add(tr.get(key));
            }
        }
        ExcelUtil.exportData(object.getString("metaName"), headList, bodyList, response);

    }

}
