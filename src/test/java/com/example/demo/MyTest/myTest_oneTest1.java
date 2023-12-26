package com.example.demo.MyTest;

import org.junit.Test;
import static org.junit.Assert.*;

public class myTest_oneTest1 {
    @Test
    public void testTestOne() {
        myTest_one testObject = new myTest_one();
        testObject.testOne(0);
    }

    @Test
    public void testTestOne2() {
        myTest_one testObject = new myTest_one();
        testObject.testOne(1);
    }
}