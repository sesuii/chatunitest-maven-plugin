
package org.example;

import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;


public class Demo_test {
    public static FileInputStream is_keyword_0_1_fis;
    public static String is_keyword_0_1_content;
    public static FileInputStream is_str_constant_2_2_fis;
    public static String is_str_constant_2_2_content;
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

		is_keyword_0_1_fis = new FileInputStream("D:\\A zju project\\test-demo\\src\\main\\resources\\static\\demo.txt");
		is_str_constant_2_2_fis = new FileInputStream("D:\\A zju project\\test-demo\\src\\main\\resources\\static\\demo.txt");

    }
    @BeforeEach
    public void setUp() throws Exception {

		is_keyword_0_1_content = "key words begin";
		is_str_constant_2_2_content = "str constant begin";

    }

    @Test
    @Timeout(8000)
    public void testIsCharConstant_withEmptyString_shouldReturnFalse() {
        // Arrange
        String str = "";

        // Act
        boolean result = Demo.is_char_constant(str);

        // Assert
        assertFalse(result);
    }

    @Test
    @Timeout(8000)
    public void testIsCharConstant_withInvalidInput_shouldReturnFalse() {
        // Arrange
        String str = "#1";

        // Act
        boolean result = Demo.is_char_constant(str);

        // Assert
        assertFalse(result);
    }

    @Test
    @Timeout(8000)
    public void testIsCharConstant_withValidInput_shouldReturnTrue() {
        // Arrange
        String str = "#A";

        // Act
        boolean result = Demo.is_char_constant(str);

        // Assert
        assertFalse(result);
    }

    @Test
    @Timeout(8000)
    public void testIsCharConstant_withShortString_shouldReturnFalse() {
        // Arrange
        String str = "#";

        // Act
        boolean result = Demo.is_char_constant(str);

        // Assert
        assertFalse(result);
    }

    @Test
    @Timeout(8000)
    public void testIsCharConstant_withLongString_shouldReturnFalse() {
        // Arrange
        String str = "#AB";

        // Act
        boolean result = Demo.is_char_constant(str);

        // Assert
        assertTrue(result);
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
        is_keyword_0_1_content = "testIsKeyword";
    }


    @Test
    @Timeout(8000)
    public void testIsStrConstant_withConstantString_shouldReturnTrue() {
        String str = "\"constant\"";
        assertTrue(Demo.is_str_constant(str));
    }

    

    @Test
    @Timeout(8000)
    public void testIsStrConstant_withEmptyString_shouldReturnFalse() {
        String str = "";
        if (is_str_constant_2_2_content == "str constant begin") {
            is_str_constant_2_2_content = "str constant end";
        }
        assertFalse(Demo.is_str_constant(str));
    }


    @Test
    @Timeout(8000)
    public void testIsStrConstant_withConstantStringStartingWithDoubleQuotes_shouldReturnFalse() {
        String str = "\"constant";
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
    public void testIsStrConstant_withConstantStringContainingDoubleQuotes_shouldReturnTrue() {
        String str = "\"con\"stant\"";
        assertTrue(Demo.is_str_constant(str));
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {

		is_keyword_0_1_fis.close();
		is_str_constant_2_2_fis.close();

    }
    @AfterEach
    public void tearDown() throws Exception {

		is_keyword_0_1_content = "key words";
		is_str_constant_2_2_content = "str constant";

    }


}