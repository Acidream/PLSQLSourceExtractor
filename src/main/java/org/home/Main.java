package org.home;


import org.home.model.MaskGroup.MaskGroupFileAgg;
import org.home.settings.DBConnSettings;
import org.home.settings.ShowAndExitException;
import org.home.settings.StartupSettings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.SQLException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-08-05.
 */
class Main {
    public static void main(String[] args) throws SQLException {

        StartupSettings.initFromArgs(args);
        if (StartupSettings.instance.isGenExamples()) {
            generateExamples();
            return;
        }

        List<String> argsTail = Arrays.stream(args).filter(a -> !a.startsWith("-") && a.trim().length() != 0).collect(Collectors.toList());

        if (argsTail.size() == 0)
            throw new RuntimeException("No input parameters given. First parameter DBConn.xml other parameters ObjectGroups.xml or comma separated object names in -noconf mode");
        if (argsTail.size() == 1)
            throw new RuntimeException("Only one input parameter given. First parameter DBConn.xml other parameters ObjectGroups.xml or comma separated object names in -noconf mode");

        String connSettingsFile = argsTail.get(0);
        try {
            DBConnSettings.init(connSettingsFile);
        } catch (ShowAndExitException e) {
            e.printMessage();
        }

        argsTail = argsTail.stream().filter(a -> !a.equals(connSettingsFile)).collect(Collectors.toList());
        if (StartupSettings.instance.isNoConfig()) {
            saveNoConfig(argsTail);
        } else {
            saveUsingConfigFiles(argsTail);
        }
    }

    private static void saveUsingConfigFiles(List<String> maskGroupFiles) {
        try {
            for (String maskGroupsFile : maskGroupFiles) {
                System.out.println("Processing " + maskGroupsFile);
                MaskGroupFileAgg ogs = MaskGroupFileAgg.get(maskGroupsFile);
                List<Path> paths = ogs.loadAndSaveObjs();

                if (StartupSettings.instance.isBeautifyWithDeveloper()) beautify(paths);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ShowAndExitException e) {
            e.printMessage();
        }
    }

    private static void saveNoConfig(List<String> argsTail) {
        if (argsTail.size() > 1) throw new RuntimeException("Wrong number of parameters given.");
        try {
            MaskGroupFileAgg ogs = MaskGroupFileAgg.fromObjNames(Arrays.stream(argsTail.get(0).toUpperCase().split(",")).map(s -> s.trim()).collect(Collectors.toList()));
            List<Path> paths = ogs.loadAndSaveObjs();
            if (StartupSettings.instance.isBeautifyWithDeveloper()) beautify(paths);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void generateExamples() {
        try {
            MaskGroupFileAgg.generateExample();
            DBConnSettings.generateExample();
        } catch (ShowAndExitException e) {
            e.printMessage();
        }
    }


    private static void beautify(List<Path> paths) throws IOException {
        try {


            Path filePath = Paths.get("beautify.sql");
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write("SET BEAUTIFIERRULES \"C:/tools/format_avangard.br\""+ "\n");

                for (Path path : paths) {
                    writer.write("beautify " + path.toAbsolutePath().toString() + "\n");
                }
                writer.write("exit application");
            }


            Process p = Runtime.getRuntime().exec(Paths.get("").toAbsolutePath().toString() + "//start_beautifier.bat", null, new File(Paths.get("").toAbsolutePath().toString()));
            p.waitFor();


            for (Path path : paths) {
                String newNameStr = path.toAbsolutePath().toString();
                newNameStr = newNameStr.substring(0, newNameStr.lastIndexOf(".")) + ".sql";

                Files.move(path, Paths.get(newNameStr));
            }

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
