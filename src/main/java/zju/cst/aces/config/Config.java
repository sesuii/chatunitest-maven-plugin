package zju.cst.aces.config;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import zju.cst.aces.constant.ChatGPTConsant;
import zju.cst.aces.constant.ProjectConstant;

import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Config {

    public static String OS = System.getProperty("os.name").toLowerCase();
    public static MavenSession session;
    public static MavenProject project;
    public static DependencyGraphBuilder dependencyGraphBuilder;
    public static boolean stopWhenSuccess = ProjectConstant.DEFAULT_STOP_WHEN_SUCCESS;
    public static boolean enableMultithreading = ProjectConstant.DEFAULT_ENABLE_MULTI_THREADING;
    public static int maxThreads = ProjectConstant.DEFAULT_MAX_THREADS;
    public static int testNumber = ProjectConstant.DEFAULT_TEST_NUMBER;
    public static int timeOut;
    public static int processNumber;
    public static String resultDir;
    public static String projectDir;
    public static int maxRounds = ProjectConstant.DEFAULT_MAX_ROUNDS;
    public static int maxPromptTokens = ProjectConstant.DEFAULT_MAX_PROMPT_TOKENS;
    public static int minErrorTokens = ProjectConstant.DEFAULT_MIN_ERROR_TOKENS;

    public static String model = ChatGPTConsant.DEFAULT_GPT_MODEL;
    public static Double temperature = ChatGPTConsant.DEFAULT_TEMPERATURE;
    public static int topP = ChatGPTConsant.DEFAULT_TOP_PROBABILITY;
    public static int frequencyPenalty = ChatGPTConsant.DEFAULT_FREQUENCY_PENALTY;
    public static int presencePenalty = ChatGPTConsant.DEFAULT_PRESENCE_PENALTY;
    public static String[] apiKeys;
    public static String proxy = ProjectConstant.DEFAULT_PROXY;

    public static Path classMapPath;

    public static Path testClassMapPath;

    public static ReentrantLock lock = new ReentrantLock();

    public static void setSession(MavenSession session) {
        Config.session = session;
    }

    public static void setProject(MavenProject project) {
        Config.project = project;
    }

    public static void setDependencyGraphBuilder(DependencyGraphBuilder dependencyGraphBuilder) {
        Config.dependencyGraphBuilder = dependencyGraphBuilder;
    }

    public static void setApiKeys(String[] apiKeys) {
        Config.apiKeys = apiKeys;
    }

    public static void setStopWhenSuccess(boolean stopWhenSuccess) {
        Config.stopWhenSuccess = stopWhenSuccess;
    }

    public static void setEnableMultithreading(boolean enableMultithreading) {
        Config.enableMultithreading = enableMultithreading;
    }

    public static void setMaxThreads(int maxThreads) {
        if (maxThreads == 0) {
            Config.maxThreads = Runtime.getRuntime().availableProcessors() * 5;
        } else {
            Config.maxThreads = maxThreads;
        }
    }

    public static void setTestNumber(int testNumber) {
        Config.testNumber = testNumber;
    }

    public static void setTimeOut(int timeOut) {
        Config.timeOut = timeOut;
    }

    public static void setProcessNumber(int processNumber) {
        Config.processNumber = processNumber;
    }

    public static void setResultDir(String resultDir) {
        Config.resultDir = resultDir;
    }

    public static void setProjectDir(String projectDir) {
        Config.projectDir = projectDir;
    }

    public static void setMaxRounds(int maxRounds) {
        Config.maxRounds = maxRounds;
    }

    public static void setMaxPromptTokens(int maxPromptTokens) {
        Config.maxPromptTokens = maxPromptTokens;
    }

    public static void setMinErrorTokens(int minErrorTokens) {
        Config.minErrorTokens = minErrorTokens;
    }

    public static void setModel(String model) {
        Config.model = model;
    }

    public static void setTemperature(Double temperature) {
        Config.temperature = temperature;
    }

    public static void setTopP(int topP) {
        Config.topP = topP;
    }

    public static void setFrequencyPenalty(int frequencyPenalty) {
        Config.frequencyPenalty = frequencyPenalty;
    }

    public static void setPresencePenalty(int presencePenalty) {
        Config.presencePenalty = presencePenalty;
    }

    public static void setProxy(String proxy){Config.proxy=proxy;}

    public static void setClassMapPath(Path classMapPath) {
        Config.classMapPath = classMapPath;
    }

    public static void setTestClassMapPath(Path testClassMapPath) {
        Config.testClassMapPath = testClassMapPath;
    }

    public static String getRandomKey() {
        Random rand = new Random();
        if (apiKeys.length == 0) {
            throw new RuntimeException("apiKeys is null!");
        }
        String apiKey = apiKeys[rand.nextInt(apiKeys.length)];
        return apiKey;
    }
}
