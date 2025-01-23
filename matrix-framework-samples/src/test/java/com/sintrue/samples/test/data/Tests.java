package com.sintrue.samples.test.data;

import com.sintrue.samples.dao.entity.TestEntity;
import com.sintrue.samples.service.TestService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class Tests {
    @Inject
    private TestService testService;

    @Test
    public void testInsert() {
        List<TestEntity> entities = createList();
    }

    private List<TestEntity> createList() {
        long time = System.currentTimeMillis();
        List<TestEntity> entities = new ArrayList<>();
        for (int i = 0; i < 500000; i++) {
            TestEntity entity = new TestEntity();
            entity.setTestName("name_" + i);
            entity.setTestDate(LocalDate.now());
            entity.setTestDateTime(LocalDateTime.now());
            entity.setTestInt(ThreadLocalRandom.current().nextInt());
            entity.setTest0("test0" + i);
            entity.setTest1("test1" + i);
            entity.setTest2("test2" + i);
            entity.setTest3("test3" + i);
            entity.setTest4("test4" + i);
            entity.setTest5("test5" + i);
            entity.setTest6("test6" + i);
            entity.setTest7("test7" + i);
            entity.setTest8("test8" + i);
            entity.setTest9("test9" + i);
            entity.setTest10("test10" + i);
            entity.setTest11("test11" + i);
            entity.setTest12("test12" + i);
            entity.setTest13("test13" + i);
            entity.setTest14("test14" + i);
            entity.setTest15("test15" + i);
            entity.setTest16("test16" + i);
            entity.setTest17("test17" + i);
            entity.setTest18("test18" + i);
            entity.setTest19("test19" + i);
            entities.add(entity);
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Time to create list: " + time);
        return entities;
    }


}
