package com.oli.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceRepository {

    protected final DataSource dataSource;

    public DataSourceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
