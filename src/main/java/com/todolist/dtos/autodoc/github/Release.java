package com.todolist.dtos.autodoc.github;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "url",
        "assets_url",
        "upload_url",
        "html_url",
        "id",
        "author",
        "node_id",
        "tag_name",
        "target_commitish",
        "name",
        "draft",
        "prerelease",
        "created_at",
        "published_at",
        "assets",
        "tarball_url",
        "zipball_url",
        "body"
})
@Generated("jsonschema2pojo")
@Getter
public class Release {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("url")
    private String url;
    @JsonProperty("assets_url")
    private String assetsUrl;
    @JsonProperty("upload_url")
    private String uploadUrl;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("author")
    private Owner author;
    @JsonProperty("node_id")
    private String nodeId;
    @JsonProperty("tag_name")
    private String tagName;
    @JsonProperty("target_commitish")
    private String targetCommitish;
    @JsonProperty("name")
    private String name;
    @JsonProperty("draft")
    private Boolean draft;
    @JsonProperty("prerelease")
    private Boolean prerelease;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("published_at")
    private String publishedAt;
    @JsonProperty("assets")
    private List<Object> assets = null;
    @JsonProperty("tarball_url")
    private String tarballUrl;
    @JsonProperty("zipball_url")
    private String zipballUrl;
    @JsonProperty("body")
    private String body;

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
