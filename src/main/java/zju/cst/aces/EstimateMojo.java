package zju.cst.aces;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import zju.cst.aces.ProjectTestMojo;
import zju.cst.aces.utils.TestClassMerger;

import java.io.IOException;

/**
 * Estimated token consumption
 *
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/7 18:14
 **/
@Mojo(name = "estimate")
public class EstimateMojo extends ProjectTestMojo {



    @Parameter(property = "selectClass", required = true)
    public String selectClass;

    public EstimateMojo() {
        super();
    }

    @Override
    public void execute() {
        init();
        TestClassMerger testClassMerger = new TestClassMerger(selectClass);
        try {
            testClassMerger.mergeWithSuite();
        } catch (IOException e) {
            log.info("merge failed");
            throw new RuntimeException(e);
        }
    }
}
