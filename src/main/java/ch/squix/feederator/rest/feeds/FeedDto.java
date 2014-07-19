package ch.squix.feederator.rest.feeds;

import java.util.Date;

import lombok.Data;


@Data
public class FeedDto {

    private Long id;

    private String name;

    private String description;

    private String appUserId;

    private String url;

    private Date lastSuccess;

    private Date lastFail;

    private Date lastUpdate;

}
