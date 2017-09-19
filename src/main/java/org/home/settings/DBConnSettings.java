package org.home.settings;

import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.SQLException;

/**
 * Created by oleg on 2017-07-12.
 */
@XmlRootElement
public class DBConnSettings {
    private static DBConnSettings instance;
    private String filename = "DBConnSettings.xml";
    private String connURL;
    private OracleDataSource ds;

    private DBConnSettings() {
    }

    public static DataSource getDataSource() {
        return instance.ds;
    }

    public static DBConnSettings init(String filename) throws ShowAndExitException {
        if (instance == null || !instance.filename.equals(filename)) {
            instance = Utils.unmarshal(filename, DBConnSettings.class);
            try {
                instance.filename = filename;
                instance.ds = new OracleDataSource();
                instance.ds.setURL(instance.connURL);
            } catch (SQLException e) {
                new ShowAndExitException("Oracle connection error" + e.getErrorCode() + " " + e.getMessage(), e);
            }

        }
        return instance;
    }

    public static void generateExample() throws ShowAndExitException {
        String filename = "DBConnSettingsExample.xml";
        DBConnSettings s = new DBConnSettings();
        s.setConnURL("jdbc:oracle:thin:scott/tiger@myhost:1521:orcl");
        Utils.marshal(s, filename, DBConnSettings.class);
    }

    public String getConnURL() {
        return connURL;
    }

    @XmlElement()
    private void setConnURL(String connURL) {
        this.connURL = connURL;
    }
}
