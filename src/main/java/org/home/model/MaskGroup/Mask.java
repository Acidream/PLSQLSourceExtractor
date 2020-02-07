package org.home.model.MaskGroup;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.extractor.IExtractor;
import org.home.extractor.NewExtractor;
import org.home.extractor.OldExtractor;
import org.home.extractor.OraDbaObj;
import org.home.model.BaseObj;
import org.home.settings.DBConnSettings;
import org.home.settings.StartupSettings;

import java.io.IOException;
import java.nio.file.Path;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-09-09.
 */
public class Mask {
    private String owner;
    private String mask;
    private List<BaseObj> baseObjs;
    private boolean isExclude=false;

    static Mask fromOwnerDotMask(String ownerdotmask) {
        Mask res = new Mask();
        res.isExclude=ownerdotmask.startsWith("!");
        if (res.isExclude)
            ownerdotmask=ownerdotmask.substring(1);
        res.setOwner(ownerdotmask.substring(0, ownerdotmask.indexOf('.')).toUpperCase());
        res.setMask(ownerdotmask.substring(ownerdotmask.indexOf('.') + 1).toUpperCase());
        return res;
    }

    private String getOwner() {
        return owner;
    }

    private void setOwner(String owner) {
        this.owner = owner;
    }

    private String getMask() {
        return mask;
    }

    private void setMask(String mask) {
        this.mask = mask;
    }

    public boolean isExclude() {
        return isExclude;
    }

    public void setExclude(boolean exclude) {
        isExclude = exclude;
    }

    void loadObjectListFromDB() throws SQLException {
        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());

        ResultSetHandler<List<OraDbaObj>> h = new BeanListHandler<>(OraDbaObj.class);
        List<OraDbaObj> oraDbaObjs;
        if (!StartupSettings.instance.isUseOnlyDBASource())
            oraDbaObjs = run.query("SELECT distinct owner, type,name name FROM DBA_source where type in ('PROCEDURE','FUNCTION','PACKAGE','TABLE','VIEW') and  owner=? and name like ? ESCAPE '\\' and name is not null", h, getOwner(), getMask());
        else
            oraDbaObjs = run.query("SELECT distinct owner,object_type type,object_name name FROM DBA_OBJECTS where object_type in ('PROCEDURE','FUNCTION','PACKAGE','TABLE','VIEW') and  owner=? and object_name like ? ESCAPE '\\' and object_name is not null", h, getOwner(), getMask());
        baseObjs = oraDbaObjs.stream().map(oraDbaObj -> new BaseObj(oraDbaObj.getOwner(), oraDbaObj.getName(), oraDbaObj.getType())).collect(Collectors.toList());
    }

    void loadSources() throws SQLException {
        IExtractor ex = StartupSettings.instance.isUseOldMethod() ? new OldExtractor() : new NewExtractor();
        ex.init();
        baseObjs = ex.extract(baseObjs);
    }

    public List<BaseObj> getBaseObjs() {
        return baseObjs;
    }

    List<Path> saveSources(String folder) throws IOException {
        List<Path>  paths=new ArrayList<>();
        for (BaseObj baseObj : baseObjs) {
            paths.add( baseObj.saveToFile(folder));
        }
        return paths;
    }
}
