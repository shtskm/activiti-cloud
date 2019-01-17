/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.organization.entity;

import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.activiti.cloud.organization.api.Extensions;
import org.activiti.cloud.organization.api.Model;
import org.activiti.cloud.organization.core.rest.client.model.ModelReference;
import org.activiti.cloud.services.organization.jpa.audit.AuditableEntity;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Model model entity
 */
@Entity(name = "Model")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class ModelEntity extends AuditableEntity<String> implements Model<ProjectEntity, String> {

    @Id
    private String id;

    @ManyToOne
    private ProjectEntity project;

    private String type;

    @Transient
    @JsonIgnore
    private ModelReference data;

    @Transient
    private Extensions extensions;

    public ModelEntity() { // for JPA
        this.data = new ModelReference();
    }

    public ModelEntity(String id,
                       String name,
                       String type) {
        this.id = id;
        this.type = type;
        this.data = new ModelReference(id,
                                       name);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
        data.setModelId(id);
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public void setName(String name) {
        data.setName(name);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public ModelReference getData() {
        return data;
    }

    public void setData(ModelReference data) {
        this.data = data;
    }

    @Override
    @JsonIgnore
    public ProjectEntity getProject() {
        return project;
    }

    @Override
    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @Transient
    @JsonProperty("projectId")
    public String projectId() {
        return Optional.ofNullable(project)
                .map(ProjectEntity::getId)
                .orElse(null);
    }

    @Override
    public String getVersion() {
        return data.getVersion();
    }

    @Override
    public void setVersion(String version) {
        data.setVersion(version);
    }

    @Override
    @JsonIgnore
    public String getContentType() {
        return data.getContentType();
    }

    @Override
    public void setContentType(String contentType) {
        data.setContentType(contentType);
    }

    @Override
    @JsonIgnore
    public String getContent() {
        return data.getContent();
    }

    @Override
    public void setContent(String content) {
        data.setContent(content);
    }

    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }
}
