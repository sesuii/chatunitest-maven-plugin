package zju.cst.aces;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.config.Config;
import zju.cst.aces.utils.TestCompiler;
import zju.cst.aces.utils.TotalTokensObserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author chenyi
 * ChatUniTest maven plugin
 */

@Mojo(name = "project")
public class ProjectTestMojo
        extends AbstractMojo {
    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    public MavenSession session;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    public MavenProject project;
    @Parameter(defaultValue = "chatunitest-tests", property = "testOutput")
    public String testOutput;
    @Parameter(defaultValue = "/tmp/chatunitest-info", property = "tmpOutput")
    public String tmpOutput;
    @Parameter(name = "apiKeys", required = true)
    public String[] apiKeys;
    @Parameter(name = "stopWhenSuccess", property = "stopWhenSuccess", defaultValue = "true")
    public boolean stopWhenSuccess;
    @Parameter(alias = "thread", property = "thread", defaultValue = "true")
    public boolean enableMultithreading;
    @Parameter(property = "maxThreads", defaultValue = "0")
    public int maxThreads;

    @Parameter(name = "testNumber", defaultValue = "5")
    public int testNumber;
    @Parameter(name = "maxRounds", defaultValue = "6")
    public int maxRounds;
    @Parameter(name = "minErrorTokens", defaultValue = "500")
    public int minErrorTokens;
    @Parameter(name = "maxPromptTokens", defaultValue = "2600")
    public int maxPromptTokens;
    @Parameter(name = "model", defaultValue = "gpt-3.5-turbo")
    public String model;
    @Parameter(name = "temperature", defaultValue = "0.5")
    public Double temperature;
    @Parameter(name = "topP", defaultValue = "1")
    public  int topP;
    @Parameter(name = "frequencyPenalty", defaultValue = "0")
    public int frequencyPenalty;
    @Parameter(name = "presencePenalty", defaultValue = "0")
    public int presencePenalty;
    @Parameter(name = "proxy",defaultValue = "null:-1")
    public String proxy;

    @Parameter(property = "mergeInOneClass", defaultValue = "true")
    public boolean mergeInOneClass;

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    @Component(hint = "default")
    public DependencyGraphBuilder dependencyGraphBuilder;
    public String parseOutput;
    public static Log log;
    public static int classThreads;
    public static int methodThreads;
    protected boolean isMavenDirect;

    public static TotalTokensObserver totalTokensObserver;



    public ProjectTestMojo() {
        super();
        this.isMavenDirect = true;
    }

    public ProjectTestMojo(boolean isMavenDirect) {
        super();
        this.isMavenDirect = isMavenDirect;
    }


    /**
     * Generate tests for all classes in the project
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        if(isMavenDirect) {
            init();
        }
        else {
            // TODO: Init for Gradle
        }
        log.info("\n==========================\n[ChatTester] Generating tests for project " + project.getBasedir().getName() + " ...");
        log.warn("[ChatTester] It may consume a significant number of tokens!");
        // ANNO 判断项目根目录是否存在
        Path srcMainJavaPath = Paths.get(project.getBasedir().getAbsolutePath(), "src", "main", "java");
        if (!srcMainJavaPath.toFile().exists()) {
            log.error("\n==========================\n[ChatTester] No compile source found in " + project);
            return;
        }
        // ANNO parseOutput 为项目根目录下的 chatunitest-info 文件夹
        ProjectParser parser = new ProjectParser(srcMainJavaPath.toString(), parseOutput);
        if (! (new File(parseOutput).exists())) {
            log.info("\n==========================\n[ChatTester] Parsing class info ...");
            parser.parse();
            log.info("\n==========================\n[ChatTester] Parse finished");
        }
        // ANNO 获取项目根目录下的所有 java 文件的路径
        List<String> classPaths = new ArrayList<>();
        parser.scanSourceDirectory(srcMainJavaPath.toFile(), classPaths);

        // ANNO 如果测试目录下有文件，先备份到src.backup目录下，再删除测试目录下的文件
        TestCompiler.backupTestFolder();

        if (Config.enableMultithreading) {
            classJob(classPaths);
        } else {
            for (String classPath : classPaths) {
                // ANNO 提取类名
                String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
                try {
                    className = getFullClassName(className);
                    log.info("\n==========================\n[ChatTester] Generating tests for class < " + className + " > ...");
                    // ANNO 生成测试用例，参数为全限定类名，class-info，测试输出路径
                    new ClassRunner(className, parseOutput, testOutput).start();
                } catch (IOException e) {
                    log.error("[ChatTester] Generate tests for class " + className + " failed: " + e);
                }
            }
        }
//        TestCompiler.restoreTestFolder();
        log.info("\n==========================\n[ChatTester] Generation finished");
    }

    public void classJob(List<String> classPaths) {
        ExecutorService executor = Executors.newFixedThreadPool(classThreads);
        List<Future<String>> futures = new ArrayList<>();
        for (String classPath : classPaths) {
            Callable<String> callable = () -> {
                String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
                try {
                    className = getFullClassName(className);
                    log.info("\n==========================\n[ChatTester] Generating tests for class < " + className + " > ...");
                    new ClassRunner(className, parseOutput, testOutput).start();
                } catch (IOException e) {
                    log.error("[ChatTester] Generate tests for class " + className + " failed: " + e);
                }
                return "Processed " + classPath;
            };
            Future<String> future = executor.submit(callable);
            futures.add(future);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    /**
     * 初始化
     */
    public void init() {
        Config.setSession(session);
        Config.setProject(project);
        Config.setDependencyGraphBuilder(dependencyGraphBuilder);
        Config.setApiKeys(apiKeys);
        Config.setModel(model);
        Config.setStopWhenSuccess(stopWhenSuccess);
        Config.setEnableMultithreading(enableMultithreading);
        Config.setMaxThreads(maxThreads);
        Config.setTestNumber(testNumber);
        Config.setMaxRounds(maxRounds);
        Config.setMinErrorTokens(minErrorTokens);
        Config.setMaxPromptTokens(maxPromptTokens);
        Config.setTemperature(temperature);
        Config.setTopP(topP);
        Config.setFrequencyPenalty(frequencyPenalty);
        Config.setPresencePenalty(presencePenalty);
        Config.setProxy(proxy);
        totalTokensObserver = new TotalTokensObserver(log);
        tmpOutput = String.valueOf(Paths.get(tmpOutput, project.getArtifactId()));
        parseOutput = tmpOutput + File.separator + "class-info";
        parseOutput = parseOutput.replace("/", File.separator);
        Config.setClassMapPath(Paths.get(parseOutput, "class-map.json"));
        log = getLog();
        classThreads = Config.maxThreads / 10;
        methodThreads = Config.maxThreads / classThreads;
        log.info("\n==========================\n[ChatTester] Multithreading enabled >>>> " + Config.enableMultithreading);
        if (!Config.stopWhenSuccess) {
            methodThreads = methodThreads / Config.testNumber;
        }
        if (Config.enableMultithreading) {
            log.info("Class threads: " + classThreads + ", Method threads: " + methodThreads);
        }
    }

    /**
     * 获取类的全限定名 例如：org.example.demo.Demo
     *
     * @param name 类名
     * @return
     */
    public static String getFullClassName(String name) throws IOException {
        if (isFullName(name)) {
            return name;
        }
        Path classMapPath = Config.classMapPath;
        Map<String, List<String>> classMap = GSON.fromJson(Files.readString(classMapPath, StandardCharsets.UTF_8), Map.class);
        if (classMap.containsKey(name)) {
            if (classMap.get(name).size() > 1) {
                throw new RuntimeException("[ChatTester] Multiple classes Named " + name + ": " + classMap.get(name)
                + " Please use full qualified name!");
            }
            return classMap.get(name).get(0);
        }
        return name;
    }

    public static boolean isFullName(String name) {
        return name.contains(".");
    }
}
