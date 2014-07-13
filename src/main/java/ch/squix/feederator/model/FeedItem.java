package ch.squix.feederator.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

@Entity
@Cache
@Data
public class FeedItem {

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

    private Article article;

    @Serialize(zip = true)
    private List<String> links = new ArrayList<>();

    @Serialize(zip = true)
    private List<String> authors = new ArrayList<>();

    @Serialize(zip = true)
    private List<String> contributors = new ArrayList<>();

    @Serialize(zip = true)
    private List<String> categories = new ArrayList<>();


}
