package org.dan.jadalnia.sys.db;

import static org.dan.jadalnia.sys.error.JadEx.internalError;
import static org.eclipse.jetty.http.HttpStatus.LOCKED_423;

import org.dan.jadalnia.sys.error.Error;
import org.dan.jadalnia.sys.error.JadEx;

public class DbStrictUpdater implements DbUpdater {
    public static final DbStrictUpdater DB_STRICT_UPDATER = new DbStrictUpdater();

    @Override
    public void flush() {
        // relax
    }

    @Override
    public void rollback() {
        throw new JadEx(LOCKED_423, new Error("Rollback transaction"), null);
    }

    @Override
    public DbUpdater onFailure(Runnable r) {
        throw internalError("not implemented");
    }

    @Override
    public DbUpdater exec(DbUpdate u) {
        ((DbUpdateSql) u).getQuery().execute();
        return this;
    }

    @Override
    public void markDirty() {
        // relax
    }
}
