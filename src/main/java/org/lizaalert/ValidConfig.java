package org.lizaalert;

import java.io.File;

public class ValidConfig {
    public final String operationName;
    public final File disk;
    public final File workingDir;
    public static final String MAPS_URL = "https://maps.lizaalert.ru/maps/";

    public ValidConfig(String operationName, File navigatorRoot, File workingDir) {
        if (!InitialConfig.isNameValid(operationName)) {
            throw new IllegalArgumentException("Operation name was invalid " + operationName);
        }
        this.operationName = operationName;
        File gpxDirectory = new File(navigatorRoot.getAbsolutePath() + InitialConfig.gpxSubdirectory);
        if (!gpxDirectory.exists()) {
            throw new IllegalArgumentException("Navigator disk '" + navigatorRoot.getAbsolutePath() + "' does not contain gpx directory " + gpxDirectory.getAbsolutePath());
        }
        this.disk = navigatorRoot;
        if (!workingDir.exists()) {
            throw new IllegalArgumentException("Working directory '" + workingDir.getAbsolutePath() + "' does not exist");
        }
        this.workingDir = workingDir;
    }

    public File get4GarminLocalZipname() {
        return new File(workingDir + "/4-Garmin.zip");
    }

    public String get4GarminDownloadUrl() {
        return MAPS_URL + operationName + "/4-Garmin.zip";
    }

    public File get4GarminLocalDirectory() {
        return new File(workingDir + "/4-Garmin");
    }

    public String getBirdsEyeFilename() {
        return "/Garmin/BirdsEye/" + operationName + "_Satell_z17.jnx";
    }

    public String getGGC16Filename() {
        return "/Garmin/CustomMaps/" + operationName + "_Topo_GGC_z16.kmz";
    }

    public String getOTM17Filename() {
        return "/Garmin/CustomMaps/" + operationName + "_Topo_OTM_z17.kmz";
    }

    public String getGrid500Filename() {
        return "/Garmin/GPX/" + operationName + "_500m.gpx";
    }

    public String getGrid200Filename() {
        return "/Garmin/GPX/" + operationName + "_200m.gpx";
    }
}
