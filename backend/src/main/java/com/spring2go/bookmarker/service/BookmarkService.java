package com.spring2go.bookmarker.service;

import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.dto.BookmarkByTagDto;
import com.spring2go.bookmarker.dto.BookmarkDto;
import com.spring2go.bookmarker.dto.BookmarkListDto;
import com.spring2go.bookmarker.model.Bookmark;
import com.spring2go.bookmarker.model.Tag;
import com.spring2go.bookmarker.model.User;
import com.spring2go.bookmarker.repository.BookmarkRepository;
import com.spring2go.bookmarker.repository.TagRepository;
import com.spring2go.bookmarker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BookmarkService {
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public BookmarkListDto getAllBookmarks() {
        log.debug("process=get_all_bookmarks");
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Bookmark> bookmarkList = bookmarkRepository.findAll(sort);
        List<BookmarkDto> bookmarkDtoList = buildBookmarksResult(bookmarkList);
        return new BookmarkListDto().setBookmarkList(bookmarkDtoList);
    }

    @Transactional(readOnly = true)
    public BookmarkListDto getBookmarksByUser(String userId) {
        log.debug("process=get_bookmarks_by_user_id, user_id=" + userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Bookmark> bookmarkList = bookmarkRepository.findByCreatedById(userId, sort);
        List<BookmarkDto> bookmarkDtoList = buildBookmarksResult(bookmarkList);
        return new BookmarkListDto().setBookmarkList(bookmarkDtoList);
    }

    @Transactional(readOnly = true)
    public BookmarkByTagDto getBookmarksByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            throw new ServiceException(ResultCode.NOT_FOUND, "Tag " + tagName + " not Found");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Bookmark> bookmarkList = bookmarkRepository.findByTag(tagName, sort);
        List<BookmarkDto> bookmarkDtoList = buildBookmarksResult(bookmarkList);
        return new BookmarkByTagDto()
                .setId(tag.getId()).setName(tag.getName())
                .setBookmarkList(bookmarkDtoList);
    }

    @Transactional(readOnly = true)
    public BookmarkDto getBookmarkById(String id) {
        log.debug("process=get_bookmark_by_id, id" + id);
        Bookmark bookmark = bookmarkRepository.findById(id);
        if (bookmark == null) return null;
        return buildBookmarkResult(bookmark);
    }

    public BookmarkDto createBookmark(BookmarkDto bookmarkDto) {
        log.debug("process=create_bookmark, url=" + bookmarkDto.getUrl());
        Bookmark bookmark = convert2Model(bookmarkDto);
        Bookmark createdBookmark = bookmarkRepository.insert(bookmark);
        return buildBookmarkResult(createdBookmark);
    }

    public void deleteBookmark(String id) {
        log.debug("process=delete_bookmark_by_id, id=" + id);
        bookmarkRepository.deleteById(id);
    }

    private BookmarkDto buildBookmarkResult(Bookmark bookmark) {
        BookmarkDto bookmarkDto = convert2Dto(bookmark);
        populateUserName(bookmarkDto);
        return bookmarkDto;
    }

    private List<BookmarkDto> buildBookmarksResult(List<Bookmark> bookmarkList) {
        List<BookmarkDto> bookmarkDtoList = bookmarkList.stream().map(bookmark -> {
            return convert2Dto(bookmark);
        }).collect(Collectors.toList());
        populateUserNames(bookmarkDtoList);
        return bookmarkDtoList;
    }

    private BookmarkDto convert2Dto(Bookmark bookmark) {
        BookmarkDto bookmarkDto = new BookmarkDto();
        bookmarkDto.setId(bookmark.getId());
        bookmarkDto.setCreatedAt(bookmark.getCreatedAt());
        bookmarkDto.setCreatedUserId(bookmark.getCreatedBy());
        bookmarkDto.getTags().addAll(bookmark.getTags());
        bookmarkDto.setUpdatedAt(bookmark.getUpdatedAt());
        bookmarkDto.setUrl(bookmark.getUrl());
        bookmarkDto.setTitle(bookmark.getTitle());
        return bookmarkDto;
    }

    private void populateUserName(BookmarkDto bookmarkDto) {
        User user = userRepository.findById(bookmarkDto.getCreatedUserId());
        if (user == null) return;
        bookmarkDto.setCreatedUserName(user.getName());
    }

    private void populateUserNames(List<BookmarkDto> bookmarkDtoList) {
        List<String> userIdList =
                bookmarkDtoList.stream().map(bookmarkDto -> bookmarkDto.getCreatedUserId()).collect(Collectors.toList());
        if (userIdList.isEmpty()) return;
        List<User> userList = userRepository.findByIds(userIdList);
        Map<String, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        bookmarkDtoList.forEach(bookmarkDto -> {
            User user = userMap.get(bookmarkDto.getCreatedUserId());
            bookmarkDto.setCreatedUserName(user.getName());
        });
    }

    private Bookmark convert2Model(BookmarkDto bookmarkDto) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUrl(bookmarkDto.getUrl());
        bookmark.setTitle(bookmarkDto.getTitle());
        if (StringUtils.isBlank(bookmark.getTitle())) {
            try {
                Document document = Jsoup.connect(bookmark.getUrl()).get();
                bookmark.setTitle(document.title());
            } catch (IOException e) {
                log.error("fail to get title @ url " + bookmarkDto.getUrl(), e);
            }
        }
        bookmark.setCreatedBy(bookmarkDto.getCreatedUserId());
        bookmarkDto.getTags().forEach(
                tagName -> {
                    if (StringUtils.isNotBlank(tagName)) {
                        Tag tag = createTagIfNotExist(tagName);
                        bookmark.getTags().add(tag.getName());
                    }
                }
        );
        return bookmark;
    }

    private Tag createTagIfNotExist(String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        if (tag != null) return tag;
        tag = new Tag().setName(tagName);
        return tagRepository.insert(tag);
    }
}
