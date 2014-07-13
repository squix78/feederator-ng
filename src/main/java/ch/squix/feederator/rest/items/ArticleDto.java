package ch.squix.feederator.rest.items;

import java.util.Date;

import lombok.Data;

@Data
public class ArticleDto {

    private String title;
    private String excerpt;
    private Date date;
    private String author;
    private String language;
    private String url;
    private String effectiveUrl;
    private String content;
}
