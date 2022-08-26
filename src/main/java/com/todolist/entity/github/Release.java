
package com.todolist.entity.github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class Release {

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
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("assets_url")
    public String getAssetsUrl() {
        return assetsUrl;
    }

    @JsonProperty("assets_url")
    public void setAssetsUrl(String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }

    @JsonProperty("upload_url")
    public String getUploadUrl() {
        return uploadUrl;
    }

    @JsonProperty("upload_url")
    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    @JsonProperty("html_url")
    public String getHtmlUrl() {
        return htmlUrl;
    }

    @JsonProperty("html_url")
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("author")
    public Owner getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(Owner author) {
        this.author = author;
    }

    @JsonProperty("node_id")
    public String getNodeId() {
        return nodeId;
    }

    @JsonProperty("node_id")
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @JsonProperty("tag_name")
    public String getTagName() {
        return tagName;
    }

    @JsonProperty("tag_name")
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @JsonProperty("target_commitish")
    public String getTargetCommitish() {
        return targetCommitish;
    }

    @JsonProperty("target_commitish")
    public void setTargetCommitish(String targetCommitish) {
        this.targetCommitish = targetCommitish;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("draft")
    public Boolean getDraft() {
        return draft;
    }

    @JsonProperty("draft")
    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    @JsonProperty("prerelease")
    public Boolean getPrerelease() {
        return prerelease;
    }

    @JsonProperty("prerelease")
    public void setPrerelease(Boolean prerelease) {
        this.prerelease = prerelease;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("published_at")
    public String getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty("published_at")
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @JsonProperty("assets")
    public List<Object> getAssets() {
        return assets;
    }

    @JsonProperty("assets")
    public void setAssets(List<Object> assets) {
        this.assets = assets;
    }

    @JsonProperty("tarball_url")
    public String getTarballUrl() {
        return tarballUrl;
    }

    @JsonProperty("tarball_url")
    public void setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
    }

    @JsonProperty("zipball_url")
    public String getZipballUrl() {
        return zipballUrl;
    }

    @JsonProperty("zipball_url")
    public void setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
    }

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
