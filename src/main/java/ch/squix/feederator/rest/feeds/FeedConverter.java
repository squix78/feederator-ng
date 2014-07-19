package ch.squix.feederator.rest.feeds;

import java.util.ArrayList;
import java.util.List;

import ch.squix.feederator.model.Feed;


public class FeedConverter {

    public static FeedDto convertToDto(Feed feed) {
        FeedDto dto = new FeedDto();
        dto.setAppUserId(feed.getAppUserId());
        dto.setDescription(feed.getDescription());
        dto.setId(feed.getId());
        dto.setLastFail(feed.getLastFail());
        dto.setLastSuccess(feed.getLastSuccess());
        dto.setLastUpdate(feed.getLastUpdate());
        dto.setName(feed.getName());
        dto.setUrl(feed.getUrl());
        return dto;
    }

    public static List<FeedDto> convertToDtos(List<Feed> feeds) {
        List<FeedDto> dtos = new ArrayList<>();
        for (Feed feed : feeds) {
            dtos.add(convertToDto(feed));
        }
        return dtos;
    }

    public static void convertFromDto(Feed feed, FeedDto dto) {

        feed.setDescription(dto.getDescription());
        feed.setLastFail(dto.getLastFail());
        feed.setLastSuccess(dto.getLastSuccess());
        feed.setLastUpdate(dto.getLastUpdate());
        feed.setName(dto.getName());
        feed.setUrl(dto.getUrl());
    }

}
