package org.home.model.MaskGroup;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.extractor.*;
import org.home.model.BaseObj;
import org.home.settings.DBConnSettings;
import org.home.settings.StartupSettings;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-09-09.
 */
public class Mask {
    private String owner;
    private String mask;
    private List<BaseObj> baseObjs;

    static Mask fromOwnerDotMask(String ownerdotmask) {
        Mask res = new Mask();
        res.setOwner(ownerdotmask.substring(0, ownerdotmask.indexOf('.')));
        res.setMask(ownerdotmask.substring(ownerdotmask.indexOf('.') + 1));
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


    void loadObjectListFromDB() throws SQLException {
        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
        ResultSetHandler<List<OraDbaObj>> h = new BeanListHandler<OraDbaObj>(OraDbaObj.class);
        List<OraDbaObj> oraDbaObjs = run.query("SELECT distinct owner,object_type type,object_name name FROM DBA_OBJECTS where object_type in ('PROCEDURE','FUNCTION','PACKAGE','TABLE','VIEW') and  owner=? and object_name like ? and object_name is not null", h, getOwner(), getMask());
        baseObjs = oraDbaObjs.stream().map(oraDbaObj -> new BaseObj(oraDbaObj.getOwner(), oraDbaObj.getName(), oraDbaObj.getType())).collect(Collectors.toList());
    }

    void loadSources() throws SQLException {
        IExtractor ex = StartupSettings.instance.isUseOldMethod() ? new OldExtractor() : new NewExtractor();
        ex.init();
        baseObjs = ex.extract(baseObjs);
    }


    void saveSources(String folder) throws IOException {
        for (BaseObj baseObj : baseObjs) {
            baseObj.saveToFile(folder);
        }
    }
}
