package org.lizaalert;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class InitialConfig {
    public final String operationName;
    public final String disk;
    public final String workingDir;
    public final boolean showHelp;

    public static final String DEFAULT_PARAM = "DEFAULT";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public InitialConfig(String[] argsArray) {
        List<String> args = Arrays.asList(argsArray);
        int index = args.indexOf("-help");
        if (index >= 0 || args.isEmpty()) {
            this.showHelp = true;
            this.operationName = DEFAULT_PARAM;
            this.disk = DEFAULT_PARAM;
            this.workingDir = DEFAULT_PARAM;
        } else {
            showHelp = false;
            index = args.indexOf("-name");
            if (index >= 0) {
                if (args.size() <= index + 1) {
                    throw new IllegalArgumentException("-name is required, but was passed with empty value.");
                } else {
                    String value = args.get(index + 1);
                    if (isNameValid(value)) {
                        this.operationName = value;
                    } else {
                        throw new IllegalArgumentException("-name is required, but passed value does not match pattern yyyy-MM-dd_Place");
                    }
                }
            } else {
                throw new IllegalArgumentException("-name is required, but was not specified.");
            }
            index = args.indexOf("-disk");
            if (index >= 0) {
                if (args.size() <= index + 1) {
                    System.out.println("-disk was empty. Set default.");
                    this.disk = DEFAULT_PARAM;
                } else {
                    this.disk = args.get(index + 1);
                }
            } else {
                System.out.println("-disk was not specified. Set default.");
                this.disk = DEFAULT_PARAM;
            }
            index = args.indexOf("-workdir");
            if (index >= 0) {
                if (args.size() <= index + 1) {
                    System.out.println("-workdir was empty. Set default.");
                    this.workingDir = DEFAULT_PARAM;
                } else if (args.get(index + 1).equals(".")) {
                    System.out.println("-workdir was 'here'. Set default.");
                    this.workingDir = DEFAULT_PARAM;
                } else {
                    this.workingDir = args.get(index + 1);
                }
            } else {
                this.workingDir = DEFAULT_PARAM;
            }
        }
        System.out.println(this.toString());
    }

    public static boolean isNameValid(String value) {
        int sep = value.indexOf("_");
        if (sep == DATE_FORMAT.length()) {
            String dateStr = value.substring(0, sep);
            String place = value.substring(sep);
            LocalDate date = LocalDate.parse(dateStr, format);
            return !place.contains("\\");
        } else return false;
    }

    public boolean isWorkingDirDefault() {
        return workingDir.equals(DEFAULT_PARAM);
    }

    public boolean isDiskDefault() {
        return disk.equals(DEFAULT_PARAM);
    }

    public File resolveWorkingDir() {
        File workingDir;
        workingDir = new File(this.workingDir);
        if (this.workingDir.equals(DEFAULT_PARAM)) {
            workingDir = new File("./" + operationName);
            System.out.println("Default working dir was resolved to " + workingDir.getAbsolutePath());
        } else if (!workingDir.isDirectory()) {
            workingDir = new File("./" + operationName);
            System.out.println("Specified working dir is not directory '" + this.workingDir + "'. Falling back to default " + workingDir.getAbsolutePath());
        } else {
            System.out.println("Working directory is " + workingDir.getAbsolutePath());
        }

        return workingDir;
    }

    public static final String gpxSubdirectory = "/Garmin/GPX/Current";

    public File resolveDisk() {
        File navigatorRoot;
        navigatorRoot = new File(disk);

        File gpxDir = new File(navigatorRoot + gpxSubdirectory);
        if (gpxDir.exists()) {
            System.out.println("Specified directory '" + navigatorRoot.getAbsolutePath() + "' does not contain gps directory '" + gpxSubdirectory + "'");
        }
        if (disk.equals(DEFAULT_PARAM) || !navigatorRoot.isDirectory()) {
            File[] paths = File.listRoots();
            System.out.println("Resolving default disk");
            System.out.println("Found " + paths.length + " root drives ");
            boolean found = false;
            for (File path : paths) {
                gpxDir = new File(path.getAbsolutePath() + gpxSubdirectory);
                if (gpxDir.exists()) {
                    System.out.println("Found gpx directory '" + gpxSubdirectory + "' in disk " + path.getAbsolutePath());
                    navigatorRoot = path;
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Can't find connected navigator");
            }
        }
        return navigatorRoot;
    }

    @Override
    public String toString() {
        return "PrepareNavigatorConfig{" +
                "operationName='" + operationName + '\'' +
                ", disk='" + disk + '\'' +
                ", workingDir='" + workingDir + '\'' +
                ", showHelp=" + showHelp +
                '}';
    }

    public static boolean directoryIsValid() {
        return false;
    }
}
