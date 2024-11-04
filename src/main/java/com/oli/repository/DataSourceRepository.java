package com.oli.repository;

import javax.sql.DataSource;

public abstract class DataSourceRepository {

    protected final DataSource dataSource;

    public DataSourceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
