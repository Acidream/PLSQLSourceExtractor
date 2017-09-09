package org.home;

import org.home.extractor.SrcExtractor;
import org.home.settings.DBConnSettings;
import org.home.settings.ObjGroupSettings;
import org.home.settings.StartupSettings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-08-05.
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        ObjGroupSettings.generateExample();
        DBConnSettings.generateExample();

        StartupSettings.initFromArgs(args);
        List<String> argsTail = Arrays.stream(args).filter(a -> !a.startsWith("-") && a.trim().length() != 0).collect(Collectors.toList());


        if (argsTail.size() == 0)
            throw new RuntimeException("No input parameters given. First parameter DBConn.xml next parameters ObjectGroups.xml");
        if (argsTail.size() == 1)
            throw new RuntimeException("Only one input parameter given. First parameter DBConn.xml next parameters ObjectGroups.xml");

        String connSettingsFile = argsTail.get(0);
        DBConnSettings.init(connSettingsFile);


        argsTail = argsTail.stream().filter(a -> !a.equals(connSettingsFile)).collect(Collectors.toList());

        if (StartupSettings.instance.isNoConfig()) {
            if (argsTail.size() > 1) throw new RuntimeException("Wrong number of parameters given.");
            ObjGroupSettings ogs = ObjGroupSettings.fromObjNames(Arrays.stream(argsTail.get(0).split(",")).map(s -> s.trim()).collect(Collectors.toList()));
            SrcExtractor se = new SrcExtractor(ogs.getGroups());
            try {
                se.extract();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            for (String objSettingsFile : argsTail) {
                System.out.println("Processing " + objSettingsFile);
                ObjGroupSettings ogs = ObjGroupSettings.get(objSettingsFile);
                SrcExtractor se = new SrcExtractor(ogs.getGroups());
                try {
                    se.extract();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
