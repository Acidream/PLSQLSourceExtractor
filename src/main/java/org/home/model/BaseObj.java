package org.home.model;

import org.home.extractor.OraDbaObj;
import org.home.settings.StartupSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 2017-09-09.
 */
public abstract class BaseObj {

    String owner;
    String name;
    String sourceCode;
    String type;

    public String getName() {
        return name;
    }

    public abstract String getSourceCode();

    public String getType() {
        return type;
    }

    public void saveToFile(String folder) throws IOException {
        Path dir = Paths.get(folder, StartupSettings.instance.isAddTypeDirectoryOnSave() ? getType() : "");
        Files.createDirectories(dir);

        Path path = Paths.get(dir.normalize().toString(), getName() + ".sql");
        Files.write(path, getSourceCode().getBytes());
    }

    List<BaseObj> extractList() {
        ArrayList<BaseObj> res = new ArrayList<>();


        return res;
    }

    public static BaseObj fromOraDbaObj(OraDbaObj odo) {
        if (odo.getType().equalsIgnoreCase("PACKAGE")) return new PackageObj(odo);
        else return new Obj(odo);
    }

    public String getPrefix() {
        return "CREATE OR REPLACE ";
    }

    public abstract void extractSources() throws SQLException;


}
