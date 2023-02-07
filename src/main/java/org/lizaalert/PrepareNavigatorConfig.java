package org.lizaalert;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class PrepareNavigatorConfig {
    public final String operationName;
    public final String disk;
    public final String workingDir;
    public final boolean showHelp;

    public static final String DEFAULT_PARAM = "DEFAULT";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public PrepareNavigatorConfig(String[] argsArray) {
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

    public PrepareNavigatorConfig(String operationName, String disk, String workingDir) {
        if (!isNameValid(operationName)) {
            throw new IllegalArgumentException("-name is required, but passed value does not match pattern yyyy-MM-dd_Place");
        }
        this.operationName = operationName;
        if (disk.trim().isEmpty()) {
            this.disk = DEFAULT_PARAM;
        } else {
            this.disk = disk;
        }
        if (workingDir.trim().isEmpty() || workingDir.trim().equals(".")) {
            this.workingDir = DEFAULT_PARAM;
        } else {
            this.workingDir = workingDir;
        }
        this.showHelp = false;
    }

    public boolean isWorkingDirDefault() {
        return workingDir.equals(DEFAULT_PARAM);
    }

    public boolean isDiskDefault() {
        return disk.equals(DEFAULT_PARAM);
    }

    public String resolveWorkingDir() {
        File dir;
        dir = new File(workingDir);
        if (workingDir.equals(DEFAULT_PARAM)) {
            dir = new File("./" + operationName);
            System.out.println("Default working dir was resolved to " + dir.getAbsolutePath());
        } else if (!dir.isDirectory()) {
            dir = new File("./" + operationName);
            System.out.println("Specified working dir is not directory '" + workingDir + "'. Falling back to default " + dir.getAbsolutePath());
        } else {
            System.out.println("Working directory is " + dir.getAbsolutePath());
        }

        return dir.getAbsolutePath();
    }

    public String resolveDisk() {
        File dir;
        dir = new File(disk);
        if (!dir.isDirectory()) {
            System.out.println("Specified disk is not directory '" + disk + "'.");
        }
        if (disk.equals(DEFAULT_PARAM) || !dir.isDirectory()) {
            File[] paths = File.listRoots();
            System.out.println("Resolving default disk");
            System.out.println("Found " + paths.length + " root drives ");
            for (File path : paths) {
                String[] list = path.list();
                if (list != null) {
                    List<String> contents = Arrays.asList(list);
                    if (contents.contains("Garmin")) {
                        dir = path;

                    }
                }
            }
        }
        return dir.getAbsolutePath();
    }

    public static boolean directoryIsValid() {
        return false;
    }
}
