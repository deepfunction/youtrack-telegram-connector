package youtrack.telegram.connector.resource;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import youtrack.telegram.connector.service.CacheService;
import youtrack.telegram.connector.service.YoutrackService;
import youtrack.telegram.connector.service.telegram.TelegramBotService;
import youtrack.telegram.connector.utils.ApiPaths;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ApiPaths.BASE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class YouTrackTelegramResource {

    @Inject
    YoutrackService youTrackService;

    @Inject
    TelegramBotService telegramBotService;

    @Inject
    CacheService cacheService;

    @POST
    @Path(ApiPaths.YOUTRACK)
    public Response onYoutrackUpdateReceived(@QueryParam("login") String youTrackLogin, @QueryParam("text") String text) {
        return Response
                .status(Response.Status.ACCEPTED)
                .entity(youTrackService.onWebhookUpdateReceived(youTrackLogin, text))
                .build();
    }

    @POST
    @Path(ApiPaths.TELEGRAM)
    public BotApiMethod<?> onTelegramUpdateReceived(Update update) {
        return telegramBotService.onWebhookUpdateReceived(update);
    }

    @PUT
    @Path(ApiPaths.INVALIDATE_CACHE)
    public Response invalidateCache() {
        cacheService.invalidateAllCache();
        return Response
                .status(Response.Status.OK)
                .build();
    }
}
