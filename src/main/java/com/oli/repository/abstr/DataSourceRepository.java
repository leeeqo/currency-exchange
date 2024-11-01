package com.oli.repository.abstr;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceRepository {

    protected final DataSource dataSource;

    public DataSourceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
