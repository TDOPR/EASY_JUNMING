package com.haoliang.server;

import com.haoliang.enums.DataTypeEnum;
import com.haoliang.model.MetaColumn;
import com.haoliang.model.MetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dominick Li
 * @description sql执行工具类
 **/
@Component
@Slf4j
public class NativeQueryServer {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 获取sqlSession
     *
     * @return
     */
    public SqlSession getSqlSession() {
        return SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());
    }

    /**
     * 关闭sqlSession
     *
     * @param session
     */
    public void closeSqlSession(SqlSession session) {
        SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
    }

    /**
     * 执行 ddl语句
     *
     * @param sql 需要执行的sql
     * @return 执行是否成功
     */
    public boolean executeUpdate(String sql) {
        SqlSession session = getSqlSession();
        try {
            Statement statement = session.getConnection().createStatement();
            int result = statement.executeUpdate(sql);
            log.info("executeUpdate sql={} result={}", sql, result);
            return result == 0;
        } catch (Exception e) {
            log.error("executeUpdate sql={} error:{}", sql, e.getMessage());
            return false;
        } finally {
            closeSqlSession(session);
        }
    }

    /**
     * 根据MetaInfo和MetaColumn类的字段描述信息生成创建表的sql
     */
    public boolean createTable(MetaInfo metaInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table ").append(metaInfo.getTableCode()).append(" (");
        sb.append("id ");
        if (metaInfo.isIncrement()) {
            //设置主键自增
            sb.append(DataTypeEnum.INT.getValue());
            sb.append(" AUTO_INCREMENT");
        } else {
            //非自增的使用长整形，使用雪花算法手动设置id
            sb.append(DataTypeEnum.LONG.getValue());
        }
        sb.append(",");
        for (MetaColumn metaColumn : metaInfo.getMetaColumnList()) {
            sb.append(metaColumn.getColumnCode()).append(" ").append(metaColumn.getDataType());
            sb.append(",");
        }
        //设置主键
        sb.append(" PRIMARY KEY (id)");
        //设置字符集
        sb.append(" )ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;");
        return executeUpdate(sb.toString());
    }

    /**
     * 删除表
     */
    public boolean dropTable(String tableName) {
        String sql = "drop table " + tableName;
        return executeUpdate(sql);
    }

    /**
     * 新增字段
     */
    public boolean addColumn(String tableName, MetaColumn metaColumn) {
        return executeUpdate(String.format("alter table %s add %s %s", tableName, metaColumn.getColumnCode(), metaColumn.getDataType()));
    }

    /**
     * 修改字段
     */
    public boolean changeColumn(String tableName, String oldColumnCode, MetaColumn metaColumn) {
        return executeUpdate(String.format("alter table %s change %s %s %s", tableName, oldColumnCode, metaColumn.getColumnCode(), metaColumn.getDataType()));
    }

    /**
     * 删除字段
     */
    public boolean dropColumn(String tableName, String columnCode) {
        return executeUpdate(String.format("alter table %s drop %s", tableName, columnCode));
    }

    /**
     * 分页查询列表数据
     */
    public List query(String sql, List<Object> condition) {
        SqlSession session = getSqlSession();
        try {
            log.info("exec query sql={}", sql);
            PreparedStatement prepareStatement = session.getConnection().prepareStatement(sql);
            //注入参数
            Integer size = condition.size();
            if (size > 0) {
                for (int i = 1; i <= size; i++) {
                    //query的parameter 下标起始位置重1开始的
                    prepareStatement.setObject(i, condition.get(i - 1));
                }
            }
            ResultSet rs = prepareStatement.executeQuery();

            List<List<Object>> list = new ArrayList();
            List<Object> data;

            //获取sql查询字段的列数（个数）
            int columnCount = rs.getMetaData().getColumnCount();

            while(rs.next()){
                data = new ArrayList();
                list.add(data);
                for (int col = 0; col < columnCount; col++) {
                    //获取列数获取该列的字段名
                    String columnName = rs.getMetaData().getColumnName(col + 1);
                    //根据字段名获取改行当前字段的数据
                    Object name = rs.getObject(columnName);
                    data.add(name);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("executeUpdate sql={} error:{}", sql, e.getMessage());
            return Collections.emptyList();
        } finally {
            closeSqlSession(session);
        }
    }


    public static void main(String[] args) {
        System.out.println(String.format("alter table %s add %s %s", "1", "2", "3"));
    }

}
