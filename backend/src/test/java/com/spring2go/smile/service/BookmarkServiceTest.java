package com.spring2go.bookmarker.service;

import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.BookmarkByTagDto;
import com.spring2go.bookmarker.dto.BookmarkDto;
import com.spring2go.bookmarker.dto.BookmarkListDto;
import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.model.Tag;
import com.spring2go.bookmarker.model.User;
import com.spring2go.bookmarker.repository.BookmarkRepository;
import com.spring2go.bookmarker.repository.RoleRepository;
import com.spring2go.bookmarker.repository.TagRepository;
import com.spring2go.bookmarker.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@ActiveProfiles("test") // Like this
public class BookmarkServiceTest {
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private User user1;
    private User user2;

    private String tagName1 = "Kubernetes";
    private String tagName2 = "Microservice";
    private String tagName3 = "Spring";

    private Tag tag1;
    private Tag tag2;
    private Tag tag3;

    @Before
    public void setUp() {
        roleRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        bookmarkRepository.deleteAll();

        // 添加缺省Role
        Role defaultRole = new Role().setName(Constants.ROLE_USER);
        roleRepository.insert(defaultRole);

        user1 = new User().setName("test_user1").setEmail("test_user1@spring2go.com")
                .setPassword("test_password1");
        user1.getRoleNames().add(defaultRole.getName());
        userRepository.create(user1);

        user2 = new User().setName("test_user2").setEmail("test_user2@spring2go.com")
                .setPassword("test_password2");
        user2.getRoleNames().add(defaultRole.getName());
        userRepository.create(user2);
    }

    @Test
    public void testBookmarkFailurePath() {
        // 创建第一个收藏
        BookmarkDto bookmarkDto1 = new BookmarkDto().setTitle("Understanding Kubernetes Kube-Proxy")
                .setUrl("https://supergiant.io/blog/understanding-kubernetes-kube-proxy/")
                .setCreatedUserName(user1.getName())
                .setCreatedUserId(user1.getId());
        bookmarkDto1.getTags().addAll(Arrays.asList(tagName1, tagName2));

        BookmarkDto createdBookmark1 = bookmarkService.createBookmark(bookmarkDto1);
        assertThat(createdBookmark1).isEqualTo(bookmarkDto1);

        // 通过Id查找收藏, Id不存在
        BookmarkDto foundBookmarkDto =  bookmarkService.getBookmarkById(UUID.randomUUID().toString());
        assertThat(foundBookmarkDto).isNull();

        // 通过userId查找收藏，userId不存在
        BookmarkListDto foundBookmarkListDto =  bookmarkService.getBookmarksByUser(UUID.randomUUID().toString());
        assertThat(foundBookmarkListDto.getBookmarkList()).isEmpty();

        // 通过Tag查找收藏
        try {
            bookmarkService.getBookmarksByTag("not_existing_tag_name");
        } catch (ServiceException se) {
            assertThat(se.getResultCode()).isEqualTo(ResultCode.NOT_FOUND);
        }
    }

    @Test
    public void testBookmarkHappyPath() {
        // 创建第一个收藏
        BookmarkDto bookmarkDto1 = new BookmarkDto().setTitle("Understanding Kubernetes Kube-Proxy")
                .setUrl("https://supergiant.io/blog/understanding-kubernetes-kube-proxy/")
                .setCreatedUserName(user1.getName())
                .setCreatedUserId(user1.getId());
        bookmarkDto1.getTags().addAll(Arrays.asList(tagName1, tagName2));

        BookmarkDto createdBookmark1 = bookmarkService.createBookmark(bookmarkDto1);
        assertThat(createdBookmark1).isEqualTo(bookmarkDto1);

        // 创建第二个收藏
        BookmarkDto bookmarkDto2 = new BookmarkDto().setTitle("Microservices With Spring Boot - Part1")
                .setUrl("https://dzone.com/articles/microservices-with-spring-boot-part-1-getting-star")
                .setCreatedUserName(user2.getName())
                .setCreatedUserId(user2.getId());
        bookmarkDto2.getTags().addAll(Arrays.asList(tagName3, tagName2));
        BookmarkDto createdBookmark2 = bookmarkService.createBookmark(bookmarkDto2);
        assertThat(createdBookmark2).isEqualTo(bookmarkDto2);

        // 创建第三个收藏
        BookmarkDto bookmarkDto3 = new BookmarkDto().setTitle("Microservices With Spring Boot - Part2")
                .setUrl("http://www.springboottutorial.com/creating-microservices-with-spring-boot-part-2-forex-microservice")
                .setCreatedUserName(user2.getName())
                .setCreatedUserId(user2.getId());
        bookmarkDto3.getTags().addAll(Arrays.asList(tagName2, tagName3));

        BookmarkDto createdBookmark3 = bookmarkService.createBookmark(bookmarkDto3);
        assertThat(createdBookmark3).isEqualTo(bookmarkDto3);

        // 查找标签
        tag1 = tagRepository.findByName(tagName1);
        tag2 = tagRepository.findByName(tagName2);
        tag3 = tagRepository.findByName(tagName3);
        assertThat(tag1).isNotNull();
        assertThat(tag2).isNotNull();
        assertThat(tag3).isNotNull();

        // 通过Id查找收藏
        BookmarkDto foundBookmarkDto =  bookmarkService.getBookmarkById(createdBookmark1.getId());
        assertThat(foundBookmarkDto).isEqualTo(bookmarkDto1);

        // 通过Tag查找收藏
        BookmarkByTagDto bookmarkByTagDto = bookmarkService.getBookmarksByTag(tagName1);
        assertThat(bookmarkByTagDto.getName()).isEqualTo(tagName1);
        assertThat(bookmarkByTagDto.getId()).isEqualTo(tag1.getId());
        assertThat(bookmarkByTagDto.getBookmarkList()).containsExactly(bookmarkDto1);

        bookmarkByTagDto = bookmarkService.getBookmarksByTag(tagName2);
        assertThat(bookmarkByTagDto.getName()).isEqualTo(tagName2);
        assertThat(bookmarkByTagDto.getId()).isEqualTo(tag2.getId());
        assertThat(bookmarkByTagDto.getBookmarkList()).containsExactly(bookmarkDto3, bookmarkDto2, bookmarkDto1);

        // 通过UserId查找收藏
        BookmarkListDto bookmarkListDto = bookmarkService.getBookmarksByUser(user1.getId());
        assertThat(bookmarkListDto.getBookmarkList().size()).isEqualTo(1);
        assertThat(bookmarkListDto.getBookmarkList()).containsExactly(bookmarkDto1);

        bookmarkListDto = bookmarkService.getBookmarksByUser(user2.getId());
        assertThat(bookmarkListDto.getBookmarkList().size()).isEqualTo(2);
        assertThat(bookmarkListDto.getBookmarkList()).containsExactly(bookmarkDto3, bookmarkDto2);

        // 获取所有收藏
        bookmarkListDto = bookmarkService.getAllBookmarks();
        assertThat(bookmarkListDto.getBookmarkList().size()).isEqualTo(3);
        assertThat(bookmarkListDto.getBookmarkList()).containsExactly(bookmarkDto3, bookmarkDto2, bookmarkDto1);

        // 删除一个收藏
        bookmarkService.deleteBookmark(bookmarkDto1.getId());
        bookmarkListDto = bookmarkService.getAllBookmarks();
        assertThat(bookmarkListDto.getBookmarkList().size()).isEqualTo(2);
        assertThat(bookmarkListDto.getBookmarkList()).containsExactly(bookmarkDto3, bookmarkDto2);

        // 全部删除
        bookmarkService.deleteBookmark(bookmarkDto2.getId());
        bookmarkService.deleteBookmark(bookmarkDto3.getId());
        bookmarkListDto = bookmarkService.getAllBookmarks();
        assertThat(bookmarkListDto.getBookmarkList()).isEmpty();
    }

    @After
    public void destroy() {
        roleRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        bookmarkRepository.deleteAll();
    }
}
