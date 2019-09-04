package org.dan.jadalnia.app.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dan.jadalnia.app.festival.pojo.Fid;

import javax.validation.constraints.Size;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegRequest {
    Fid festivalId;
    @Size(min = 3, max = 40)
    String name;
    String session;
    private UserType userType;

    Optional<String> email;
    Optional<String> phone;

    public static class UserRegRequestBuilder {
        Optional<String> email = Optional.empty();
        Optional<String> phone = Optional.empty();
    }
}
