package org.home.extractor;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.home.model.BaseObj;
import org.home.settings.DBConnSettings;
import org.home.settings.StartupSettings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by oleg on 2017-09-10.
 */
public class OldExtractor implements IExtractor {
    @Override
    public void init() {
    }

    private String getSQL(BaseObj b) {
        return "select owner, name, type, line, text from dba_source where owner='" + b.getOwner() + "' and name = '" + b.getName() + "' ";
    }


    @Override
    public List<BaseObj> extract(Collection<BaseObj> baseObjs) throws SQLException {
        int cnt = 0;
        List<BaseObj> pack = new ArrayList<>();
        List<BaseObj> res = new ArrayList<>();
        QueryRunner run = new QueryRunner(DBConnSettings.getDataSource());
        ResultSetHandler<List<OraDbaSource>> h = new BeanListHandler<OraDbaSource>(OraDbaSource.class);
        for (BaseObj baseObj : baseObjs) {
            pack.add(baseObj);
            if ((cnt > 0 && cnt % 100 == 0) || cnt == baseObjs.size() - 1) {
                String packsql = String.join(" union all ", pack.stream().map(pe -> getSQL(pe)).collect(Collectors.toList()));
                packsql = packsql + " order by owner, name, type, line asc ";
                List<OraDbaSource> oraDbaSources = run.query(packsql, h);
                Map<BaseObj, String> baseobjDefsAndSources = oraDbaSources.stream().collect(Collectors.groupingBy(e -> new BaseObj(e.getOwner(), e.getName(), e.getType()), Collectors.mapping(e -> e.getText(), Collectors.joining())));
                Map<BaseObj, List<String>> baseobjDefsAndListOfSources = baseobjDefsAndSources.entrySet().stream().
                        sorted((f1, f2) -> f1.getKey().compareTo(f2.getKey())).
                        collect(Collectors.groupingBy(e -> new BaseObj(e.getKey().getOwner(), e.getKey().getName(), e.getKey().getSuperType()), Collectors.mapping(e -> e.getValue(), Collectors.toList())));
                List<BaseObj> sourcesPack = baseobjDefsAndListOfSources.entrySet().stream().map(es -> es.getKey().setSourceCode(es.getValue().get(0), es.getValue().size() > 1 ? es.getValue().get(1) : null)).collect(Collectors.toList());
                res.addAll(sourcesPack);
                pack.clear();
                System.out.println(cnt);
            }
            cnt++;
        }
        return res;
    }
}
