package ch.squix.feederator.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import ch.squix.feederator.model.Feed;
import ch.squix.feederator.model.FeedItem;
import ch.squix.feederator.rest.items.InboxItemResource;
import ch.squix.feederator.rest.items.ItemResource;
import ch.squix.feederator.rest.parser.ParserResource;
import ch.squix.feederator.rest.parser.UpdateFeedsResource;
import ch.squix.feederator.rest.ping.PingResource;
import ch.squix.feederator.rest.testdata.TestDataResource;

import com.googlecode.objectify.ObjectifyService;

public class RestApplication extends Application {

    static {
        ObjectifyService.register(Feed.class);
        ObjectifyService.register(FeedItem.class);
        // ObjectifyService.register(Highscore.class);
    }

    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());
        router.attach("/ping", PingResource.class);
        router.attach("/admin/parser/{feedId}", ParserResource.class);
        router.attach("/admin/updateFeeds", UpdateFeedsResource.class);
        router.attach("/user/testdata", TestDataResource.class);
        router.attach("/user/inbox", InboxItemResource.class);
        router.attach("/user/item/{itemId}", ItemResource.class);

        return router;
    }

}
