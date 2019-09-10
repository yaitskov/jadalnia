package org.dan.jadalnia.app.label;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.OrderLabel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.Optional.ofNullable;
import static org.dan.jadalnia.jooq.tables.Labels.LABELS;

@Slf4j
public class LabelDao extends AsyncDao {
    public CompletableFuture<OrderLabel> storeNew(
            Fid fid, int labelId) {
        val label = OrderLabel.of(labelId);
        return execQuery(
                jooq -> jooq.insertInto(LABELS,
                        LABELS.FESTIVAL_ID,
                        LABELS.LABEL)
                        .values(fid, label)
                        .execute())
                .thenApply(rows -> label);
    }

    public CompletionStage<OrderLabel> maxOrderNumber(Fid fid) {
        return execQuery(
                jooq -> ofNullable(jooq
                        .select(LABELS.LABEL.max().as(LABELS.LABEL))
                        .from(LABELS)
                        .where(LABELS.FESTIVAL_ID.eq(fid))
                        .fetchOne())
                        .map(r -> r.get(LABELS.LABEL))
                        .orElseGet(() -> OrderLabel.of(0)));
    }
}
