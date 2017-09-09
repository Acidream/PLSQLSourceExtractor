package org.home.settings;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 2017-07-12.
 */
public class ObjGroup {

    private String name;

    private String outFolder;


    List<String> DBObjects;

    @XmlTransient
    List<ObjOwnerAndMask> objOwnerAndMasks = new ArrayList<>();

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

    public List<ObjOwnerAndMask> getObjOwnerAndMasks() {
        return objOwnerAndMasks;
    }

    @XmlList//on init through jaxb this method not called
    public void setDBObjects(List<String> DBObjects) {
        this.DBObjects = DBObjects;
        for (String dbObject : DBObjects) {
            objOwnerAndMasks.add(ObjOwnerAndMask.fromOwnerDotMask(dbObject));
        }


    }

    public List<String> getDBObjects() {
        return DBObjects;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (String dbObject : DBObjects) {
            objOwnerAndMasks.add(ObjOwnerAndMask.fromOwnerDotMask(dbObject));
        }

    }
}

