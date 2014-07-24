package ch.squix.feederator.rest.feeds;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.Feed;
import ch.squix.feederator.rest.items.InboxItemResource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class FeedResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(InboxItemResource.class.getName());

    @Get(value = "json")
    public FeedDto execute() throws UnsupportedEncodingException {
        String feedIdParam = (String) this.getRequestAttributes().get("feedId");
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        Feed feed = ofy().load().type(Feed.class).id(Long.valueOf(feedIdParam)).now();
        if (feed == null) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }
        if (!user.getUserId().equals(feed.getAppUserId())) {
            setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            return null;
        }
        FeedDto dto = FeedConverter.convertToDto(feed);
        return dto;

    }

    @Post(value = "json")
    public FeedDto update(FeedDto dto) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        }
        Feed feed = new Feed();
        if (dto.getId() != null) {
            feed = ofy().load().type(Feed.class).id(Long.valueOf(dto.getId())).now();
            if (feed == null) {
                setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                return null;
            }
            if (!feed.getAppUserId().equals(user.getUserId())) {
                setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                return null;
            }
            FeedConverter.convertFromDto(feed, dto);
            ofy().save().entity(feed).now();
            return dto;
        }
        return null;

    }

}
