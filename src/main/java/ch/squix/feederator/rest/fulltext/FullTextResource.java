package ch.squix.feederator.rest.fulltext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.Image;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import de.l3s.boilerpipe.sax.HtmlArticleExtractor;
import de.l3s.boilerpipe.sax.ImageExtractor;


public class FullTextResource extends ServerResource {


    private static final Logger logger = Logger.getLogger(FullTextResource.class.getName());

    @Get(value = "json")
    public FullTextDto execute() throws IOException, BoilerpipeProcessingException, SAXException,
            URISyntaxException {
        String urlParam = (String) this.getRequestAttributes().get("url");

        BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        HtmlArticleExtractor htmlExtr = HtmlArticleExtractor.INSTANCE;
        URL url = new URL(URLDecoder.decode(urlParam, "utf-8"));
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);

        String html = htmlExtr.process(htmlDoc, url.toURI(), extractor);

        final ImageExtractor ie = ImageExtractor.INSTANCE;

        final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
        extractor.process(doc);

        final InputSource is = htmlDoc.toInputSource();

        List<Image> imgUrls = ie.process(doc, is);
        Collections.sort(imgUrls);

        FullTextDto dto = new FullTextDto();
        dto.setOriginalUrl(urlParam);
        dto.setFullText(html);
        if (imgUrls.size() > 0) {
            dto.setImageUrl(imgUrls.get(0).getSrc());
        }
        return dto;
    }
}
