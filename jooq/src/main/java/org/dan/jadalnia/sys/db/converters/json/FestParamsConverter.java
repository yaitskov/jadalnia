package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dan.jadalnia.app.festival.pojo.FestParams;
import org.dan.jadalnia.sys.jackson.ObjectMapperFactory;

public class FestParamsConverter extends JsonConverter<FestParams> {
    public FestParamsConverter() {
        super(
                new TypeReference<FestParams>() {},
                ObjectMapperFactory.INSTANCE.create());
    }
}
