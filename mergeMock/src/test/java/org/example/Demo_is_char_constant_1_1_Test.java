package org.example;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class Demo_is_char_constant_1_1_Test {

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
}