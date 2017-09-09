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
public class PackageObj extends BaseObj {


    String header;
    String body;

    public PackageObj(OraDbaObj odo) {
        name = odo.getName();
        this.owner = odo.getOwner();

    }


    @Override
    public String getSourceCode() {
        return getPrefix() + header + "/\n\n" + getPrefix() + body + "/\n";
    }

    @Override
    public String getType() {
        return "PACKAGE";
    }

    public void extractSources() throws SQLException {

        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
        ResultSetHandler<List<OraDbaSource>> h = new BeanListHandler<OraDbaSource>(OraDbaSource.class);
        List<OraDbaSource> oraDbaSources = run.query("select owner, name, type, line, text from dba_source where owner=? and name = ? order by type,line asc", h, owner, name);
        header = String.join("", oraDbaSources.stream().filter(s -> s.getType().equalsIgnoreCase("PACKAGE")).map(s -> s.getText()).collect(Collectors.toList()));
        body = String.join("", oraDbaSources.stream().filter(s -> s.getType().equalsIgnoreCase("PACKAGE BODY")).map(s -> s.getText()).collect(Collectors.toList()));

    }

}
