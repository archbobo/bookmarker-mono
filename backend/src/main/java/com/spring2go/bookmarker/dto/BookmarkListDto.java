package com.spring2go.bookmarker.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class BookmarkListDto {
    private List<BookmarkDto> bookmarkList = new ArrayList<>();
}
