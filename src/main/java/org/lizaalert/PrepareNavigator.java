package org.lizaalert;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.lizaalert.ValidConfig.MAPS_URL;

public class PrepareNavigator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        InitialConfig initialConfig = new InitialConfig(args);
        if (initialConfig.showHelp) {
            showHelp();
        } else {
            ValidConfig validConfig = new ValidConfig(
                    initialConfig.operationName,
                    initialConfig.resolveDisk(),
                    initialConfig.resolveWorkingDir()
            );
            if (!validConfig.get4GarminLocalZipname().exists()) {
                downloadPackage(validConfig);
            } else {
                File file = validConfig.get4GarminLocalZipname();
                LocalDateTime lastModified = timestampToLocalDateTime(file.lastModified());
                System.out.println("Package already downloaded " + file.getAbsolutePath() + " at " + formatter.format(lastModified));
            }
            if (isCurrentPackageInvalid(validConfig)) {
                System.out.println("Package is not valid. Cleaning up.");
                cleanup(validConfig);
                unzipPackage(validConfig);
                System.out.println("Validating after cleanup.");
                if (isCurrentPackageInvalid(validConfig)) {
                    System.out.println("Cleanup didn't help. Archive is invalid.");
                } else {
                    System.out.println("Package is valid. Loading it to navigator");
                    resetSettings();
                    cleanup();
                    deployPackage();
                }
            } else {
                System.out.println("Package is valid. Loading it to navigator");
                resetSettings();
                cleanup();
                deployPackage();
            }

        }
    }

    public static LocalDateTime timestampToLocalDateTime(long unixTimestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(unixTimestamp),
                TimeZone.getDefault().toZoneId()
        );
    }

    public static String resolveDisk() {
        throw new NotImplementedException();
    }

    public static void showHelp() {
        System.out.println("Utility to prepare connected navigator. Downloads map package from " + MAPS_URL + " and deploys it to connected navigator.");
        System.out.println("-name\tName of the search with date. Required. Format: 'YYYY-MM-DD_Place' Used to download map data.");
        System.out.println("-disk\tName of the disk. Can be letter or arbitrary path. It should contain Garmin subdirectory. By default scans all disks of filesystem alphabetically and chooses first valid.");
        System.out.println("-workdir\tName of working directory. By default it is subdirectory named after search.");
    }

    public static boolean isCurrentPackageInvalid(ValidConfig config) {
        File birdsEyeMap = new File(config.get4GarminLocalDirectory() + config.getBirdsEyeFilename());
        if (birdsEyeMap.exists()) {
            System.out.println("Package contain birdsEye satellite map");
        } else {
            System.out.println("birdsEye satellite map is missing");
        }
        File ggc16Map = new File(config.get4GarminLocalDirectory() + config.getGGC16Filename());
        File otm17Map = new File(config.get4GarminLocalDirectory() + config.getOTM17Filename());
        if (ggc16Map.exists()) {
            System.out.println("Package contain GGC_z16 topo map");
        }
        if (otm17Map.exists()) {
            System.out.println("Package contain OTM_z17 topo map");
        }
        if (!ggc16Map.exists() && !otm17Map.exists()){
            System.out.println("topo maps are missing");
        }
        if (ggc16Map.exists() && otm17Map.exists()) {
            System.out.println("WARNING: Multiple topo maps exists");
        }

        File grid500 = new File(config.get4GarminLocalDirectory() + config.getGrid500Filename());
        File grid200 = new File(config.get4GarminLocalDirectory() + config.getGrid200Filename());
        if (grid500.exists()) {
            System.out.println("Package contain grid 500 meters");
        }
        if (grid200.exists()) {
            System.out.println("Package contain grid 200 meters");
        }
        if (!grid200.exists() && !grid500.exists()) {
            System.out.println("grid file is missing");
        }
        if (grid200.exists() && grid500.exists()) {
            System.out.println("WARNING: Package contains multiple grids");
        }

        boolean topoMapExists = ggc16Map.exists() || otm17Map.exists();
        boolean gridExists = grid500.exists() || grid200.exists();
        return !topoMapExists || !birdsEyeMap.exists() || !gridExists;
    }

    public static void cleanup(ValidConfig config) {
        File unpackageTarget = config.get4GarminLocalDirectory();
        if (unpackageTarget.exists()) {
            System.out.println("Unpackaging target exists " + unpackageTarget.getAbsolutePath());
            if (deleteDirectory(unpackageTarget)) {
                System.out.println("Directory was successfully deleted " + unpackageTarget.getAbsolutePath());
            } else {
                System.out.println("Failed to delete directory " + unpackageTarget.getAbsolutePath());
            }
        }
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        System.out.println("Delete " + directoryToBeDeleted.getAbsolutePath());
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private static void unzipPackage(ValidConfig config) {
        byte[] buffer = new byte[1024];
        File zip = config.get4GarminLocalZipname();
        File dir = config.get4GarminLocalDirectory();
        System.out.println("Unzipping from " + zip.getAbsolutePath() + " to " + dir.getAbsolutePath());
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(dir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            System.out.println("Unzipping was successful");
        } catch (FileNotFoundException e) {
            System.out.println("Garmin zip package was not found " + zip.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void downloadPackage(ValidConfig config) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            File downloadTarget = config.get4GarminLocalZipname();
            System.out.println("Sending GET to " + config.get4GarminDownloadUrl());
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(config.get4GarminDownloadUrl()).build();
            File downloadedResult = httpClient.execute(httpGet, new FileDownloadResponseHandler(downloadTarget));
            System.out.println("Package was downloaded to " + downloadedResult.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void cleanup() {

    }

    public static void deployPackage() {

    }

    public static void resetSettings() {
        //?????????????????????? ???????????????? ??????????, ?????????????? ?? ??.??.
    }
}
