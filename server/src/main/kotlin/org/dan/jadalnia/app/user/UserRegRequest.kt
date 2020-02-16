package org.dan.jadalnia.app.user

import com.fasterxml.jackson.annotation.JsonCreator
import org.dan.jadalnia.app.festival.pojo.Fid

import javax.validation.constraints.Size
import java.util.Optional


data class UserRegRequest @JsonCreator constructor(
        var festivalId: Fid,
        @Size(min = 3, max = 40)
        var name: String,
        var session: String,
        var userType: UserType) //,
// todo how to have default values
//        var email: Optional<String> = Optional.empty(),
//        var phone: Optional<String> = Optional.empty())
{
//    @JsonCreator
//    constructor(
//            festivalId: Fid,
//            name: String,
//            session: String,
//            userType: UserType): this(festivalId, name, session, userType,
//            Optional.empty(), Optional.empty()) {
//    }
}
