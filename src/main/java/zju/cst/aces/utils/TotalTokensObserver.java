package zju.cst.aces.utils;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import zju.cst.aces.constant.ChatGPTConsant;
import zju.cst.aces.constant.ProjectConstant;

/**
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/13 11:22
 **/
public class TotalTokensObserver {
    private Log log;
    private int totalTokens;

    public TotalTokensObserver(Log log) {
        this.totalTokens = 0;
        this.log = log;
    }

    private Log getLog() {
        if(this.log == null) {
            this.log = new SystemStreamLog();
        }
        return this.log;
    }

    public synchronized void update(int consumedTokens) throws RuntimeException {
        if(totalTokens + consumedTokens > ProjectConstant.DEFAULT_MAX_TOTAL_TOKENS) {
            throw new RuntimeException("Total tokens exceeded the limit. Current consumption: " + totalTokens + consumedTokens);
        }
        totalTokens += consumedTokens;
        getLog().info("\n<<<<<<<<<<< Current consumed tokens: " + totalTokens + " >>>>>>>>>>>\n");
    }
}
