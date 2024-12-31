package com.sintrue.samples.test.common;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.object.EnhancedList;
import wang.liangchen.matrix.framework.commons.object.EnhancedMap;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;

public class HashCodeAndEqualsTests {

    @Test
    public void testEnhancedMap() {
        EnhancedMap<String, Object> source = new EnhancedMap<>();
        source.put("name", "Liangchen.Wang");
        source.put("age", 18);

        EnhancedMap<String, Object> target = new EnhancedMap<>();
        target.put("name", "Liangchen.Wang");
        target.put("age", 18);

        assert source.hashCode() == target.hashCode();
        assert source.equals(target);
        System.out.println(source);
    }

    @Test
    public void testEnhancedList() {
        EnhancedList<String> source = new EnhancedList<>();
        source.add("Liangchen.Wang");
        source.add("18");

        EnhancedList<String> target = new EnhancedList<>();
        target.add("Liangchen.Wang");
        target.add("18");

        assert source.hashCode() == target.hashCode();
        assert source.equals(target);
        System.out.println(source);
    }
    @Test
    public void testEnhancedObject(){
        EnhancedObject source = new EnhancedObject();
        source.addExtendedField("name", "Liangchen.Wang");
        source.addExtendedField("age", 18);

        EnhancedObject target = new EnhancedObject();
        target.addExtendedField("name", "Liangchen.Wang");
        target.addExtendedField("age", 18);

        assert source.hashCode() == target.hashCode();
        assert source.equals(target);
        System.out.println(source);
    }
}
