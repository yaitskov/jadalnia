package org.dan.jadalnia.sys.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.sys.error.JadalniaEx;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SeqBatchExecutor implements BatchExecutor {
    @Override
    public List<Integer> execute(List<DbUpdateSql> updates) {
        final List<Integer> result = new ArrayList<>(updates.size());
        for (DbUpdateSql update : updates) {
            update.getLogBefore().run();
            try {
                result.add(update.getQuery().execute());
            } catch (Exception e) {
                throw JadalniaEx.internalError("Sql [" + result.size()
                        + "] failed: [" + update.getQuery().getSQL() + "]", e);
            }
        }
        return result;
    }
}
