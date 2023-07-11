package zju.cst.aces.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import zju.cst.aces.config.Config;
import zju.cst.aces.dto.ClassInfo;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.parser.ClassParser;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.AbstractRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import static zju.cst.aces.ProjectTestMojo.getFullClassName;

/**
 * Merge all test classes with different methods in the same class.
 * @author <a href="mailto: sjiahui27@gmail.com">songjiahui</a>
 * @since 2023/7/10 16:47
 **/
public class TestClassMerger {

    public static String tmpTestOutput = "/tmp/chatunitest-info";
    public static String parseTestOutput;
    private String sourceClassName;

    private String sourceFullClassName;

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private String targetClassName;

    public Set<String> importStatements = new HashSet<>();

    public String packageName;

    public StringBuilder fieldsCode = new StringBuilder("\n");

    public StringBuilder methodsCode = new StringBuilder("\n");

    public StringBuilder beforeAllCode = new StringBuilder("\n");

    public StringBuilder beforeEachCode = new StringBuilder("\n");

    public StringBuilder afterAllCode = new StringBuilder("\n");

    public StringBuilder afterEachCode = new StringBuilder("\n");

    public TestClassMerger(String className) {
        this.sourceClassName = className;
        try {
            this.sourceFullClassName = getFullClassName(className);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * merge by adding suite annotation, only Junit5 is supported now.
     *
     * @return true if the test class is merged successfully.
     * @throws IOException
     */
    public boolean mergeWithSuite() throws IOException {
        preProcessing();
        targetClassName = sourceClassName + "SuiteTest";
        List<String> testClassFullNames = new ArrayList<>();
        Path testClassMapPath = Config.testClassMapPath;
        Map<String, List<String>> testClassMap = GSON.fromJson(Files.readString(testClassMapPath, StandardCharsets.UTF_8), Map.class);
        testClassMap.forEach((k, v) -> {
            if (k.startsWith(sourceClassName + "_")) {
                testClassFullNames.add(v.get(0));
            }
        });
        if(testClassFullNames.size() == 0) {
            return false;
        }
        packageName = "package " + testClassFullNames.get(0).substring(0, testClassFullNames.get(0).lastIndexOf(".")) + ";";
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(packageName)
                .append("\n\n")
                .append("import org.junit.platform.suite.api.SelectClasses;\n" +
                        "import org.junit.platform.suite.api.Suite;\n\n")
                .append("@Suite\n@SelectClasses({\n");
        testClassFullNames.forEach(testClass ->{
            codeBuilder.append("    ").append(testClass).append(".class,\n");
        });
        codeBuilder.append("})\npublic class ").append(sourceClassName).append("SuiteTest {\n\n}");
        deleteRepeatTestFile(Collections.singletonList(targetClassName));
        return export(String.valueOf(codeBuilder));
    }

    /**
     * merge all test classes with different methods in the same class.
     *
     * @return true if merge successfully, false otherwise.
     */
    public boolean mergeInOneClass() throws IOException {
        ProjectParser parser = preProcessing();
        if(parser == null) {
            return false;
        }
        List<String> fieldNameToDelete = new ArrayList<>();
        targetClassName = sourceClassName + "_test";
        Path srcTestJavaPath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java");
        List<String> classPaths = new ArrayList<>();
        parser.scanSourceDirectory(srcTestJavaPath.toFile(), classPaths);
        classPaths.forEach(classPath -> {
            String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
            if(className.startsWith(sourceClassName + "_")) {
                try {
                    File classDir  =  new File(parseTestOutput + File.separator +
                            sourceFullClassName.substring(0, sourceFullClassName.lastIndexOf("."))
                                    .replace(".", File.separator) + File.separator + className);
                    File classInfoFile =  new File(classDir + File.separator + "class.json");
                    ClassInfo classInfo = GSON.fromJson(Files.readString(classInfoFile.toPath(), StandardCharsets.UTF_8), ClassInfo.class);
                    importStatements.addAll(classInfo.imports);
                    fieldNameToDelete.add(classInfo.className);
                    extractFieldsAndMethods(classInfo, classDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        deleteRepeatTestFile(fieldNameToDelete);
        return mergeCodeSnippets();
    }

    private void extractFieldsAndMethods(ClassInfo classInfo, File classDir) throws IOException {
        List<MethodInfo> methodInfos = new ArrayList<>();
        for(String mSigIdx: classInfo.methodSignatures.values()) {
            File methodFile = new File(classDir + File.separator + mSigIdx + ".json");
            MethodInfo methodInfo =  GSON.fromJson(Files.readString(methodFile.toPath(), StandardCharsets.UTF_8), MethodInfo.class);
            methodInfos.add(methodInfo);
        }
        packageName = classInfo.packageDeclaration;
        String className = classInfo.className;
        if(!classInfo.fields.isEmpty()) {
            classInfo.fields.forEach(line -> {
                String variableName;
                if(line.contains("=")) {
                    // field with initialization
                    String front = line.substring(0, line.indexOf("=")).trim();
                    variableName = front.substring(front.lastIndexOf(" ") + 1);
                }
                else {
                    // field without initialization
                    variableName = line.substring(line.lastIndexOf(" ") + 1, line.length() - 1);
                }
                String replacedName = className.substring(className.indexOf("_") + 1, className.lastIndexOf("_")) + "_" + variableName;
                fieldsCode.append(line.replace(variableName, replacedName));
                fieldsCode.append("\n");
                methodInfos.forEach(methodInfo -> {
                    methodInfo.sourceCode = ClassParser.renameVariable(methodInfo.sourceCode, variableName, replacedName);
                });
            });
        }
        methodInfos.forEach(methodInfo -> {
            if(methodInfo.sourceCode.contains("@Test")) {
                methodsCode.append("    ").append(methodInfo.sourceCode).append("\n\n");
            }
            else if(methodInfo.sourceCode.contains("@AfterAll")) {
                extractMethodBody(methodInfo.sourceCode, afterAllCode);
            }
            else if(methodInfo.sourceCode.contains("@BeforeEach")) {
                extractMethodBody(methodInfo.sourceCode, beforeEachCode);
            }
            else if(methodInfo.sourceCode.contains("@AfterEach")) {
                extractMethodBody(methodInfo.sourceCode, afterEachCode);
            }
            else if(methodInfo.sourceCode.contains("@BeforeAll")) {
                extractMethodBody(methodInfo.sourceCode, beforeAllCode);
            }
        });
    }

    private void extractMethodBody(String sourceCode, StringBuilder body) {
        body.append(sourceCode, sourceCode.indexOf("{") + 1, sourceCode.lastIndexOf("}\n"));
    }

    private boolean mergeCodeSnippets() {
        StringBuilder code = new StringBuilder();
        code.append("\n").append(packageName).append("\n\n");
        importStatements.forEach(importStatement -> code.append(importStatement).append("\n"));
        extractSetUpAndTearDown();
        code.append("\n\npublic class ")
                .append(targetClassName).append(" {")
                .append(fieldsCode)
                .append(beforeAllCode)
                .append(beforeEachCode)
                .append(methodsCode)
                .append(afterAllCode)
                .append(afterEachCode).append("\n\n}");
        deleteRepeatTestFile(Collections.singletonList(targetClassName));
        return export(String.valueOf(code));
    }

    private void extractSetUpAndTearDown() {
        if(beforeAllCode.length() > 1) {
            beforeAllCode.insert(0, "@BeforeAll\npublic static void setUpBeforeClass() throws Exception {\n");
            beforeAllCode.append("\n}\n");
        }
        if(beforeEachCode.length() > 1) {
            beforeEachCode.insert(0, "@BeforeEach\npublic void setUp() throws Exception {\n");
            beforeEachCode.append("\n}\n");
        }
        if(afterAllCode.length() > 1) {
            afterAllCode.insert(0, "@AfterAll\npublic static void tearDownAfterClass() throws Exception {\n");
            afterAllCode.append("\n}\n");
        }
        if(afterEachCode.length() > 1) {
            afterEachCode.insert(0, "@AfterEach\npublic void tearDown() throws Exception {\n");
            afterEachCode.append("\n}\n");
        }
    }

    private void deleteRepeatTestFile(List<String> classNameToDel) {
        Path srcTestJavaPath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java");
        List<String> classPaths = new ArrayList<>();
        ProjectParser parser = new ProjectParser(srcTestJavaPath.toString(), parseTestOutput);
        parser.scanSourceDirectory(srcTestJavaPath.toFile(), classPaths);
        classPaths.forEach(classPath -> {
            String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
            if(classNameToDel.contains(className)) {
                File classFile = new File(classPath);
                classFile.delete();
            }
        });
    }

    public ProjectParser preProcessing() {
        Path srcTestJavaPath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java");
        if(!srcTestJavaPath.toFile().exists()) {
            return null;
        }
        tmpTestOutput = String.valueOf(Paths.get(tmpTestOutput, Config.project.getArtifactId()));
        parseTestOutput = tmpTestOutput + File.separator + "test-class-info";
        parseTestOutput = parseTestOutput.replace("/", File.separator);
        Config.setTestClassMapPath(Paths.get(parseTestOutput, "class-map.json"));
        ProjectParser parser = new ProjectParser(srcTestJavaPath.toString(), parseTestOutput);
        parser.parse();
        return parser;
    }

    public boolean export(String code) {
        Path srcTestJavaPath = Paths.get(Config.project.getBasedir().getAbsolutePath(), "src", "test", "java");
        Path savePath = srcTestJavaPath.resolve(packageName.replace("package ", "")
                        .replace(";", "")
                        .replace(".", File.separator))
                .resolve(targetClassName + ".java");
        AbstractRunner.exportTest(code, savePath);
        return true;
    }

}
