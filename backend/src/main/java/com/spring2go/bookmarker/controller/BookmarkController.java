package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.dto.BookmarkByTagDto;
import com.spring2go.bookmarker.dto.BookmarkDto;
import com.spring2go.bookmarker.dto.BookmarkListDto;
import com.spring2go.bookmarker.service.BookmarkService;
import com.spring2go.bookmarker.common.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/bookmarks")
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping
    public BookmarkListDto getAllBookmarks(@RequestParam(name = "userId", required = false) String userId) {
        if (userId == null) {
            return bookmarkService.getAllBookmarks();
        } else {
            return bookmarkService.getBookmarksByUser(userId);
        }
    }

    @GetMapping("/tagged/{tag}")
    public BookmarkByTagDto getBookmarksByTag(@PathVariable("tag") String tag) {
        return bookmarkService.getBookmarksByTag(tag);
    }

    @GetMapping("/{id}")
    ResponseEntity<BookmarkDto> getBookmarkById(@PathVariable String id) {
        BookmarkDto bookmarkDto = bookmarkService.getBookmarkById(id);
        if (bookmarkDto != null) return ResponseEntity.ok(bookmarkDto);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public BookmarkDto createBookmark(@RequestBody BookmarkDto bookmarkDto) {
        bookmarkDto.setCreatedUserId(SecurityUtils.loginUser().getId());
        return bookmarkService.createBookmark(bookmarkDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteBookmark(@PathVariable String id) {
        BookmarkDto bookmarkDto = bookmarkService.getBookmarkById(id);
        if (bookmarkDto == null || (!StringUtils.equals(bookmarkDto.getCreatedUserId(), SecurityUtils.loginUser().getId()) &&
                !SecurityUtils.isCurrentAdmin())) {
            throw new ServiceException(ResultCode.NOT_FOUND, "未找到该收藏, id = " + id);
        }
        bookmarkService.deleteBookmark(id);
    }
}
