package org.lizaalert;

import java.io.File;

public class ValidConfig {
    public final String operationName;
    public final File disk;
    public final File workingDir;

    public ValidConfig(String operationName, File navigatorRoot, File workingDir) {
        if (InitialConfig.isNameValid(operationName)) {
            throw new IllegalArgumentException("Operation name was invalid " + operationName);
        }
        this.operationName = operationName;
        File gpxDirectory = new File(navigatorRoot.getAbsolutePath() + InitialConfig.gpxSubdirectory);
        if (!gpxDirectory.exists()) {
            throw new IllegalArgumentException("Navigator disk '" + navigatorRoot.getAbsolutePath() + "' does not contain gpx directory");
        }
        this.disk = navigatorRoot;
        if (!workingDir.exists()) {
            throw new IllegalArgumentException("Working directory '" + workingDir.getAbsolutePath() + "' does not exist");
        }
        this.workingDir = workingDir;
    }


}
