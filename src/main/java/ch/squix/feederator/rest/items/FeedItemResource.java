package ch.squix.feederator.rest.items;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.Feed;
import ch.squix.feederator.model.FeedItem;
import ch.squix.feederator.rest.parser.FeedItemConverter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class FeedItemResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(FeedItemResource.class.getName());

    @Get(value = "json")
    public List<FeedItemDto> execute() throws UnsupportedEncodingException {
        String feedIdParam = (String) this.getRequestAttributes().get("feedId");
        logger.info("Loading feeds for id " + feedIdParam);
        Long feedId = Long.valueOf(feedIdParam);
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        Feed feed = ofy().load().type(Feed.class).id(feedId).now();
        if (!feed.getAppUserId().equals(user.getUserId())) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        List<FeedItem> feedItems = ofy().load()
                .type(FeedItem.class)
                .filter("feedId", feedId)
                .order("-importedDate")
                .limit(50)
                .list();
        List<FeedItemDto> dtos = FeedItemConverter.convertToDtos(feedItems);
        return dtos;

    }

}
