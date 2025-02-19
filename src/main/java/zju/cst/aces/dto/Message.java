package zju.cst.aces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import zju.cst.aces.constant.RoleEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private String role;
    private String content;
    private String name;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static Message of(String content) {

        return new Message(RoleEnum.USER.getValue(), content);
    }

    public static Message ofSystem(String content) {

        return new Message(RoleEnum.SYSTEM.getValue(), content);
    }

    public static Message ofAssistant(String content) {

        return new Message(RoleEnum.ASSISTANT.getValue(), content);
    }

}