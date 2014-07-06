package ch.squix.feederator.util;

import com.sun.syndication.feed.synd.SyndEntry;


public class FeedUtil {

    public static String getUuid(SyndEntry syndEntry) {
        if (syndEntry == null) {
            return null;
        }
        String guid = syndEntry.getUri();
        if (guid != null && !"".equals(guid)) {
            return guid;
        }
        guid = syndEntry.getLink();
        if (guid != null && !"".equals(guid)) {
            return guid;
        }
        return syndEntry.getTitle();
    }

}
