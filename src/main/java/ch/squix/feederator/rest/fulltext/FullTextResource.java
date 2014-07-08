package ch.squix.feederator.rest.fulltext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HtmlArticleExtractor;


public class FullTextResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(FullTextResource.class.getName());

    @Get(value = "json")
    public FullTextDto execute() throws IOException, BoilerpipeProcessingException, SAXException,
            URISyntaxException {
        String urlParam = (String) this.getRequestAttributes().get("url");

        BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        HtmlArticleExtractor htmlExtr = HtmlArticleExtractor.INSTANCE;
        URL url = new URL(URLDecoder.decode(urlParam, "utf-8"));
        String html = htmlExtr.process(extractor, url);

        FullTextDto dto = new FullTextDto();
        dto.setOriginalUrl(urlParam);
        dto.setFullText(html);
        return dto;
    }
}
