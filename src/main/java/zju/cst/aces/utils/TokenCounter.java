package zju.cst.aces.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import zju.cst.aces.constant.ChatGPTConsant;

import java.util.List;

/**
 * estimate the consumption of GPT tokens.
 *
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/6 17:03
 **/
public class TokenCounter {

    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();

    public static int countMessageTokens(Message message) {
        Encoding encoding = REGISTRY.getEncodingForModel(ChatGPTConsant.DEFAULT_GPT_MODEL).orElseThrow();
        int tokensSum = 0;
        tokensSum += ChatGPTConsant.DEFAULT_TOKENS_PER_MESSAGE;
        tokensSum += encoding.countTokens(message.getContent());
        tokensSum += encoding.countTokens(message.getRole());
        return tokensSum;
    }

    public static int countMessageTokens(List<Message> messages) {
        int tokensSum = 0;
        for (Message message : messages) {
            tokensSum += countMessageTokens(message);
        }
        // every reply is primed with <|start|>assistant<|message|>
        tokensSum += 3;
        return tokensSum;
    }

    public static int countMessageTokens(String message) {
        Encoding encoding = REGISTRY.getEncodingForModel(ChatGPTConsant.DEFAULT_GPT_MODEL).orElseThrow();
        return encoding.countTokens(message);
    }
}