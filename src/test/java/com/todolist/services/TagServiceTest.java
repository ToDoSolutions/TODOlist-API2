package com.todolist.services;

import com.todolist.component.FetchApiData;
import com.todolist.entity.Group;
import com.todolist.entity.Tag;
import com.todolist.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import static org.mockito.Mockito.*;

class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private FetchApiData fetchApiData;

    @Value("${clockify.api.token}")
    private String token;

    @Value("${clockify.api.url.tags}")
    private String tagsUrl;

    @Test
    void testGetTagFromClockify() {
        Group group = new Group();
        group.setWorkSpaceId("workspaceId");
        String idTag = "tagId";
        String url = tagsUrl.replace(TagService.WORKSPACE_ID, group.getWorkSpaceId()).replace(TagService.TAG_ID, idTag);

        Tag tag = new Tag();
        when(fetchApiData.getApiDataWithToken(eq(url), eq(Tag.class), any())).thenReturn(tag);

        tagService.getTagFromClockify(group, idTag);

        verify(fetchApiData, times(1)).getApiDataWithToken(eq(url), eq(Tag.class), any());
        verify(tagRepository, times(1)).save(tag);
    }

    // Add more test methods for other service methods
}

