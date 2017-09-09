package org.home.settings;

import java.util.Iterator;

/**
 * Created by oleg on 2017-09-09.
 */
public class StartupSettings {

    boolean noconfig = false;
    boolean addTypeDirectoryOnSave = false;

    public static StartupSettings instance;

    public static void initFromArgs(String[] args) {
        instance = new StartupSettings();
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-NOCONF")) instance.noconfig = true;
            else if (arg.equalsIgnoreCase("-ADDTYPEDIR")) instance.addTypeDirectoryOnSave = true;
        }
    }


    public boolean isNoConfig() {
        return noconfig;
    }

    public boolean isAddTypeDirectoryOnSave() {
        return addTypeDirectoryOnSave;
    }
}
