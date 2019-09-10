package org.dan.jadalnia.app.label;

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.jooq.tables.Labels.LABELS
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


class LabelDao: AsyncDao() {
    fun  storeNew(fid: Fid, labelId: Int): CompletableFuture<OrderLabel> {
        val label = OrderLabel.of(labelId);
        return execQuery(
                { jooq -> jooq.insertInto(LABELS,
                        LABELS.FESTIVAL_ID,
                        LABELS.LABEL)
                        .values(fid, label)
                        .execute()
                })
                .thenApply({ rows -> label });
    }

    fun maxOrderNumber(fid: Fid): CompletionStage<OrderLabel> {
        return execQuery(
                { jooq -> ofNullable(jooq
                        .select(LABELS.LABEL.max().`as`(LABELS.LABEL))
                        .from(LABELS)
                        .where(LABELS.FESTIVAL_ID.eq(fid))
                        .fetchOne())
                        .map({r -> r.get(LABELS.LABEL)})
                        .orElseGet({ OrderLabel.of(0) })
                })
    }
}
