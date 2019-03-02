package org.activiti.cloud.query.controller;

import com.querydsl.core.types.Predicate;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.api.task.model.Task;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.ProcessDefinitionRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.TaskVariableRepository;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.query.model.TaskVariableEntity;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.activiti.core.common.spring.security.policies.SecurityPoliciesManager;
import org.activiti.core.common.spring.security.policies.conf.SecurityPoliciesProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(secure = false)
@SpringBootTest
public class CustomControllerTest {

    private static final String TASK_ALFRESCO_IDENTIFIER = "task-alfresco";
    private static final String TASK_IDENTIFIER = "task";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private EntityFinder entityFinder;

    @MockBean
    private TaskLookupRestrictionService taskLookupRestrictionService;

    @MockBean
    private SecurityManager securityManager;

    @MockBean
    private SecurityPoliciesManager securityPoliciesManager;

    @MockBean
    private ProcessDefinitionRepository processDefinitionRepository;

    @MockBean
    private SecurityPoliciesProperties securityPoliciesProperties;

    @MockBean
    private TaskVariableRepository variableRepository;

    @Test
    public void testABPMNendpoint() throws Exception {

        //when
        MvcResult result = mockMvc.perform(get("/abpm/test"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Greetings from Spring Boot!");
    }

    @Test
    public void taskFinderTest() throws Exception {
        //given
        TaskEntity taskEntity = buildDefaultTask();
        given(entityFinder.findById(eq(taskRepository),
                eq(taskEntity.getId()),
                anyString()))
                .willReturn(taskEntity);

        Predicate restrictionPredicate = mock(Predicate.class);
        given(taskLookupRestrictionService.restrictTaskQuery(any())).willReturn(restrictionPredicate);
        given(taskRepository.findAll(restrictionPredicate)).willReturn(Collections.singletonList(taskEntity));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/abpm/{taskId}",
                taskEntity.getId()).accept(MediaType.APPLICATION_JSON_VALUE))
                //then
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult).isNotNull();
    }

    @Test
    public void taskFinderTaskPaged() throws Exception {
        //given

        PageRequest pageRequest = PageRequest.of(1,10);

        TaskEntity taskEntity = buildDefaultTask();

        given(taskRepository.findAll(any(), eq(pageRequest)))
                .willReturn(new PageImpl<>(Collections.singletonList(taskEntity), pageRequest, 11));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/abpm/{taskId}/new?page=1&size=10",
                taskEntity.getId())
                .accept(MediaTypes.HAL_JSON_VALUE))
                //then
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult).isNotNull();
    }

    @Test
    public void taskFinder() throws Exception {
        //given

        PageRequest pageRequest = PageRequest.of(1,10);

        TaskEntity taskEntity = buildDefaultTask();

        given(taskRepository.findAll(any(), eq(pageRequest)))
                .willReturn(new PageImpl<>(Collections.singletonList(taskEntity), pageRequest, 11));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/abpm/tasks?page=1&size=10",
                taskEntity.getId())
                .accept(MediaTypes.HAL_JSON_VALUE))
                //then
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult).isNotNull();
    }

    @Test
    public void variableFinderTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1,10);

        TaskVariableEntity variableEntity = buildVariable(1L);

        given(variableRepository.findAll(any(), eq(pageRequest)))
                .willReturn(new PageImpl<>(Collections.singletonList(variableEntity), pageRequest, 11));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/abpm/{taskId}/variables?page=1&size=10",
                variableEntity.getTaskId())
                .accept(MediaTypes.HAL_JSON_VALUE))
                //then
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult).isNotNull();

    }

    private TaskEntity buildDefaultTask() {
        return new TaskEntity(UUID.randomUUID().toString(),
                "john",
                "Review",
                "Review the report",
                new Date(),
                new Date(),
                20,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "My app",
                "My app",
                "1",
                null,
                null,
                Task.TaskStatus.ASSIGNED,
                new Date(),
                new Date(),
                "peter",
                null,
                "aFormKey"
        );
    }

    private TaskVariableEntity buildVariable(Long id) {
        TaskVariableEntity variableEntity = new TaskVariableEntity(id,
                String.class.getName(),
                "firstName",
                UUID.randomUUID().toString(),
                "My app",
                "My app",
                "1",
                null,
                null,
                UUID.randomUUID().toString(),
                new Date(),
                new Date(),
                UUID.randomUUID().toString());
        variableEntity.setValue("John");
        return variableEntity;
    }
}