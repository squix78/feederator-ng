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
public class Feed {

    @Id
    private Long id;

    private String name;

    private String description;

    @Index
    private String appUserId;

    private String url;

    private Date lastSuccess;

    private Date lastFail;

    private Date lastUpdate;

    @Serialize(zip = true)
    private List<String> uuids = new ArrayList<>();

    public boolean containsUuid(String uuid) {
        return uuids.contains(uuid);
    }

}
