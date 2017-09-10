package org.home;


import org.home.model.MaskGroup.MaskGroupFileAgg;
import org.home.settings.DBConnSettings;
import org.home.settings.StartupSettings;

import java.io.IOException;
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
        DBConnSettings.init(connSettingsFile);

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
                ogs.saveObjs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveNoConfig(List<String> argsTail) {
        if (argsTail.size() > 1) throw new RuntimeException("Wrong number of parameters given.");
        try {
            MaskGroupFileAgg ogs = MaskGroupFileAgg.fromObjNames(Arrays.stream(argsTail.get(0).split(",")).map(s -> s.trim()).collect(Collectors.toList()));
            ogs.saveObjs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static void generateExamples() {
        MaskGroupFileAgg.generateExample();
        DBConnSettings.generateExample();
    }


}
