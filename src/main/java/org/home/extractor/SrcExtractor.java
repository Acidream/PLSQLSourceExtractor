package org.home.extractor;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.settings.DBConnSettings;
import org.home.settings.ObjGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-08-05.
 */
public class SrcExtractor {

    private List<ObjGroup> objGrps;


    public SrcExtractor(List<ObjGroup> objGrps) {
        this.objGrps = objGrps;
    }

    public void extract() throws SQLException, IOException {
        HashSet<String> files = new HashSet<String>();
        for (ObjGroup objGrp : objGrps) {
            for (String obj : objGrp.getDBObjects()) {
                String owner = obj.substring(0, obj.indexOf('.'));
                String like = obj.substring(obj.indexOf('.') + 1);
                QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
                ResultSetHandler<List<OraDbaSource>> h = new BeanListHandler<OraDbaSource>(OraDbaSource.class);
                List<OraDbaSource> srcLines = run.query("select owner, name, type, line, text, origin_con_id from dba_source where owner=? and name like ? order by owner, name, type desc, line", h, owner, like);
                Map<String, String> fileLines2 = listToString(srcLines);
                for (String fname : fileLines2.keySet()) {
                    Files.createDirectories(Paths.get(objGrp.getOutFolder()));
                    Path path = Paths.get(objGrp.getOutFolder(), fname);
                    System.out.println("Writing file " + path);
                    Files.write(path, fileLines2.get(fname).getBytes());
                    files.add(path.toString());
                }
            }
        }
        System.out.println("Extraction complete: " + files.size() + " files written!");
    }

    private static Map<String, String> listToString(List<OraDbaSource> srcLines) {
        Map<String, String> fileLines = srcLines.stream().collect(Collectors.groupingBy(e -> e.owner + "." + e.name + "." + (e.type.equalsIgnoreCase("PACKAGE") ? "_PACKAGE" : e.type), Collectors.mapping(e -> e.getText(), Collectors.joining())));
        return fileLines.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().split("\\.")[1] + ".sql", Collectors.mapping(e -> "CREATE OR REPLACE " + e.getValue() + "\n/\n\n", Collectors.joining())));
    }
}
