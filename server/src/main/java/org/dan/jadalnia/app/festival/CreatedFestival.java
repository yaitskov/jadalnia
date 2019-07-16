package org.dan.jadalnia.app.festival;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dan.jadalnia.app.user.UserSession;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedFestival {
    Fid fid;
    UserSession session;
}
