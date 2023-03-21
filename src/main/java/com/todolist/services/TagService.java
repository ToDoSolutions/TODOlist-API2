package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Group;
import com.todolist.entity.Tag;
import com.todolist.repositories.TagRepositoty;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    // Constants --------------------------------------------------------------
    public static final String WORKSPACE_ID = "{workspaceId}";
    public static final String TAG_ID = "{tagId}";
    public static final String X_API_KEY = "X-Api-Key";
    // Repositories -----------------------------------------------------------
    private final TagRepositoty tagRepositoty;
    // Components -------------------------------------------------------------
    private final FetchApiData fetchApiData;
    @Value("${clockify.api.token}")
    private String token;
    // Urls -------------------------------------------------------------------
    @Value("${clockify.api.url.tags}")
    private String tagsUrl;

    @Autowired
    public TagService(TagRepositoty tagRepositoty, FetchApiData fetchApiData) {
        this.tagRepositoty = tagRepositoty;
        this.fetchApiData = fetchApiData;
    }

    // Finders ----------------------------------------------------------------
    Tag getTagById(Group group, String idTag) {
        return tagRepositoty.findByClockifyId(idTag).orElse(getTagFromClockify(group, idTag));
    }

    private Tag getTagFromClockify(Group group, String idTag) {
        String url = tagsUrl.replace(WORKSPACE_ID, group.getWorkSpaceId()).replace(TAG_ID, idTag);
        Tag tag = fetchApiData.getApiDataWithToken(url, Tag.class, new Pair<>(X_API_KEY, token));
        tagRepositoty.save(tag);
        return tag;
    }
}
