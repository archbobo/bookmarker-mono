package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.AbstractControllerTest;
import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.*;
import com.spring2go.bookmarker.model.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class BookmarkerControllerTest extends AbstractControllerTest {

    String email = "test_user@spring2go.com";
    String password = "test_pass";

    UserDto userDto = null;

    @Before
    public void setUp() {
        // 预设置缺省角色
        Role userRole = new Role().setName(Constants.ROLE_USER);
        roleRepository.insert(userRole);

        Role adminRole = new Role().setName(Constants.ROLE_ADMIN);
        roleRepository.insert(adminRole);

        // 创建新用户
        CreateUserRequest createUserRequest = new CreateUserRequest()
                .setName("test_user").setPassword(password).setEmail(email);
        Result<UserDto> result = bookmarkerClient.createUser(createUserRequest);
        assertThat(result.isSuccess()).isTrue();
        userDto = result.getData();
        assertThat(userDto.getName()).isEqualTo(createUserRequest.getName());
        assertThat(userDto.getEmail()).isEqualTo(createUserRequest.getEmail());
        assertThat(userDto.getRoleNames()).contains(Constants.ROLE_USER);

        // 清除之前登录
        logout();
    }

    @Test
    public void testBookmarker() {
        // 先登录
        login(email, password);

        // 创建第一个收藏
        BookmarkDto bookmarkDto1 = new BookmarkDto()
                .setTitle("test_title1").setUrl("http://spring2go.com/test1");
        bookmarkDto1.getTags().addAll(Arrays.asList("tag1", "tag2"));
        Result<BookmarkDto> createBookmarkResult1 = bookmarkerClient.createBookmark(bookmarkDto1);
        assertThat(createBookmarkResult1.isSuccess()).isTrue();
        BookmarkDto createdBookmarkDto1 = createBookmarkResult1.getData();
        assertThat(createdBookmarkDto1.getTitle()).isEqualTo(bookmarkDto1.getTitle());
        assertThat(createdBookmarkDto1.getUrl()).isEqualTo(bookmarkDto1.getUrl());
        assertThat(createdBookmarkDto1.getTags()).isEqualTo(bookmarkDto1.getTags());
        assertThat(createdBookmarkDto1.getCreatedUserId()).isEqualTo(userDto.getId());
        assertThat(createdBookmarkDto1.getCreatedUserName()).isEqualTo(userDto.getName());

        // 创建第二个收藏
        BookmarkDto bookmarkDto2 = new BookmarkDto()
                .setTitle("test_title2").setUrl("http://spring2go.com/test2");
        bookmarkDto2.getTags().addAll(Arrays.asList("tag2", "tag3"));
        Result<BookmarkDto> createBookmarkResult2 = bookmarkerClient.createBookmark(bookmarkDto2);
        assertThat(createBookmarkResult2.isSuccess()).isTrue();
        BookmarkDto createdBookmarkDto2 = createBookmarkResult2.getData();

        // 查找第一个收藏
        Result<BookmarkDto>  getBookmarkByIdResult = bookmarkerClient.getBookmarkById(createdBookmarkDto1.getId());
        assertThat(getBookmarkByIdResult.isSuccess()).isTrue();
        BookmarkDto foundBookmarkDto1 = getBookmarkByIdResult.getData();
        assertThat(foundBookmarkDto1).isEqualTo(createdBookmarkDto1);

        // 查询所有收藏，不提供userId
        Result<BookmarkListDto> getAllBookmarksResult = bookmarkerClient.getAllBookmarks(null);
        assertThat(getAllBookmarksResult.isSuccess()).isTrue();
        BookmarkListDto bookmarkListDto = getAllBookmarksResult.getData();
        assertThat(bookmarkListDto.getBookmarkList()).containsExactlyInAnyOrder(createdBookmarkDto1, createdBookmarkDto2);

        // 查询所有收藏，提供userId
        getAllBookmarksResult = bookmarkerClient.getAllBookmarks(userDto.getId());
        assertThat(getAllBookmarksResult.isSuccess()).isTrue();
        bookmarkListDto = getAllBookmarksResult.getData();
        assertThat(bookmarkListDto.getBookmarkList()).containsExactlyInAnyOrder(createdBookmarkDto1, createdBookmarkDto2);

        // 根据tag1查询收藏
        Result<BookmarkByTagDto> getBookmarksByTagResult = bookmarkerClient.getBookmarksByTag("tag1");
        assertThat(getAllBookmarksResult.isSuccess()).isTrue();
        BookmarkByTagDto bookmarkByTagDto = getBookmarksByTagResult.getData();
        assertThat(bookmarkByTagDto.getName()).isEqualTo("tag1");
        assertThat(bookmarkByTagDto.getBookmarkList()).containsExactlyInAnyOrder(createdBookmarkDto1);

        // 根据tag2查询收藏
        getBookmarksByTagResult = bookmarkerClient.getBookmarksByTag("tag2");
        assertThat(getAllBookmarksResult.isSuccess()).isTrue();
        bookmarkByTagDto = getBookmarksByTagResult.getData();
        assertThat(bookmarkByTagDto.getName()).isEqualTo("tag2");
        assertThat(bookmarkByTagDto.getBookmarkList()).containsExactlyInAnyOrder(createdBookmarkDto1, createdBookmarkDto2);

        // 删除收藏1
        Result deleteBookmarkResult = bookmarkerClient.deleteBookmark(createdBookmarkDto1.getId());
        assertThat(deleteBookmarkResult.isSuccess()).isTrue();

        // 根据tag1查询收藏
        getBookmarksByTagResult = bookmarkerClient.getBookmarksByTag("tag1");
        assertThat(getAllBookmarksResult.isSuccess()).isTrue();
        bookmarkByTagDto = getBookmarksByTagResult.getData();
        assertThat(bookmarkByTagDto.getName()).isEqualTo("tag1");
        assertThat(bookmarkByTagDto.getBookmarkList()).isEmpty();

        // 根据tag2查询收藏
        getBookmarksByTagResult = bookmarkerClient.getBookmarksByTag("tag2");
        assertThat(getAllBookmarksResult.isSuccess()).isTrue();
        bookmarkByTagDto = getBookmarksByTagResult.getData();
        assertThat(bookmarkByTagDto.getName()).isEqualTo("tag2");
        assertThat(bookmarkByTagDto.getBookmarkList()).containsExactlyInAnyOrder(createdBookmarkDto2);
    }

    @After
    public void destroy() {
        logout();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }
}
