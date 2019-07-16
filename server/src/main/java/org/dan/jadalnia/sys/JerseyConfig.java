
package org.dan.jadalnia.sys;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.glassfish.jersey.server.ServerProperties.BV_SEND_ERROR_IN_RESPONSE;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.dan.jadalnia.app.auth.AuthResource;
import org.dan.jadalnia.app.auth.SysAdminSignInResource;
import org.dan.jadalnia.app.bid.BidResource;
import org.dan.jadalnia.app.castinglots.CastingLotsResource;
import org.dan.jadalnia.app.category.CategoryResource;
import org.dan.jadalnia.app.city.CityResource;
import org.dan.jadalnia.app.country.CountryResource;
import org.dan.jadalnia.app.group.GroupResource;
import org.dan.jadalnia.app.order.OrderResource;
import org.dan.jadalnia.app.order.dispute.MatchDisputeResource;
import org.dan.jadalnia.app.place.PlaceResource;
import org.dan.jadalnia.app.suggestion.ParticipantSuggestionResource;
import org.dan.jadalnia.app.table.TableResource;
import org.dan.jadalnia.app.festival.FestivalResource;
import org.dan.jadalnia.app.festival.marshaling.TournamentMarshalingResource;
import org.dan.jadalnia.app.user.UserResource;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperContextResolver;
import org.dan.jadalnia.sys.error.DefaultExceptionMapper;
import org.dan.jadalnia.sys.error.InvalidTypeIdExceptionMapper;
import org.dan.jadalnia.sys.error.JerseyExceptionMapper;
import org.dan.jadalnia.sys.error.JerseyValidationExceptionMapper;
import org.dan.jadalnia.sys.error.JooqExceptionMapper;
import org.dan.jadalnia.sys.error.JsonMappingExceptionMapper;
import org.dan.jadalnia.sys.error.PiPoExMapper;
import org.dan.jadalnia.sys.error.UncheckedExecutionExceptionMapper;
import org.dan.jadalnia.sys.error.UndeclaredThrowableExecutionExceptionMapper;
import org.dan.jadalnia.sys.error.UnrecognizedPropertyExceptionMapper;
import org.dan.jadalnia.sys.warmup.WarmUpHttpFilter;
import org.dan.jadalnia.sys.warmup.WarmUpResource;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        property(BV_SEND_ERROR_IN_RESPONSE, true);
        register(new LoggingFilter());
        register(WarmUpHttpFilter.class);
        register(ObjectMapperContextResolver.class);
        register(JacksonJaxbJsonProvider.class);
        register(new PiPoExMapper());
        register(new JerseyExceptionMapper()); // just class get exception
        register(new JerseyValidationExceptionMapper());
        register(new DefaultExceptionMapper());
        register(UnrecognizedPropertyExceptionMapper.class);
        register(JooqExceptionMapper.class);
        register(JsonMappingExceptionMapper.class);
        register(InvalidTypeIdExceptionMapper.class);
        register(UncheckedExecutionExceptionMapper.class);
        register(UndeclaredThrowableExecutionExceptionMapper.class);
        packages(false,
                asList(UserResource.class, SysAdminSignInResource.class,
                        PlaceResource.class, FestivalResource.class,
                        WarmUpResource.class,
                        ParticipantSuggestionResource.class,
                        TournamentMarshalingResource.class,
                        GroupResource.class, MatchDisputeResource.class,
                        BidResource.class, CategoryResource.class,
                        OrderResource.class, AuthResource.class,
                        CountryResource.class, CityResource.class,
                        TableResource.class, CastingLotsResource.class)
                        .stream()
                        .map(Class::getPackage)
                        .map(Package::getName)
                        .collect(toList())
                        .toArray(new String[0]));
    }
}
