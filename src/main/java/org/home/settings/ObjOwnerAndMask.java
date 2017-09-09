package org.home.settings;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.extractor.OraDbaObj;
import org.home.model.BaseObj;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-09-09.
 */
public class ObjOwnerAndMask {
    String owner;
    String mask;

    public static ObjOwnerAndMask fromOwnerDotMask(String ownerdotmask) {
        ObjOwnerAndMask res = new ObjOwnerAndMask();
        res.owner = ownerdotmask.substring(0, ownerdotmask.indexOf('.'));
        res.mask = ownerdotmask.substring(ownerdotmask.indexOf('.') + 1);
        return res;


    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }


    public List<BaseObj> loadObjectListFromDB() throws SQLException {
        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
        ResultSetHandler<List<OraDbaObj>> h = new BeanListHandler<OraDbaObj>(OraDbaObj.class);
        List<OraDbaObj> oraDbaObjs = run.query("SELECT distinct owner,object_type type,object_name name FROM DBA_OBJECTS where object_type in ('PROCEDURE','FUNCTION','PACKAGE') and  owner=? and object_name like ? and object_name is not null", h, getOwner(), getMask());
        return oraDbaObjs.stream().map(oraDbaObj -> BaseObj.fromOraDbaObj(oraDbaObj)).collect(Collectors.toList());
    }

}
