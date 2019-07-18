package org.dan.jadalnia.app.festival;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.dan.jadalnia.app.ws.PropertyUpdated;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropertyUpdated.class, name = "propertyUpdated")
})
public interface MessageForClient {
}
