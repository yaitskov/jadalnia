package org.dan.jadalnia.app.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        return UserSession.builder()
                .uid(Uid.valueOf(parts[0]))
                .key(parts[1])
                .build();
    }

    public String toString() {
        return uid + ":" + key;
    }
}
