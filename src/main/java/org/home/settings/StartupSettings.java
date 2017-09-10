package org.home.settings;

/**
 * Created by oleg on 2017-09-09.
 */
public class StartupSettings {

    private boolean noConfig = false;
    private boolean addTypeDirectoryOnSave = false;
    private boolean useOldMethod = false;
    private boolean genExamples = false;

    public static StartupSettings instance;

    public static void initFromArgs(String[] args) {
        instance = new StartupSettings();
        for (String arg : args) {
            if (!arg.trim().startsWith("-")) continue;
            if (arg.equalsIgnoreCase("-NOCONF")) instance.noConfig = true;
            else if (arg.equalsIgnoreCase("-ADDTYPEDIR")) instance.addTypeDirectoryOnSave = true;
            else if (arg.equalsIgnoreCase("-USEOLDMETHOD")) instance.useOldMethod = true;
            else if (arg.equalsIgnoreCase("-GENEXAMPLES")) instance.genExamples = true;
            else throw new RuntimeException("Not supported option " + arg);
        }
    }


    public boolean isNoConfig() {
        return noConfig;
    }

    public boolean isAddTypeDirectoryOnSave() {
        return addTypeDirectoryOnSave;
    }

    public boolean isUseOldMethod() {
        return useOldMethod;
    }

    public boolean isGenExamples() {
        return genExamples;
    }
}
