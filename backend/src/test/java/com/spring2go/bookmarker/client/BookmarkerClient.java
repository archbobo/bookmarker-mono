package com.spring2go.bookmarker.client;

import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.dto.*;
import com.spring2go.bookmarker.model.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "bookmarker-service", path = "/api", url = "http://localhost:8080")
public interface BookmarkerClient {
    @GetMapping("/users/{id}")
    Result<UserDto> getUser(@PathVariable String id);

    @PostMapping("/users")
    Result<UserDto> createUser(@RequestBody @Valid CreateUserRequest createUserRequest);

    @PutMapping("/users/{id}")
    Result<UserDto> updateUser(@PathVariable String id, @RequestBody @Valid UserDto user);

    @DeleteMapping("/users/{id}")
    Result deleteUser(@PathVariable String id);

    @PostMapping("/users/change-password")
    Result<UserDto> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest);

    @GetMapping("/tags")
    Result<List<Tag>> tags();

    @PostMapping(value = {"/auth/login"})
    Result<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest credentials);

    @PostMapping(value = {"/auth/refresh"})
    Result<AuthenticationResponse> refreshAuthenticationToken();

    @GetMapping("/me")
    Result<UserDto> me();

    @GetMapping("/bookmarks")
    Result<BookmarkListDto> getAllBookmarks(@RequestParam(name = "userId", required = false) String userId);

    @GetMapping("/bookmarks/tagged/{tag}")
    Result<BookmarkByTagDto> getBookmarksByTag(@PathVariable("tag") String tag);

    @GetMapping("/bookmarks/{id}")
    Result<BookmarkDto> getBookmarkById(@PathVariable String id);

    @PostMapping("/bookmarks")
    Result<BookmarkDto> createBookmark(@RequestBody BookmarkDto bookmarkDto);

    @DeleteMapping("/bookmarks/{id}")
    Result deleteBookmark(@PathVariable String id);
}
