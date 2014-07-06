package ch.squix.feederator.rest.items;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.FeedItem;
import ch.squix.feederator.rest.parser.FeedItemConverter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class InboxItemResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(InboxItemResource.class.getName());

    @Get(value = "json")
    public List<FeedItemDto> execute() throws UnsupportedEncodingException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        List<FeedItem> feeds = ofy().load()
                .type(FeedItem.class)
                .filter("appUserId", user.getUserId())
                .order("-importedDate")
                .limit(50)
                .list();
        List<FeedItemDto> dtos = FeedItemConverter.convertToDtos(feeds);
        return dtos;

    }

}
