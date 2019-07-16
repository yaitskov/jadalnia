package org.dan.jadalnia.app.order.dispute;

import org.dan.jadalnia.app.festival.Fid;
import org.dan.jadalnia.sys.db.DbUpdater;

public interface MatchDisputeDao {
    void create(Fid fid, DisputeMemState dispute, DbUpdater batch);
}
