package org.home.model.MaskGroup;

import org.home.extractor.IExtractor;
import org.home.extractor.NewExtractor;
import org.home.extractor.OldExtractor;
import org.home.model.BaseObj;
import org.home.settings.StartupSettings;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashSet;
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
        for (String dbObject : DBObjects) {
            objMasks.add(Mask.fromOwnerDotMask(dbObject));
        }
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

    public List<Path> loadAndSaveObjs() throws IOException, SQLException {
        List<Path> paths = new ArrayList<>();
        HashSet<BaseObj> all = new HashSet<>();
        HashSet<BaseObj> exclude = new HashSet<>();

        for (Mask objMask : objMasks) {
            objMask.loadObjectListFromDB();
            if (!objMask.isExclude())
                all.addAll(objMask.getBaseObjs());
            else
                exclude.addAll(objMask.getBaseObjs());
        }
        all.removeAll(exclude);
        IExtractor ex = StartupSettings.instance.isUseOldMethod() ? new OldExtractor() : new NewExtractor();
        ex.init();
        List<BaseObj> withSources = ex.extract(all);
        for (BaseObj baseObj : withSources) {
            paths.add(baseObj.saveToFile(outFolder));
        }
        return paths;
    }

    public void loadObjList() throws IOException, SQLException {
        for (Mask objMask : objMasks) {
            objMask.loadObjectListFromDB();
        }
    }


    public void saveSources() throws IOException, SQLException {
        for (Mask objMask : objMasks) {
            objMask.saveSources(outFolder);
        }
    }

    public List<BaseObj> getBaseObjs() {
        List<BaseObj> res = new ArrayList<>();
        for (Mask objMask : objMasks) {
            res.addAll(objMask.getBaseObjs());
        }
        return res;
    }


}

