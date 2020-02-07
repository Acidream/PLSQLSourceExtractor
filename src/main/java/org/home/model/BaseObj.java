package org.home.model;

import org.home.settings.StartupSettings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.home.settings.Utils.LINE_SEPARATOR;

/**
 * Created by oleg on 2017-09-09.
 */
public class BaseObj implements Comparable {

    static String PREFIX = "CREATE OR REPLACE ";
    private String owner;
    private String name;
    private String type;
    private String sourceCode;

    public BaseObj(String owner, String name, String type) {
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    public BaseObj(String owner, String name, String type, String sourceCode) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.sourceCode = sourceCode;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSuperType() {
        if (type.equalsIgnoreCase("PACKAGE BODY")) return "PACKAGE";
        else return type;
    }

    public String getOwner() {
        return owner;
    }

    public BaseObj setSourceCode(String headerSourceCode, String bodySourceCode) {
        this.sourceCode = PREFIX + headerSourceCode + LINE_SEPARATOR+"/"+LINE_SEPARATOR;
        if (bodySourceCode != null) this.sourceCode += LINE_SEPARATOR + PREFIX + bodySourceCode + LINE_SEPARATOR+"/"+LINE_SEPARATOR;
        return this;
    }

    public Path saveToFile(String folder) throws IOException {
        Path dir = Paths.get(folder, StartupSettings.instance.isAddTypeDirectoryOnSave() ? getType() : "");
        Files.createDirectories(dir);
        String ext=StartupSettings.instance.isUsePlSqlDeveloperExtensions()?getSqlDeveloperExtension():"sql";
        Path path = Paths.get(dir.normalize().toString(), getName() + "."+ext);
        String src = getSourceCode();
        System.out.print("File " + path + " ");
        if (StartupSettings.instance.isUpdateAllFiles() || !Files.exists(path) || !(new String(Files.readAllBytes(path), StartupSettings.instance.getCharset())).equals(src)) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StartupSettings.instance.getCharset())) {
                writer.write(src);
            }
            //           Files.write(path, src.getBytes(), Charset.forName("UTF-8").newEncoder());
            System.out.println("OK");
        } else {
            System.out.println("No changes");
        }
        return path;
    }



    public boolean isTable() {
        return type.equalsIgnoreCase("TABLE");
    }

    public boolean isPackage() {
        return type.equalsIgnoreCase("PACKAGE");
    }

    public boolean isFunction() {
        return type.equalsIgnoreCase("FUNCTION");
    }

    public boolean isProcedure() {
        return type.equalsIgnoreCase("PROCEDURE");
    }

    private String getSqlDeveloperExtension() {
        if (isPackage()) return "pck";
        if (isFunction()) return "fnc";
        if (isProcedure()) return "prc";
        return "sql";
    }


    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseObj baseObj = (BaseObj) o;
        if (!owner.equals(baseObj.owner)) return false;
        if (!name.equals(baseObj.name)) return false;
        if (!type.equals(baseObj.type)) return false;
        return sourceCode != null ? sourceCode.equals(baseObj.sourceCode) : baseObj.sourceCode == null;
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (sourceCode != null ? sourceCode.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return -1;
        if (o == null || getClass() != o.getClass()) return -1;
        BaseObj baseObj = (BaseObj) o;
        return (owner + "." + name + "." + type).compareTo((baseObj.owner + "." + baseObj.name + "." + baseObj.type));
    }
}
