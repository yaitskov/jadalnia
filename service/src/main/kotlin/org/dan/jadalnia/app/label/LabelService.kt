package org.dan.jadalnia.app.label;

import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.order.pojo.OrderLabel;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

class LabelService @Inject constructor (val labelDao: LabelDao) {
    fun allocate(festival: Festival): CompletableFuture<OrderLabel> =
        labelDao.storeNew(festival.fid(), festival.nextLabel.getAndIncrement())

}
