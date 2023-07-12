package org.example;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Demo_is_keyword_0_1_Test {

    public static FileInputStream fis;
    public static String content;

    @BeforeAll
    public static void setup() throws FileNotFoundException {
        fis = new FileInputStream("D:\\A zju project\\test-demo\\src\\main\\resources\\static\\demo.txt");
    }

    @BeforeEach
    public void setupEach() {
        content = "key words begin";
    }

    @Test
    @Timeout(8000)
    public void testIsKeyword() {
        // Test when the input string is "and"
        assertTrue(Demo.is_keyword("and"));
        
        // Test when the input string is "or"
        assertTrue(Demo.is_keyword("or"));
        
        // Test when the input string is "if"
        assertTrue(Demo.is_keyword("if"));
        
        // Test when the input string is "xor"
        assertTrue(Demo.is_keyword("xor"));
        
        // Test when the input string is "lambda"
        assertTrue(Demo.is_keyword("lambda"));
        
        // Test when the input string is "=>"
        assertTrue(Demo.is_keyword("=>"));
        
        // Test when the input string is not a keyword
        assertFalse(Demo.is_keyword("not_keyword"));

        content = "testIsKeyword";
    }

    @AfterEach
    public void teardownEach() {
        content = "key words";
    }

    @AfterAll
    public static void teardown() throws IOException {
        fis.close();
    }
}