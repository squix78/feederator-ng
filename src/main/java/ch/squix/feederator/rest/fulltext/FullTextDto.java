package ch.squix.feederator.rest.fulltext;

import lombok.Data;

@Data
public class FullTextDto {

    private String originalUrl;
    private String fullText;
    private String imageUrl;

}
