package org.home;

import org.home.extractor.SrcExtractor;
import org.home.settings.DBConnSettings;
import org.home.settings.ObjGroupSettings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-08-05.
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        ObjGroupSettings.generateExample();
        DBConnSettings.generateExample();

        if (args.length == 0)
            throw new RuntimeException("No input parameters given. First parameter DBConn.xml next parameters ObjectGroups.xml");
        if (args.length == 1)
            throw new RuntimeException("Only one input parameter given. First parameter DBConn.xml next parameters ObjectGroups.xml");

        DBConnSettings.init(args[0]);

        if (args[1].equalsIgnoreCase("-NOCONF")) {

            ObjGroupSettings ogs = ObjGroupSettings.getOneGroup(Arrays.stream(args[2].split(",")).map(s -> s.trim()).collect(Collectors.toList()));
            SrcExtractor se = new SrcExtractor(ogs.getGroups());
            try {
                se.extract();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            for (int i = 1; i < args.length; i++) {
                System.out.println("Processing " + args[i]);
                ObjGroupSettings ogs = ObjGroupSettings.get(args[i]);
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
