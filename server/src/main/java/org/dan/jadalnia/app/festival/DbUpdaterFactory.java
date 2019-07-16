package org.dan.jadalnia.app.festival;

import org.dan.jadalnia.sys.db.BatchExecutor;
import org.dan.jadalnia.sys.db.DbUpdaterSql;

import javax.inject.Inject;

public class DbUpdaterFactory {
    @Inject
    private BatchExecutor batchExecutor;

    public DbUpdaterSql create() {
        return DbUpdaterSql.create(batchExecutor);
    }
}
