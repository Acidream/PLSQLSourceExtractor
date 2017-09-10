package org.home.model.MaskGroup;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oleg on 2017-07-12.
 */
@XmlRootElement(name = "objGroupSettings")
public class MaskGroupFileAgg {
    private MaskGroupFileAgg() {
    }

    private static MaskGroupFileAgg instance;

    private String filename = "org.home.settings.xml";

    private List<MaskGroup> groups = new ArrayList<MaskGroup>();

    public List<MaskGroup> getGroups() {
        return groups;
    }

    @XmlElement(name = "Group")
    private void setGroups(List<MaskGroup> groups) {
        this.groups = groups;
    }

    public static MaskGroupFileAgg fromObjNames(List<String> objNames) {
        MaskGroupFileAgg res = new MaskGroupFileAgg();
        res.setGroups(Arrays.asList(new MaskGroup("grp1", "NoConfOut\\", objNames)));
        return res;
    }

    public static MaskGroupFileAgg get(String filename) {
        if (instance == null || !instance.filename.equals(filename)) {
            JAXBContext jc = null;
            try {
                jc = JAXBContext.newInstance(MaskGroupFileAgg.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                File xml = new File(filename);
                instance = (MaskGroupFileAgg) unmarshaller.unmarshal(xml);
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
            JAXBContext jc = JAXBContext.newInstance(MaskGroupFileAgg.class);
            MaskGroupFileAgg s = new MaskGroupFileAgg();
            MaskGroup gm = new MaskGroup();
            gm.setOutFolder("c:\\temp");
            gm.setDBObjects(Arrays.asList("abs.iacq_order%", "abs.fm_%"));
            gm.setName("firstGroup");
            MaskGroup gm2 = new MaskGroup();
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


    public void saveObjs() throws IOException, SQLException {
        for (MaskGroup group : groups) {
            group.saveObjs();
        }


    }


}
