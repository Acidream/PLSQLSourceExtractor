package org.home.extractor;

import org.home.model.BaseObj;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by oleg on 2017-09-10.
 */
public interface IExtractor {


    void init() throws SQLException;

    List<BaseObj> extract(List<BaseObj> input) throws SQLException;

}
