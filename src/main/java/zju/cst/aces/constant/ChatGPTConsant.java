package zju.cst.aces.constant;

/**
 * The constants for ChatGPT
 *
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/6 19:41
 **/
public class ChatGPTConsant {
    /**
     * The default model for ChatGPT
     */
    public static final String DEFAULT_GPT_MODEL = "gpt-3.5-turbo";

    /**
     * The default consume tokens per message
     * every message follows <|start|>{role/name}\n{content}<|end|>\n
     */
    public static final int DEFAULT_TOKENS_PER_MESSAGE = 4;

    /**
     * if there's a name, the role is omitted
     */
    public static final int DEFAULT_TOKENS_PER_NAME = -1;

    /**
     * The default temperature for ChatGPT
     */
    public static final double DEFAULT_TEMPERATURE = 0.5;

    /**
     * The default top probability for ChatGPT
     */
    public static final int DEFAULT_TOP_PROBABILITY = 1;

    /**
     * The default frequency penalty for ChatGPT
     */
    public static final int DEFAULT_FREQUENCY_PENALTY = 0;

    /**
     * The default presence penalty for ChatGPT
     */
    public static final int DEFAULT_PRESENCE_PENALTY = 0;
}
