package org.lizaalert;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PrepareNavigator {
    public static void main(String[] args) {
        PrepareNavigatorConfig initialConfig = new PrepareNavigatorConfig(args);
        if (initialConfig.showHelp) {
            showHelp();
        } else {
            String actualWorkingDir = initialConfig.resolveWorkingDir();
            String actualDisk;
            if (initialConfig.isDiskDefault()) {
                actualDisk = resolveDisk();
            } else {
                actualDisk = initialConfig.disk;
            }
            PrepareNavigatorConfig preparedConfig = new PrepareNavigatorConfig(initialConfig.operationName, actualDisk, actualWorkingDir);


            if (currentPackageIsValid()) {
                downloadPackage();
            }
            cleanup();
            deployPackage();
        }
    }

    public static String resolveDisk() {
        throw new NotImplementedException();
    }

    public static void showHelp() {
        System.out.println("Utility to prepare connected navigator. Downloads map package from maps.lizaalert.ru and deploys it to connected navigator.");
        System.out.println("-name\tName of the search with date. Required. Format: 'YYYY-MM-DD_Place' Used to download map data.");
        System.out.println("-disk\tName of the disk. Can be letter or arbitrary path. It should contain Garmin subdirectory. By default scans all disks of filesystem alphabetically and chooses first valid.");
        System.out.println("-workdir\tName of working directory. By default it is subdirectory named after search.");
    }

    public static boolean currentPackageIsValid() {
        return true;
    }

    public static void downloadPackage() {

    }

    public static void cleanup() {

    }

    public static void deployPackage() {

    }
}
