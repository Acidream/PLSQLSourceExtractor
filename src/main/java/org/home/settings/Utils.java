package org.home.settings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Created by oleg on 2017-07-12.
 */
public class Utils {

    public static <T> T unmarshal(String filename, Class<T> cls) throws ShowAndExitException {

        T res = null;

        File xml = new File(filename);
        if (!xml.exists()) throw new ShowAndExitException("File " + filename + " not found");
        try {
            JAXBContext jc = JAXBContext.newInstance(cls);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            res = (T) unmarshaller.unmarshal(xml);
        } catch (JAXBException e) {
            throw new ShowAndExitException("Unmarshalling file " + filename + " as " + cls.getSimpleName() + " file error", e);
        }
        return res;

    }

    public static <T> void marshal(Object o, String filename, Class<T> cls) throws ShowAndExitException {
        File xml = new File(filename);
        if (xml.exists()) {
            System.out.println("Example file already exists. Remove it before generation.");
            return;
        }
        try {
            JAXBContext jc = JAXBContext.newInstance(DBConnSettings.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(o, xml);
        } catch (JAXBException e) {
            throw new ShowAndExitException("Marshalling file to" + filename + " from " + cls.getSimpleName() + " error", e);
        }
    }

    public static String clobToString(Clob data) {
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = data.getCharacterStream();
            BufferedReader br = new BufferedReader(reader);

            String line;
            while (null != (line = br.readLine())) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
        } catch (SQLException e) {
            // handle this exception
        } catch (IOException e) {
            // handle this exception
        }
        return sb.toString();
    }

}
