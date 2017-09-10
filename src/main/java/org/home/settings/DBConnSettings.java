package org.home.settings;

import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by oleg on 2017-07-12.
 */
@XmlRootElement
public class DBConnSettings {
    private DBConnSettings() {
    }

    private static DBConnSettings instance;
    private String filename = "DBConnSettings.xml";
    private String connURL;

    private OracleDataSource ds;

    public String getConnURL() {
        return connURL;
    }

    @XmlElement()
    private void setConnURL(String connURL) {
        this.connURL = connURL;
    }

    public static DataSource getDataSource() {
        return instance.ds;
    }

    public static DBConnSettings init(String filename) throws SQLException {
        if (instance == null || !instance.filename.equals(filename)) {
            JAXBContext jc = null;
            try {
                jc = JAXBContext.newInstance(DBConnSettings.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                File xml = new File(filename);
                instance = (DBConnSettings) unmarshaller.unmarshal(xml);
                instance.filename = filename;
                instance.ds = new OracleDataSource();
                instance.ds.setURL(instance.connURL);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static void generateExample() {
        String filename = "DBConnSettingsExample.xml";
        try {
            JAXBContext jc = JAXBContext.newInstance(DBConnSettings.class);
            DBConnSettings s = new DBConnSettings();
            s.setConnURL("jdbc:oracle:thin:scott/tiger@myhost:1521:orcl");
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File xml = new File(filename);
            if (!xml.exists()) {
                xml.createNewFile();
                marshaller.marshal(s, xml);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
