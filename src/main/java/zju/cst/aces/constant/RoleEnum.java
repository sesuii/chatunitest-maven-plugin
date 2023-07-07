package zju.cst.aces.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum of Role in Message
 *
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/6 20:19
 **/
@Getter
@AllArgsConstructor
public enum RoleEnum {
    // Message 中 Role 的枚举值
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    ;
    private final String value;
}
