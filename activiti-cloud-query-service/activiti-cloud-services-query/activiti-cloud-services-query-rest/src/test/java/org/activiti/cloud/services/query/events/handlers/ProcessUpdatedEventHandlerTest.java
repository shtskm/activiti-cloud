/*
 * Copyright 2017-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cloud.services.query.events.handlers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.activiti.api.process.model.events.ProcessRuntimeEvent;
import org.activiti.api.runtime.model.impl.ProcessInstanceImpl;
import org.activiti.cloud.api.process.model.events.CloudProcessUpdatedEvent;
import org.activiti.cloud.api.process.model.impl.events.CloudProcessUpdatedEventImpl;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.model.ProcessInstanceEntity;
import org.activiti.cloud.services.query.model.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class ProcessUpdatedEventHandlerTest {

    @InjectMocks
    private ProcessUpdatedEventHandler handler;

    @Mock
    private ProcessInstanceRepository processInstanceRepository;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void handleShouldUpdateCurrentProcessInstance() {
        //given
        CloudProcessUpdatedEvent event = buildProcessUpdatedEvent();

        ProcessInstanceEntity currentProcessInstanceEntity = mock(ProcessInstanceEntity.class);
        given(processInstanceRepository.findById(event.getEntity().getId())).willReturn(Optional.of(currentProcessInstanceEntity));

        //when
        handler.handle(event);

        //then
        verify(processInstanceRepository).save(currentProcessInstanceEntity);
        verify(currentProcessInstanceEntity).setBusinessKey(event.getEntity().getBusinessKey());
        verify(currentProcessInstanceEntity).setName(event.getEntity().getName());
        verify(currentProcessInstanceEntity).setLastModified(any(Date.class));
        verifyNoMoreInteractions(currentProcessInstanceEntity);
    }

    private CloudProcessUpdatedEventImpl buildProcessUpdatedEvent() {
        ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
        processInstance.setId(UUID.randomUUID().toString());
        processInstance.setBusinessKey("businesskey");
        processInstance.setName("name");
        return new CloudProcessUpdatedEventImpl(processInstance);
    }

    @Test
    public void handleShouldThrowExceptionWhenRelatedProcessInstanceIsNotFound() {
        //given
        CloudProcessUpdatedEvent event = buildProcessUpdatedEvent();
        String id = event.getEntity().getId();

        given(processInstanceRepository.findById(id)).willReturn(Optional.empty());

        //then
        //when
        assertThatExceptionOfType(QueryException.class)
            .isThrownBy(() -> handler.handle(event))
            .withMessageContaining("Unable to find process instance with the given id: ");
    }

    @Test
    public void getHandledEventShouldReturnProcessUpdatedEvent() {
        //when
        String handledEvent = handler.getHandledEvent();

        //then
        assertThat(handledEvent).isEqualTo(ProcessRuntimeEvent.ProcessEvents.PROCESS_UPDATED.name());
    }
}
