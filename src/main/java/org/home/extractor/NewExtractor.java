package org.home.extractor;


import oracle.sql.CLOB;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.home.model.BaseObj;
import org.home.settings.DBConnSettings;
import org.home.settings.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 2017-09-10.
 */
public class NewExtractor implements IExtractor {
    QueryRunner run;
    Connection conn;

    @Override

    public void init() throws SQLException {

        run = new QueryRunner(DBConnSettings.getDataSource());
        conn = run.getDataSource().getConnection();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'STORAGE',false) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'SEGMENT_ATTRIBUTES',false) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'PRETTY',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'SQLTERMINATOR',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'CONSTRAINTS',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'REF_CONSTRAINTS',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'CONSTRAINTS_AS_ALTER',true) }").execute();
        conn.prepareCall("{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'FORCE',false) }").execute();
    }

    private String getSQL(BaseObj b) {
        return "SELECT '" + b.getOwner() + "' owner,'" + b.getName() + "' name,'" + b.getType() + "' type, DBMS_METADATA.GET_DDL( '" + b.getType() + "','" + b.getName() + "','" + b.getOwner() + "') text FROM DUAL ";
    }


    public String getDependentSrc(BaseObj baseObj) throws SQLException {
        if (baseObj.isTable()) return getDependentSrcForTable(baseObj.getName(), baseObj.getOwner());
        return "";


    }

    public String getDependentSrcForTable(String parentName, String owner) throws SQLException {
        // ScalarHandler<oracle.sql.CLOB> scalarHandler = new ScalarHandler<>();
        QueryRunner runner = new QueryRunner();
        String query = "select dbms_metadata.get_ddl('INDEX', INDEX_NAME,owner) as ddl_indexes\n" +
                "              from DBA_INDEXES\n" +
                "             where TABLE_NAME = '%s' and owner='%s'\n" +
                "               and INDEX_NAME not in\n" +
                "                   (select CONSTRAINT_NAME\n" +
                "                      from DBA_constraints\n" +
                "                     where TABLE_NAME = '%s' and owner='%s'\n" +
                "                       and CONSTRAINT_TYPE = 'P') \n" +
                "UNION ALL " +
                " SELECT dbms_metadata.get_dependent_ddl ('COMMENT', '%s', '%s')\n" +
                "                  FROM dual  \n" +
                "                 WHERE exists (select 1 from DBA_TAB_COMMENTS dtc  where   dtc.TABLE_NAME = '%s' AND dtc.owner = '%s' AND length(dtc.comments) > 0)\n" +
                "                   or \n" +
                "                   exists (select 1 from all_col_comments cc  where   cc.TABLE_NAME = '%s' AND cc.owner = '%s' AND length(cc.comments) > 0 ) " +
                " union all " +
                "SELECT dbms_metadata.get_dependent_ddl ('TRIGGER', '%s', '%s')\n" +
                "  FROM DBA_TRIGGERS mvc\n" +
                " WHERE mvc.TABLE_NAME = '%s' AND\n" +
                "   mvc.owner = '%s' AND\n" +
                "   rownum = 1";
        query = String.format(query, parentName, owner, parentName, owner, parentName, owner, parentName, owner, parentName, owner, parentName, owner, parentName, owner);
        List<oracle.sql.CLOB> res = runner.query(conn, query, new ColumnListHandler<oracle.sql.CLOB>(1));

        StringBuilder stb = new StringBuilder();
        if (res != null) {
            for (CLOB re : res) {
                if (!re.isEmptyLob()) stb.append(Utils.clobToString(re) + "\n");
            }
            return stb.toString();
        }

        return "";
    }


    @Override
    public List<BaseObj> extract(List<BaseObj> baseObjs) throws SQLException {
        int cnt = 0;
        List<BaseObj> res = new ArrayList<>();


        ResultSetHandler<OraDbaSource> h = new BeanHandler<OraDbaSource>(OraDbaSource.class);
        for (BaseObj baseObj : baseObjs) {
            String packsql = getSQL(baseObj);

            try {
                OraDbaSource s = run.query(conn, packsql, h);
                String src = s.getText() + "\n" + getDependentSrc(baseObj);
                BaseObj bo = new BaseObj(s.getOwner(), s.getName(), s.getType(), src);
                res.add(bo);
                // System.out.println(baseObj.getName() + " OK");
            } catch (Exception e) {
                System.out.println(baseObj.getName() + " EXTRACTION ERROR!");
                e.printStackTrace();
            }
            cnt++;
        }
        return res;
    }
}
