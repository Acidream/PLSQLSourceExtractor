package org.home.extractor;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.model.BaseObj;
import org.home.settings.DBConnSettings;
import org.home.settings.ObjGroup;
import org.home.settings.ObjOwnerAndMask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-08-05.
 */
public class SrcExtractor {

    private List<ObjGroup> objGrps;


    public SrcExtractor(List<ObjGroup> objGrps) {
        this.objGrps = objGrps;
    }

    volatile int cnt;
    public void extract() throws SQLException, IOException {
        for (ObjGroup objGrp : objGrps) {
            for (ObjOwnerAndMask objOwnerAndMask : objGrp.getObjOwnerAndMasks()) {
                List<BaseObj> bobs = objOwnerAndMask.loadObjectListFromDB();
                bobs.parallelStream().forEach(bob -> {
                    try {
                        bob.extractSources();
                        bob.saveToFile(objGrp.getOutFolder());
                        cnt++;
                        if (cnt % 100 == 0)
                            System.out.println("Written: " + cnt + " files in group " + objGrp.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        System.out.println("Extraction complete: " + cnt + " files written!");
    }


}
