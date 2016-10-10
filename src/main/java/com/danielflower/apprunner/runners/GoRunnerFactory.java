package com.danielflower.apprunner.runners;

import com.danielflower.apprunner.Config;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

import static com.danielflower.apprunner.FileSandbox.fullPath;

class GoRunnerFactory implements AppRunnerFactory {

    public static final Logger log = LoggerFactory.getLogger(GoRunnerFactory.class);
    private final CommandLineProvider goCommandProvider;
    private final String versionInfo;
    private final String goPath;

    public GoRunnerFactory(CommandLineProvider goCommandProvider, String versionInfo, String goPath) {
        this.goCommandProvider = goCommandProvider;
        this.versionInfo = versionInfo;
        this.goPath = goPath;
    }

    @Override
    public String id() {
        return "go";
    }

    @Override
    public String sampleProjectName() {
        return "golang";
    }

    @Override
    public String description() {
        return "Go executable built with go build";
    }

    @Override
    public String[] startCommands() {
        return GoRunner.startCommands;
    }

    @Override
    public AppRunner appRunner(File folder) {
        return new GoRunner(folder, goPath, goCommandProvider);
    }

    @Override
    public String versionInfo() {
        return versionInfo;
    }

    @Override
    public boolean canRun(File appDirectory) {
        boolean canRun = false;
        for (File currentFile : appDirectory.listFiles()) {
            if (FilenameUtils.isExtension(currentFile.getName(), "go")) {
                canRun = true;
            }
        }
        return canRun;
    }

    public static Optional<GoRunnerFactory> createIfAvailable(Config config) {
        CommandLineProvider goCmdProvider = config.goCommandProvider();
        Pair<Boolean, String> version = ProcessStarter.run(goCmdProvider.commandLine(config.env()).addArgument("version"));
        String goPath = config.get(Config.GOPATH, fullPath(config.getOrCreateDir(Config.DATA_DIR)) + File.separator + "go");
        File goPathDir = new File(goPath);
        goPathDir.mkdirs();
        if (version.getLeft()) {
            return Optional.of(new GoRunnerFactory(goCmdProvider, version.getRight(), goPath));
        }
        return Optional.empty();
    }
}
