package org.dan.jadalnia.app.label;

import org.springframework.context.annotation.Import;

@Import({LabelService.class, LabelDao.class})
public class OrderLabelCtx {
}
