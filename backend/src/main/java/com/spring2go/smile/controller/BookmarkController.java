package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.dto.BookmarkByTagDto;
import com.spring2go.bookmarker.dto.BookmarkDto;
import com.spring2go.bookmarker.dto.BookmarkListDto;
import com.spring2go.bookmarker.service.BookmarkService;
import com.spring2go.bookmarker.common.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/bookmarks")
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping
    public Result<BookmarkListDto> getAllBookmarks(@RequestParam(name = "userId", required = false) String userId) {
        BookmarkListDto bookmarkListDto = null;
        if (userId == null) {
            bookmarkListDto = bookmarkService.getAllBookmarks();
        } else {
            bookmarkListDto = bookmarkService.getBookmarksByUser(userId);
        }
        return Result.success(bookmarkListDto);
    }

    @GetMapping("/tagged/{tag}")
    public Result<BookmarkByTagDto> getBookmarksByTag(@PathVariable("tag") String tag) {
        BookmarkByTagDto bookmarkByTagDto =  bookmarkService.getBookmarksByTag(tag);
        return Result.success(bookmarkByTagDto);
    }

    @GetMapping("/{id}")
    public Result<BookmarkDto> getBookmarkById(@PathVariable String id) {
        BookmarkDto bookmarkDto = bookmarkService.getBookmarkById(id);
        if (bookmarkDto != null) return Result.success(bookmarkDto);
        return Result.fail(ResultCode.NOT_FOUND, "未找到指定id的收藏, id = " + id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<BookmarkDto> createBookmark(@RequestBody BookmarkDto bookmarkDto) {
        bookmarkDto.setCreatedUserId(SecurityUtils.loginUser().getId());
        bookmarkDto.setCreatedUserName(SecurityUtils.loginUser().getName());
        BookmarkDto createdBookmarkDto = bookmarkService.createBookmark(bookmarkDto);
        return Result.success(createdBookmarkDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result deleteBookmark(@PathVariable String id) {
        BookmarkDto bookmarkDto = bookmarkService.getBookmarkById(id);
        if (bookmarkDto == null) {
            throw new ServiceException(ResultCode.NOT_FOUND, "未找到该收藏, id = " + id);
        }
        if (!StringUtils.equals(bookmarkDto.getCreatedUserId(), SecurityUtils.loginUser().getId()) &&
                !SecurityUtils.isCurrentAdmin()) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "无权限删除他人的收藏, id = " + id);
        }
        bookmarkService.deleteBookmark(id);
        return Result.success();
    }
}
