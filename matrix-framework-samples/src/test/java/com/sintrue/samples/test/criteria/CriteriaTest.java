package com.sintrue.samples.test.criteria;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.data.context.DataSourceContext;
import wang.liangchen.matrix.framework.data.criteria.Criteria;
import wang.liangchen.matrix.framework.data.criteria.CriteriaParameter;
import wang.liangchen.matrix.framework.data.criteria.CriteriaResolver;
import wang.liangchen.matrix.framework.data.datasource.dialect.MySQLDialect;

/**
 * @author LiangChen.Wang 2024/11/16 11:47
 */
public class CriteriaTest {
    @Test
    public void testSelect() {
        // set context
        DataSourceContext.INSTANCE.set("primary");
        DataSourceContext.INSTANCE.putDataSource("primary", null, new MySQLDialect());


        Criteria<DemoEntity> criteria = Criteria.of(DemoEntity.class);
        criteria.selectFields(DemoEntity::getDemoId, DemoEntity::getDemoName);
        criteria._equals(DemoEntity::getDemoId, 1)._notEquals(DemoEntity::getDemoName, "test");
        CriteriaParameter<DemoEntity> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);


        String json = JacksonUtil.INSTANCE.writeValueAsString(criteriaParameter);
        System.out.println(json);
    }
}
