package org.home.model;

import org.home.settings.StartupSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by oleg on 2017-09-09.
 */
public class BaseObj implements Comparable {

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

    static String PREFIX = "CREATE OR REPLACE ";

    public BaseObj setSourceCode(String headerSourceCode, String bodySourceCode) {
        this.sourceCode = PREFIX + headerSourceCode + "\n/\n";
        if (bodySourceCode != null) this.sourceCode += "\n" + PREFIX + bodySourceCode + "\n/\n";
        return this;
    }

    public void saveToFile(String folder) throws IOException {
        Path dir = Paths.get(folder, StartupSettings.instance.isAddTypeDirectoryOnSave() ? getType() : "");
        Files.createDirectories(dir);
        Path path = Paths.get(dir.normalize().toString(), getName() + ".sql");
        Files.write(path, getSourceCode().getBytes());
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
