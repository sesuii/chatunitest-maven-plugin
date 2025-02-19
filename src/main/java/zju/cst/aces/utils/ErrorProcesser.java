package zju.cst.aces.utils;

import zju.cst.aces.dto.TestMessage;
import zju.cst.aces.parser.ErrorParser;

import java.io.IOException;
import java.util.List;

/**
 * @author volunze
 * @date 2023/6/26 13:15
 * @className: ProcessError
 * @description: Delete unnecessary error messages
 * @version 1.0
 */
public class ErrorProcesser {
    public static String processErrorMessage(List<String> msg, int allowedTokens) throws IOException {
        if(allowedTokens<=0) {
            return "";
        }
        TestMessage testMessage = ErrorParser.loadMessage(msg);
        List<String> errors = testMessage.getErrorMessage();
        String errorMessage = String.join(" ",errors);
        while(TokenCounter.countMessageTokens(errorMessage) > allowedTokens){
            if(errorMessage.length()>50){
                errorMessage=errorMessage.substring(0,errorMessage.length()-50);
            }else{
                break;
            }
        }
        return errorMessage;
    }

}
