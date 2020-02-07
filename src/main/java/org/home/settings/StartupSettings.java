package org.home.settings;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by oleg on 2017-09-09.
 */
public class StartupSettings {

    static String charsetPrefix = "-CHARSET_";
    private boolean noConfig = false;
    private boolean addTypeDirectoryOnSave = false;
    private boolean useOldMethod = false;
    private boolean useOnlyDBASource = false;
    private boolean genExamples = false;
    private boolean updateAllFiles = false;
    private boolean usePlSqlDeveloperExtensions = false;
    private boolean beautifyWithDeveloper = false;


    private Charset charset = StandardCharsets.UTF_8;

    public static StartupSettings instance;

    public static void initFromArgs(String[] args) {
        instance = new StartupSettings();
        for (String arg : args) {
            if (!arg.trim().startsWith("-")) continue;
            if (arg.equalsIgnoreCase("-NOCONF")) instance.noConfig = true;
            else if (arg.equalsIgnoreCase("-ADDTYPEDIR")) instance.addTypeDirectoryOnSave = true;
            else if (arg.equalsIgnoreCase("-USEOLDMETHOD")) instance.useOldMethod = true;
            else if (arg.equalsIgnoreCase("-PLSQLDEVELOPEREXT")) instance.usePlSqlDeveloperExtensions = true;
            else if (arg.equalsIgnoreCase("-BEAUTIFY_WITH_DEVELOPER")) instance.beautifyWithDeveloper = true;
            else if (arg.equalsIgnoreCase("-USEONLYDBASOURCE")) {
                instance.useOldMethod = true;
                instance.useOnlyDBASource = true;
            } else if (arg.equalsIgnoreCase("-GENEXAMPLES")) instance.genExamples = true;
            else if (arg.toUpperCase().startsWith(charsetPrefix)) instance.initCharset(arg);
            else throw new RuntimeException("Not supported option " + arg);
        }
    }

    private void initCharset(String option) {
        option = option.substring(charsetPrefix.length());
        charset = Charset.forName(option);
        System.out.println(charset);
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

    public boolean isUseOnlyDBASource() {
        return useOnlyDBASource;
    }

    public Charset getCharset() {
        return charset;
    }


    public boolean isUpdateAllFiles() {
        return updateAllFiles;
    }

    public boolean isUsePlSqlDeveloperExtensions() {
        return usePlSqlDeveloperExtensions;
    }

    public boolean isBeautifyWithDeveloper() {
        return beautifyWithDeveloper;
    }
}
