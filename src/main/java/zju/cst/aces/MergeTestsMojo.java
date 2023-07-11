package zju.cst.aces;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import zju.cst.aces.utils.TestClassMerger;

import java.io.IOException;

/**
 * merge tests
 *
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/7 18:14
 **/
@Mojo(name = "merge")
public class MergeTestsMojo extends ProjectTestMojo {

    @Parameter(property = "selectClass", required = true)
    public String selectClass;

    public MergeTestsMojo() {
        super();
    }

    @Override
    public void execute() {
        init();
        TestClassMerger testClassMerger = new TestClassMerger(selectClass);
        try {
            boolean merged = mergeInOneClass ? testClassMerger.mergeInOneClass() : testClassMerger.mergeWithSuite();
            if(merged) {
                log.info("\n==========================\n[ChatTester] Merged tests SUCCEEDED for class < " + selectClass + " > ...");
            }
        } catch (IOException e) {
            log.info("merge failed");
            throw new RuntimeException(e);
        }
    }
}
