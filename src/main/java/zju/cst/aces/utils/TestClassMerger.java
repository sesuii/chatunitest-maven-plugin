package zju.cst.aces.utils;

import zju.cst.aces.config.Config;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.AbstractRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static zju.cst.aces.ProjectTestMojo.GSON;

/**
 * Merge all test classes with different methods int the same class.
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/10 16:47
 **/
public class TestClassMerger {

    public static String tmpTestOutput = "/tmp/chatunitest-info";
    public static String parseTestOutput;
    private String sourceClassName;

    private Set<String> importStatements;

    private List<MethodInfo> methodInfos;



    public TestClassMerger(String className) {
        this.sourceClassName = className;
    }

    public boolean mergeWithSuite() throws IOException {
        if(!preProcessing()) {
            return false;
        }
        Set<String> testClassFullNames = new HashSet<>();
        Path testClassMapPath = Config.testClassMapPath;
        Map<String, List<String>> testClassMap = GSON.fromJson(Files.readString(testClassMapPath, StandardCharsets.UTF_8), Map.class);
        if(testClassMap.containsKey(sourceClassName + "_suiteTest")) {
            String filePath = testClassMap.get(sourceClassName + "_suiteTest").get(0).replace(".", File.separator) + ".java";
            Path testSuitePath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java", filePath);
            Files.deleteIfExists(testSuitePath);
            testClassMap.remove(sourceClassName + "_suiteTest");
        }
        AtomicReference<String> packageName = new AtomicReference<>("");
        testClassMap.forEach((k, v) -> {
            if (k.startsWith(sourceClassName + "_")) {
                testClassFullNames.add(v.get(0));
                if (packageName.get().equals("")) {
                    packageName.set("package " + v.get(0).substring(0, v.get(0).lastIndexOf(".")));
                }
            }
        });
        if(testClassFullNames.size() == 0) {
            return false;
        }
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(packageName.get())
                .append(";\n\n")
                .append("import org.junit.platform.suite.api.SelectClasses;\n" +
                        "import org.junit.platform.suite.api.Suite;\n\n")
                .append("@Suite\n@SelectClasses({\n");
        testClassFullNames.forEach(testClass ->{
            codeBuilder.append("    ").append(testClass).append(".class,\n");
        });
        codeBuilder.append("})\npublic class ").append(sourceClassName).append("_suiteTest {\n\n}");
        Path srcTestJavaPath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java");
        Path savePath = srcTestJavaPath.resolve(packageName.get().replace("package ", "")
                .replace(".", File.separator))
                .resolve(sourceClassName + "_suiteTest.java");
        AbstractRunner.exportTest(codeBuilder.toString(), savePath);
        return true;
    }

    public boolean mergeInOneClass() {
        if(!preProcessing()) {
            return false;
        }
        // TODO
        //  1. 生成 testclass-info.json
        //  2. 从 testclass-info.json 中读取信息
        //  3. 合并 import 语句、合并 setUp 和 tearDown 方法
        //  4. 合并所有 method
        //  5. 组合生成新的 test class
        //  6. 编译运行
        return false;
    }

    public boolean preProcessing() {
        Path srcTestJavaPath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java");
        if(!srcTestJavaPath.toFile().exists()) {
            return false;
        }
        tmpTestOutput = String.valueOf(Paths.get(tmpTestOutput, Config.project.getArtifactId()));
        parseTestOutput = tmpTestOutput + File.separator + "test-class-info";
        parseTestOutput = parseTestOutput.replace("/", File.separator);
        Config.setTestClassMapPath(Paths.get(parseTestOutput, "class-map.json"));
        ProjectParser parser = new ProjectParser(srcTestJavaPath.toString(), parseTestOutput);
        parser.parse();
        return true;
    }

}
