package ch.squix.feederator.rest.items;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.FeedItem;
import ch.squix.feederator.rest.parser.FeedItemConverter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class ItemResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(ItemResource.class.getName());

    @Get(value = "json")
    public FeedItemDto execute() throws UnsupportedEncodingException {
        String itemIdParam = (String) this.getRequestAttributes().get("itemId");
        Long itemId = Long.valueOf(itemIdParam);
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        FeedItem feedItem = ofy().load().type(FeedItem.class).id(itemId).now();
        if (feedItem != null && !user.getUserId().equals(feedItem.getAppUserId())) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }

        FeedItemDto dto = FeedItemConverter.convertToDto(feedItem);
        return dto;

    }

}
