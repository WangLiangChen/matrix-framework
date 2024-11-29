package com.sintrue.samples.test.criteria;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.data.criteria.Criteria;
import wang.liangchen.matrix.framework.data.criteria.CriteriaParameter;
import wang.liangchen.matrix.framework.data.criteria.CriteriaResolver;

/**
 * @author LiangChen.Wang 2024/11/16 11:47
 */
public class CriteriaTest {
    @Test
    public void testSelect() {
        Criteria<DemoEntity> criteria = Criteria.of(DemoEntity.class);
        CriteriaParameter<DemoEntity> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        System.out.println();
    }
}
