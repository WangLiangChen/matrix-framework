package com.sintrue.samples.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.spring.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "test")
public class TestEntity extends RootEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MATRIX_FLAKE)
    private Long testId;
    private String testName;
    private LocalDate testDate;
    private LocalDateTime testDateTime;
    private Integer testInt;
    private String test0;
    private String test1;
    private String test2;
    private String test3;
    private String test4;
    private String test5;
    private String test6;
    private String test7;
    private String test8;
    private String test9;
    private String test10;
    private String test11;
    private String test12;
    private String test13;
    private String test14;
    private String test15;
    private String test16;
    private String test17;
    private String test18;
    private String test19;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public LocalDateTime getTestDateTime() {
        return testDateTime;
    }

    public void setTestDateTime(LocalDateTime testDateTime) {
        this.testDateTime = testDateTime;
    }

    public Integer getTestInt() {
        return testInt;
    }

    public void setTestInt(Integer testInt) {
        this.testInt = testInt;
    }

    public String getTest0() {
        return test0;
    }

    public void setTest0(String test0) {
        this.test0 = test0;
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setTest2(String test2) {
        this.test2 = test2;
    }

    public String getTest3() {
        return test3;
    }

    public void setTest3(String test3) {
        this.test3 = test3;
    }

    public String getTest4() {
        return test4;
    }

    public void setTest4(String test4) {
        this.test4 = test4;
    }

    public String getTest5() {
        return test5;
    }

    public void setTest5(String test5) {
        this.test5 = test5;
    }

    public String getTest6() {
        return test6;
    }

    public void setTest6(String test6) {
        this.test6 = test6;
    }

    public String getTest7() {
        return test7;
    }

    public void setTest7(String test7) {
        this.test7 = test7;
    }

    public String getTest8() {
        return test8;
    }

    public void setTest8(String test8) {
        this.test8 = test8;
    }

    public String getTest9() {
        return test9;
    }

    public void setTest9(String test9) {
        this.test9 = test9;
    }

    public String getTest10() {
        return test10;
    }

    public void setTest10(String test10) {
        this.test10 = test10;
    }

    public String getTest11() {
        return test11;
    }

    public void setTest11(String test11) {
        this.test11 = test11;
    }

    public String getTest12() {
        return test12;
    }

    public void setTest12(String test12) {
        this.test12 = test12;
    }

    public String getTest13() {
        return test13;
    }

    public void setTest13(String test13) {
        this.test13 = test13;
    }

    public String getTest14() {
        return test14;
    }

    public void setTest14(String test14) {
        this.test14 = test14;
    }

    public String getTest15() {
        return test15;
    }

    public void setTest15(String test15) {
        this.test15 = test15;
    }

    public String getTest16() {
        return test16;
    }

    public void setTest16(String test16) {
        this.test16 = test16;
    }

    public String getTest17() {
        return test17;
    }

    public void setTest17(String test17) {
        this.test17 = test17;
    }

    public String getTest18() {
        return test18;
    }

    public void setTest18(String test18) {
        this.test18 = test18;
    }

    public String getTest19() {
        return test19;
    }

    public void setTest19(String test19) {
        this.test19 = test19;
    }
}
