package org.home.model.MaskGroup;

import org.home.model.BaseObj;
import org.home.settings.ShowAndExitException;
import org.home.settings.Utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.nio.file.Path;

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

    public static MaskGroupFileAgg get(String filename) throws ShowAndExitException {
        if (instance == null || !instance.filename.equals(filename)) {
            instance = Utils.unmarshal(filename, MaskGroupFileAgg.class);
            instance.filename = filename;
        }
        return instance;
    }

    public static void generateExample() throws ShowAndExitException {
        String filename = "ObjGroupSettingsExample.xml";
            MaskGroupFileAgg s = new MaskGroupFileAgg();
            MaskGroup gm = new MaskGroup();
        gm.setOutFolder("c:\\temp\\book");
        gm.setDBObjects(Arrays.asList("abs.order%", "abs.book_%"));
            gm.setName("firstGroup");
            MaskGroup gm2 = new MaskGroup();
        gm2.setOutFolder("c:\\temp\\lalala");
        gm2.setDBObjects(Arrays.asList("abs.order%", "abs.fm_%,abs.alalalala"));
            gm2.setName("firstGroup2");
            s.setGroups(Arrays.asList(gm, gm2));
        Utils.marshal(s, filename, MaskGroupFileAgg.class);
    }


    public List<Path> loadAndSaveObjs() throws IOException, SQLException {
        List<Path> paths = new ArrayList<>();
        for (MaskGroup group : groups) {
            paths.addAll( group.loadAndSaveObjs());
        }
        return paths;
    }

    public List<BaseObj> getBaseObjs(){
        List<BaseObj>res =new ArrayList<>();
        for (MaskGroup group : groups) {
            res.addAll(group.getBaseObjs());
        }
        return res;
    }






}
