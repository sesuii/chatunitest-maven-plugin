package org.example;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    org.example.Demo_is_char_constant_1_1_Test.class,
    org.example.Demo_is_str_constant_2_2_Test.class,
    org.example.Demo_is_keyword_0_1_Test.class,
})
public class DemoSuiteTest {

}