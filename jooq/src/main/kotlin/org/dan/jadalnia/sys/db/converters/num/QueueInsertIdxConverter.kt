package org.dan.jadalnia.sys.db.converters.num

import org.dan.jadalnia.util.collection.MapQ.QueueInsertIdx

class QueueInsertIdxConverter
  : TypedIdConverter<QueueInsertIdx>(::QueueInsertIdx) {

  override fun toType() = QueueInsertIdx::class.java
}