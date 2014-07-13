package ch.squix.feederator.rest.items;

import java.util.Date;

import lombok.Data;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Data
public class FeedItemDto {

    @Id
    private Long id;

    @Index
    private Long feedId;

    @Index
    private String appUserId;

    private String uri;

    private String link;
    private Date publishedDate;
    private Date updatedDate;

    @Index
    private Date importedDate;

    private String title;
    private String description;
    private String descriptionType;
    private String author;

    // Derived
    private String feedName;

    private ArticleDto article;

}
