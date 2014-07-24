package ch.squix.feederator.rest.parser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.squix.feederator.model.Article;
import ch.squix.feederator.model.Feed;
import ch.squix.feederator.model.FeedItem;
import ch.squix.feederator.util.FeedUtil;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.Image;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.ImageExtractor;


public class ParserResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(ParserResource.class.getName());

    @Get(value = "json")
    public String execute() throws UnsupportedEncodingException {
        String feedId = (String) this.getRequestAttributes().get("feedId");
        Feed feed = ofy().load().type(Feed.class).id(Long.valueOf(feedId)).now();
        SyndFeed syndFeed = readFeed(feed.getUrl());
        List<FeedItem> items = new ArrayList<>();
        Integer knownItemCount = 0;
        if (syndFeed != null) {
            List<SyndEntry> entries = syndFeed.getEntries();
            for (SyndEntry syndEntry : entries) {
                String uuid = FeedUtil.getUuid(syndEntry);
                if (!feed.containsUuid(uuid)) {
                    feed.getUuids().add(uuid);
                    FeedItem item = FeedItemConverter.convertFromSyndEntry(syndEntry);
                    item.setFeedId(feed.getId());
                    item.setAppUserId(feed.getAppUserId());
                    items.add(item);
                } else {
                    knownItemCount++;
                }
            }
        } else {
            feed.setLastFail(new Date());
        }
        enrichItems(items);
        logger.info("Found " + items.size() + " new items. Discard " + knownItemCount
                + " items as old.");
        if (items.size() > 0) {
            feed.setLastUpdate(new Date());
            ofy().save().entities(items).now();
        }
        feed.setLastSuccess(new Date());
        ofy().save().entity(feed);
        return "OK";
    }

    private void enrichItems(List<FeedItem> items) {
        for (FeedItem item : items) {
            Article article = getArticle(item.getLink());
            item.setArticle(article);
            if (article != null) {
                item.setTeaserImageSource(getImageUrl(article.getContent()));
            }
        }
    }

    private String getImageUrl(String content) {
        URL url;
        try {
            final HTMLDocument htmlDoc = new HTMLDocument(content);

            final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
            extractor.process(doc);

            final InputSource is = htmlDoc.toInputSource();


            // choose from a set of useful BoilerpipeExtractors...

            final ImageExtractor ie = ImageExtractor.INSTANCE;

            List<Image> imgUrls = ie.process(doc, content);

            // automatically sorts them by decreasing area, i.e. most probable
            // true positives come first
            Collections.sort(imgUrls);

            for (Image img : imgUrls) {
                if (img.getArea() > 1000) {
                    return img.getSrc();
                }
            }
            return null;
        } catch (BoilerpipeProcessingException e) {
            logger.log(Level.WARNING, "Failed to get image resource", e);
            return null;
        } catch (SAXException e) {
            logger.log(Level.WARNING, "Failed to get image resource", e);
            return null;
        }
    }

    private Article getArticle(String link) {
        URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();

        try {
            String urlEncodedLink = URLEncoder.encode(link, "utf-8");
            URL url = new URL("http://fulltext-squix.rhcloud.com/full-text-rss/extract.php?url="
                    + urlEncodedLink);
            HTTPResponse response = fetchService.fetch(new HTTPRequest(url, HTTPMethod.GET,
                    FetchOptions.Builder.disallowTruncate()
                            .followRedirects()
                            .doNotValidateCertificate()
                            .setDeadline(60D)));
            byte[] content = response.getContent();
            // logger.info("Content" + new String(content, "utf-8"));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, Article.class);
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Could not read fulltext", e);
            return null;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not read fulltext", e);
            return null;
        }
    }

    public SyndFeed readFeed(String sourceURL) {

        sourceURL = sourceURL.trim().replaceAll(" ", "+");
        logger.info("Reading source URL: [" + sourceURL + "]");
        XmlReader reader = null;
        SyndFeed inputFeed = null;
        long startTime = System.currentTimeMillis();
        byte[] content = new byte[0];
        try {

            SyndFeedInput input = new SyndFeedInput(false);
            input.setXmlHealerOn(true);

            URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();

            URL url = new URL(sourceURL.replaceAll(" ", "+"));
            HTTPResponse response = fetchService.fetch(new HTTPRequest(url, HTTPMethod.GET,
                    FetchOptions.Builder.disallowTruncate()
                            .followRedirects()
                            .doNotValidateCertificate()
                            .setDeadline(60D)));
            content = response.getContent();
            // logger.info("Content" + new String(content, "utf-8"));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            // HttpURLConnection connection = (HttpURLConnection)
            // url.openConnection();
            //
            // connection.setConnectTimeout(20000);
            // connection.setReadTimeout(20000);
            // InputStream inputStream = connection.getInputStream();
            // log.info("Content-Type: " + connection.getContentType());
            // byte[] filedata = ByteStreams.toByteArray(inputStream);
            // ByteArrayInputStream byteArrayInputStream = new
            // ByteArrayInputStream(filedata);
            reader = new XmlReader(inputStream, false);

            logger.info("Reader encoding: " + reader.getEncoding());
            inputFeed = input.build(reader);
            logger.info("Took " + (System.currentTimeMillis() - startTime) + "ms to read "
                    + sourceURL);

            // } catch (Exception e) {
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Could not read feed", e);
            return null;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not read feed", e);
            return null;
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Could not read feed", e);
            return null;
        } catch (FeedException e) {
            logger.log(Level.SEVERE, "Could not read feed", e);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not read feed", e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // We did what we could
                    e.printStackTrace();
                }
            }
        }
        return inputFeed;
    }

}
