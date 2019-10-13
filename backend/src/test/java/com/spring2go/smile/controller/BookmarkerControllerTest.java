package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.AbstractControllerTest;
import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.*;
import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.model.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class BookmarkerControllerTest extends AbstractControllerTest {

    String email1 = "test_user1@spring2go.com";
    String password1 = "test_pass1";

    String email2 = "test_user2@spring2go.com";
    String password2 = "test_pass2";

    UserDto userDto1 = null;
    UserDto userDto2 = null;

    @Before
    public void setUp() {
        // 预设置缺省角色
        Role userRole = new Role().setName(Constants.ROLE_USER);
        roleRepository.insert(userRole);

        Role adminRole = new Role().setName(Constants.ROLE_ADMIN);
        roleRepository.insert(adminRole);

        // 创建新用户1
        CreateUserRequest createUserRequest1 = new CreateUserRequest()
                .setName("test_user1").setPassword(password1).setEmail(email1);
        Result<UserDto> result1 = bookmarkerClient.createUser(createUserRequest1);
        assertThat(result1.isSuccess()).isTrue();
        userDto1 = result1.getData();
        assertThat(userDto1.getName()).isEqualTo(createUserRequest1.getName());
        assertThat(userDto1.getEmail()).isEqualTo(createUserRequest1.getEmail());
        assertThat(userDto1.getRoleNames()).contains(Constants.ROLE_USER);

        // 创建新用户2
        CreateUserRequest createUserRequest2 = new CreateUserRequest()
                .setName("test_user2").setPassword(password2).setEmail(email2);
        Result<UserDto> result2 = bookmarkerClient.createUser(createUserRequest2);
        assertThat(result2.isSuccess()).isTrue();
        userDto2 = result2.getData();
        assertThat(userDto2.getName()).isEqualTo(createUserRequest2.getName());
        assertThat(userDto2.getEmail()).isEqualTo(createUserRequest2.getEmail());
        assertThat(userDto2.getRoleNames()).contains(Constants.ROLE_USER);

        // 清除之前登录
        logout();
    }

    @Test
    public void testBookmarkerFailurePath() {
        // 用户1登录
        login(email1, password1);

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
        assertThat(createdBookmarkDto1.getCreatedUserId()).isEqualTo(userDto1.getId());
        assertThat(createdBookmarkDto1.getCreatedUserName()).isEqualTo(userDto1.getName());

        // 用户2登录
        login(email2, password2);

        // 创建第二个收藏
        BookmarkDto bookmarkDto2 = new BookmarkDto()
                .setTitle("test_title2").setUrl("http://spring2go.com/test2");
        bookmarkDto2.getTags().addAll(Arrays.asList("tag2", "tag3"));
        Result<BookmarkDto> createBookmarkResult2 = bookmarkerClient.createBookmark(bookmarkDto2);
        assertThat(createBookmarkResult2.isSuccess()).isTrue();
        BookmarkDto createdBookmarkDto2 = createBookmarkResult2.getData();

        // 查找不存在收藏
        Result<BookmarkDto> getBookmarkByIdResult = bookmarkerClient.getBookmarkById(UUID.randomUUID().toString());
        assertThat(getBookmarkByIdResult.getCode()).isEqualTo(ResultCode.NOT_FOUND);

        // 删除不存在的收藏
        Result deleteBookmarkResult = bookmarkerClient.deleteBookmark(UUID.randomUUID().toString());
        assertThat(deleteBookmarkResult.getCode()).isEqualTo(ResultCode.NOT_FOUND);

        // 登录用户2不能删除用户1的收藏
        deleteBookmarkResult = bookmarkerClient.deleteBookmark(createdBookmarkDto1.getId());
        assertThat(deleteBookmarkResult.getCode()).isEqualTo(ResultCode.UN_AUTHORIZED);

        // 未登录状态不能删除收藏
        logout();
        deleteBookmarkResult = bookmarkerClient.deleteBookmark(createdBookmarkDto2.getId());
        assertThat(deleteBookmarkResult.getCode()).isEqualTo(ResultCode.UN_AUTHORIZED);
    }

    @Test
    public void testBookmarkerHappyPath() {
        // 用户1登录
        login(email1, password1);

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
        assertThat(createdBookmarkDto1.getCreatedUserId()).isEqualTo(userDto1.getId());
        assertThat(createdBookmarkDto1.getCreatedUserName()).isEqualTo(userDto1.getName());

        // 创建第二个收藏
        BookmarkDto bookmarkDto2 = new BookmarkDto()
                .setTitle("test_title2").setUrl("http://spring2go.com/test2");
        bookmarkDto2.getTags().addAll(Arrays.asList("tag2", "tag3"));
        Result<BookmarkDto> createBookmarkResult2 = bookmarkerClient.createBookmark(bookmarkDto2);
        assertThat(createBookmarkResult2.isSuccess()).isTrue();
        BookmarkDto createdBookmarkDto2 = createBookmarkResult2.getData();

        // 查询标签
        Result<List<Tag>> tagsResult = bookmarkerClient.tags();
        assertThat(tagsResult.isSuccess()).isTrue();
        List<Tag> tagList = tagsResult.getData();
        assertThat(tagList.size()).isEqualTo(3);
        List<String> tagNameList = tagList.stream().map(tag -> tag.getName()).collect(Collectors.toList());
        assertThat(tagNameList).containsExactlyInAnyOrder("tag1", "tag2", "tag3");

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
        getAllBookmarksResult = bookmarkerClient.getAllBookmarks(userDto1.getId());
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
