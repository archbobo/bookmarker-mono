package com.spring2go.bookmarker.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BookmarkByTagDto {
    private String id;
    private String name;
    private List<BookmarkDto> bookmarkList;
}
