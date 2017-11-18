package org.home.extractor;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.model.BaseObj;
import org.home.settings.DBConnSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-09-10.
 */
public class NewExtractor implements IExtractor {
    @Override
    public void init() {
    }

    private String getSQL(BaseObj b) {
        return "SELECT '" + b.getOwner() + "' owner,'" + b.getName() + "' name,'" + b.getType() + "' type, DBMS_METADATA.GET_DDL( '" + b.getType() + "','" + b.getName() + "','" + b.getOwner() + "') text FROM DUAL ";
    }

    @Override
    public List<BaseObj> extract(List<BaseObj> baseObjs) throws SQLException {
        int cnt = 0;
        List<BaseObj> res = new ArrayList<>();
        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
        Connection conn = run.getDataSource().getConnection();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'STORAGE',false) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'SEGMENT_ATTRIBUTES',false) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'PRETTY',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'SQLTERMINATOR',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'CONSTRAINTS',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'REF_CONSTRAINTS',true) }").execute();
        ResultSetHandler<OraDbaSource> h = new BeanHandler<OraDbaSource>(OraDbaSource.class);
        for (BaseObj baseObj : baseObjs) {
            String packsql = getSQL(baseObj);
            try {
                OraDbaSource s = run.query(conn, packsql, h);
                BaseObj bo = new BaseObj(s.getOwner(), s.getName(), s.getType(), s.getText());
                res.add(bo);
                // System.out.println(baseObj.getName() + " OK");
            } catch (Exception e) {
                System.out.println(baseObj.getName() + "EXTRACTION ERROR!");
            }
            cnt++;
        }
        return res;
    }
}
