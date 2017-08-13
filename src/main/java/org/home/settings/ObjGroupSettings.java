package org.home.settings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oleg on 2017-07-12.
 */
@XmlRootElement
public class ObjGroupSettings {
    public ObjGroupSettings() {
    }

    private static ObjGroupSettings instance;

    private String filename = "org.home.settings.xml";

    List<ObjGroup> groups = new ArrayList<ObjGroup>();

    public List<ObjGroup> getGroups() {
        return groups;
    }

    @XmlElement(name = "Group")
    public void setGroups(List<ObjGroup> groups) {
        this.groups = groups;
    }


    public static ObjGroupSettings get(String filename) {
        if (instance == null || !instance.filename.equals(filename)) {
            JAXBContext jc = null;
            try {
                jc = JAXBContext.newInstance(ObjGroupSettings.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                File xml = new File(filename);
                instance = (ObjGroupSettings) unmarshaller.unmarshal(xml);
                instance.filename = filename;
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static void generateExample() {
        String filename = "ObjGroupSettingsExample.xml";
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjGroupSettings.class);
            ObjGroupSettings s = new ObjGroupSettings();
            ObjGroup gm = new ObjGroup();
            gm.setOutFolder("c:\\temp");
            gm.setDBObjects(Arrays.asList("abs.iacq_order%", "abs.fm_%"));
            gm.setName("firstGroup");
            ObjGroup gm2 = new ObjGroup();
            gm2.setOutFolder("c:\\temp");
            gm2.setDBObjects(Arrays.asList("abs.iacq_order%", "abs.fm_%,abs.alalalala"));
            gm2.setName("firstGroup2");
            s.setGroups(Arrays.asList(gm, gm2));
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
