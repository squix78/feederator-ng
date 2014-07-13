package ch.squix.feederator.model;

import java.util.Date;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonProperty;

@Data
public class Article {

    private String title;
    private String excerpt;
    private Date date;
    private String author;
    private String language;
    private String url;

    @JsonProperty("effective_url")
    private String effectiveUrl;
    private String content;

}
