package org.home.model;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.extractor.OraDbaObj;
import org.home.extractor.OraDbaSource;
import org.home.settings.DBConnSettings;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-09-09.
 */
public class Obj extends BaseObj {

    Obj(OraDbaObj odo) {
        this.name = odo.getName();
        this.type = odo.getType();
        this.owner = odo.getOwner();
    }


    @Override
    public String getSourceCode() {
        return getPrefix() + sourceCode + "\n/";
    }

    public void extractSources() throws SQLException {
        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
        ResultSetHandler<List<OraDbaSource>> h = new BeanListHandler<OraDbaSource>(OraDbaSource.class);
        List<OraDbaSource> oraDbaSources = run.query("select owner, name, type, line, text from dba_source where owner=? and name = ? order by type,line asc", h, owner, name);
        sourceCode = String.join("", oraDbaSources.stream().map(s -> s.getText()).collect(Collectors.toList()));
    }
}
