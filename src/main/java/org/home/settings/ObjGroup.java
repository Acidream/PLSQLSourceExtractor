package org.home.settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;
import java.util.List;

/**
 * Created by oleg on 2017-07-12.
 */
public class ObjGroup {

    String name;
    String outFolder;

    List<String> DBObjects;

    public ObjGroup() {
    }

    public ObjGroup(String name, String outFolder, List<String> DBObjects) {
        this.name = name;
        this.outFolder = outFolder;
        this.DBObjects = DBObjects;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public String getOutFolder() {
        return outFolder;
    }

    public void setOutFolder(String outFolder) {
        this.outFolder = outFolder;
    }

    public List<String> getDBObjects() {
        return DBObjects;
    }

    @XmlList
    public void setDBObjects(List<String> DBObjects) {
        this.DBObjects = DBObjects;
    }
}

