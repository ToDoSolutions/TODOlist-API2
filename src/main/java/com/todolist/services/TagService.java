package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Group;
import com.todolist.entity.Tag;
import com.todolist.repositories.TagRepository;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    // Constants --------------------------------------------------------------
    public static final String WORKSPACE_ID = "{workspaceId}";
    public static final String TAG_ID = "{tagId}";
    public static final String X_API_KEY = "X-Api-Key";
    // Repositories -----------------------------------------------------------
    private final TagRepository tagRepository;
    // Components -------------------------------------------------------------
    private final FetchApiData fetchApiData;
    @Value("${clockify.api.token}")
    private String token;
    // Urls -------------------------------------------------------------------
    @Value("${clockify.api.url.tags}")
    private String tagsUrl;

    @Autowired
    public TagService(TagRepository tagRepository, FetchApiData fetchApiData) {
        this.tagRepository = tagRepository;
        this.fetchApiData = fetchApiData;
    }

    // Finders ----------------------------------------------------------------

    @Transactional
    public Tag getTagFromClockify(Group group, String idTag) {
        if (idTag == null)
            return new Tag();
        String url = tagsUrl.replace(WORKSPACE_ID, group.getWorkSpaceId()).replace(TAG_ID, idTag);
        Tag tag = fetchApiData.getApiDataWithToken(url, Tag.class, new Pair<>(X_API_KEY, token));
        tagRepository.save(tag);
        return tag;
    }
}
