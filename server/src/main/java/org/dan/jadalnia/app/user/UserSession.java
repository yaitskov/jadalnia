package org.dan.jadalnia.app.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dan.jadalnia.sys.error.TemplateError;

import static java.util.Collections.singletonMap;
import static org.dan.jadalnia.sys.error.JadEx.badRequest;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private Uid uid;
    private String key;

    public static UserSession valueOf(String s) {
        final String[] parts = s.split(":", 2);
        try {
            return UserSession.builder()
                    .uid(Uid.valueOf(parts[0]))
                    .key(parts[1])
                    .build();
        } catch (NumberFormatException e) {
            throw badRequest(
                    new TemplateError("user session has wrong format: [$session]",
                            singletonMap("session", s)), e);
        }
    }

    public String toString() {
        return uid + ":" + key;
    }
}
