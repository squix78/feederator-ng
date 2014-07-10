package ch.squix.feederator.rest.testdata;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.Feed;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class TestDataResource extends ServerResource {

    @Get(value = "json")
    public String execute() throws UnsupportedEncodingException {
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();
        List<Feed> feeds = new ArrayList<>();
        Feed feed = new Feed();
        feed.setAppUserId(currentUser.getUserId());
        feed.setUrl("http://hackaday.com/feed/");
        feed.setName("Hackaday");
        feed.setDescription("Fresh hacks per day");
        feeds.add(feed);

        feed = new Feed();
        feed.setAppUserId(currentUser.getUserId());
        feed.setUrl("https://news.ycombinator.com/rss");
        feed.setName("Hackernews");
        feed.setDescription("Y-Combinator Hackernews");
        feeds.add(feed);

        feed = new Feed();
        feed.setAppUserId(currentUser.getUserId());
        feed.setUrl("http://feeds.gawker.com/lifehacker/full");
        feed.setName("Lifehacker");
        feed.setDescription("Lifehacker");
        feeds.add(feed);

        feed = new Feed();
        feed.setAppUserId(currentUser.getUserId());
        feed.setUrl("http://www.heise.de/newsticker/heise-atom.xml");
        feed.setName("Heise");
        feed.setDescription("Heise");
        feeds.add(feed);

        ofy().save().entities(feeds);
        return "OK";
    }

}
