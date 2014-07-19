package ch.squix.feederator.rest.feeds;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.Feed;
import ch.squix.feederator.rest.items.InboxItemResource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class FeedsResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(InboxItemResource.class.getName());

    @Get(value = "json")
    public List<FeedDto> execute() throws UnsupportedEncodingException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        List<Feed> feeds = ofy().load()
                .type(Feed.class)
                .filter("appUserId", user.getUserId())
                .list();
        List<FeedDto> dtos = FeedConverter.convertToDtos(feeds);
        return dtos;

    }

}
