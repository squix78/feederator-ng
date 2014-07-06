package ch.squix.feederator.rest.parser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ch.squix.feederator.model.Feed;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


public class UpdateFeedsResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(UpdateFeedsResource.class.getName());

    @Get(value = "json")
    public String execute() throws UnsupportedEncodingException {
        List<Feed> feeds = ofy().load().type(Feed.class).list();
        Queue queue = QueueFactory.getDefaultQueue();
        for (Feed feed : feeds) {
            queue.add(TaskOptions.Builder.withUrl("/rest/admin/parser/" + feed.getId()).method(
                    Method.GET));
        }
        return "OK";
    }

    public SyndFeed readFeed(String sourceURL) {

        sourceURL = sourceURL.trim().replaceAll(" ", "+");
        logger.info("Reading source URL: [" + sourceURL + "]");
        XmlReader reader = null;
        SyndFeed inputFeed = null;
        long startTime = System.currentTimeMillis();
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
            byte[] content = response.getContent();
            logger.info("Content" + new String(content, "utf-8"));
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

            System.out.println("Reader encoding: " + reader.getEncoding());
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
