package org.example;

/**
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/12 10:11
 **/
public class Demo {

    public static boolean is_keyword(String str) {
        if (str.equals("and") || str.equals("or") || str.equals("if") ||
                str.equals("xor") || str.equals("lambda") || str.equals("=>")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean is_char_constant(String str) {
        if (str.length() > 2 && str.charAt(0) == '#' && Character.isLetter(str.charAt(1))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean is_str_constant(String str) {
        if(str.length() == 0) {
            return false;
        }
        int i = 1;
        /* other return FALSE */
        if (str.charAt(0) == '"') {
            /* until meet the token end sign */
            while (i < str.length() && str.charAt(0) != '\0') {
                if (str.charAt(i) == '"') {
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }
}
