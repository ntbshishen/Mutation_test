/*
 * This file was automatically generated by EvoSuite
 * Fri Dec 29 07:16:38 GMT 2023
 */

package com.example.demo.MyTest;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.demo.MyTest.meOne;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class meOne_ESTest extends meOne_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      meOne meOne0 = new meOne();
      meOne0.testOne(0);
  }
}
