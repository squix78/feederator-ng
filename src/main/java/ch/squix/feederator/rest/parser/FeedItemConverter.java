package ch.squix.feederator.rest.parser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import ch.squix.feederator.model.Article;
import ch.squix.feederator.model.Feed;
import ch.squix.feederator.model.FeedItem;
import ch.squix.feederator.rest.items.ArticleDto;
import ch.squix.feederator.rest.items.FeedItemDto;

import com.google.common.base.Strings;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;


public class FeedItemConverter {

    public static FeedItem convertFromSyndEntry(SyndEntry entry) {
        FeedItem item = new FeedItem();
        item.setUri(entry.getUri());
        item.setLink(entry.getLink());
        item.setTitle(entry.getTitle());
        item.setDescription(getTextFromSyndContent(entry.getDescription()));
        item.setDescriptionType(getTypeFromSyndContent(entry.getDescription()));
        item.setAuthor(entry.getAuthor());
        item.setUpdatedDate(entry.getUpdatedDate());
        item.setPublishedDate(entry.getPublishedDate());
        item.setImportedDate(new Date());

        List<SyndCategory> syndCategories = entry.getCategories();

        if (syndCategories != null) {
            Collection<String> categories = new HashSet<String>();
            for (SyndCategory syndCategory : syndCategories) {
                String category = StringEscapeUtils.unescapeHtml4(syndCategory.getName()).trim();
                if (!Strings.isNullOrEmpty(category)) {
                    categories.add(category);
                }
            }
            item.getCategories().addAll(categories);
        }

        return item;
    }

    private static String getTextFromSyndContent(SyndContent content) {
        if (content != null) {
            return content.getValue();
        }
        return null;
    }

    private static String getTypeFromSyndContent(SyndContent content) {
        if (content != null) {
            return content.getType();
        }
        return null;
    }

    public static FeedItemDto convertToDto(FeedItem item) {
        FeedItemDto dto = new FeedItemDto();
        dto.setAppUserId(item.getAppUserId());
        dto.setAuthor(item.getAuthor());
        dto.setDescription(item.getDescription());
        dto.setDescriptionType(item.getDescriptionType());
        dto.setFeedId(item.getFeedId());
        dto.setId(item.getId());
        dto.setImportedDate(item.getImportedDate());
        dto.setLink(item.getLink());
        dto.setPublishedDate(item.getPublishedDate());
        dto.setTitle(item.getTitle());
        dto.setUpdatedDate(item.getUpdatedDate());
        dto.setUri(item.getUri());

        Article article = item.getArticle();
        if (article != null) {
            dto.setArticle(convertToDto(article));
        }
        // Not very efficient. Maybe Objectify caches it (should do so)
        Feed parentFeed = ofy().load().type(Feed.class).id(item.getFeedId()).now();
        if (parentFeed != null) {
            dto.setFeedName(parentFeed.getName());
        }
        return dto;
    }

    public static ArticleDto convertToDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setAuthor(article.getAuthor());
        dto.setContent(article.getContent());
        dto.setDate(article.getDate());
        dto.setEffectiveUrl(article.getEffectiveUrl());
        dto.setExcerpt(article.getExcerpt());
        dto.setLanguage(article.getLanguage());
        dto.setTitle(article.getTitle());
        dto.setUrl(article.getUrl());
        return dto;
    }

    public static List<FeedItemDto> convertToDtos(List<FeedItem> items) {
        List<FeedItemDto> dtos = new ArrayList<>();
        for (FeedItem item : items) {
            dtos.add(convertToDto(item));
        }
        return dtos;
    }

}
