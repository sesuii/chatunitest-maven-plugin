package org.example;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Demo_is_str_constant_2_2_Test {

    public static FileInputStream fis;
    public static String content;

    @BeforeAll
    public static void setup() throws FileNotFoundException {
        fis = new FileInputStream("D:\\A zju project\\test-demo\\src\\main\\resources\\static\\demo.txt");
    }

    @BeforeEach
    public void setupEach() {
        content = "str constant begin";
    }


    @Test
    @Timeout(8000)
    public void testIsStrConstant_withEmptyString_shouldReturnFalse() {
        String str = "";
        if(content == "str constant begin") {
            content = "str constant end";
        }
        assertFalse(Demo.is_str_constant(str));
    }

    @Test
    @Timeout(8000)
    public void testIsStrConstant_withNonConstantString_shouldReturnFalse() {
        String str = "Hello, World!";
        assertFalse(Demo.is_str_constant(str));
    }

    @Test
    @Timeout(8000)
    public void testIsStrConstant_withConstantString_shouldReturnTrue() {
        String str = "\"constant\"";
        assertTrue(Demo.is_str_constant(str));
    }

    @Test
    @Timeout(8000)
    public void testIsStrConstant_withConstantStringContainingDoubleQuotes_shouldReturnTrue() {
        String str = "\"con\"stant\"";
        assertTrue(Demo.is_str_constant(str));
    }

    @Test
    @Timeout(8000)
    public void testIsStrConstant_withConstantStringStartingWithDoubleQuotes_shouldReturnFalse() {
        String str = "\"constant";
        assertFalse(Demo.is_str_constant(str));
    }

    @AfterEach
    public void teardownEach() {
        content = "str constant";
    }

    @AfterAll
    public static void teardown() throws IOException {
        fis.close();
    }

}