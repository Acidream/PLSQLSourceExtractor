package org.home.model.MaskGroup;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 2017-07-12.
 */
public class MaskGroup {

    private String name;

    private String outFolder;


    private List<String> DBObjects;

    @XmlTransient
    private
    List<Mask> objMasks = new ArrayList<>();

    public MaskGroup() {
    }

    public MaskGroup(String name, String outFolder, List<String> DBObjects) {
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

    public List<Mask> getObjMasks() {
        return objMasks;
    }


    @XmlList//on init through jaxb this method not called
    public void setDBObjects(List<String> DBObjects) {
        this.DBObjects = DBObjects;
        for (String dbObject : DBObjects) {
            objMasks.add(Mask.fromOwnerDotMask(dbObject));
        }
    }

    public List<String> getDBObjects() {
        return DBObjects;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (String dbObject : DBObjects) {
            objMasks.add(Mask.fromOwnerDotMask(dbObject));
        }

    }

    public void saveObjs() throws IOException, SQLException {
        for (Mask objMask : objMasks) {
            objMask.loadObjectListFromDB();
            objMask.loadSources();
            objMask.saveSources(outFolder);
        }


    }


}

