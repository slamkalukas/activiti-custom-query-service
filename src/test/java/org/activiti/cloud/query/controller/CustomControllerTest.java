package org.activiti.cloud.query.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.api.task.model.Task;
import org.activiti.cloud.query.AbpmTaskRepository;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.ProcessDefinitionRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.TaskVariableRepository;
import org.activiti.cloud.services.query.model.QTaskEntity;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.query.model.TaskVariableEntity;
import org.activiti.cloud.services.security.ActivitiForbiddenException;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.activiti.core.common.spring.security.policies.SecurityPoliciesManager;
import org.activiti.core.common.spring.security.policies.conf.SecurityPoliciesProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
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
    private AbpmTaskRepository abpmTaskRepository;

    @MockBean
    private TaskVariableRepository variableRepository;

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

    @Before
    @DirtiesContext
    public void setUp() {
        assertThat(variableRepository).isNotNull();
        assertThat(entityFinder).isNotNull();
        assertThat(taskLookupRestrictionService).isNotNull();
        assertThat(securityManager).isNotNull();
        assertThat(securityPoliciesManager).isNotNull();
        assertThat(processDefinitionRepository).isNotNull();
        assertThat(securityPoliciesProperties).isNotNull();
        assertThat(abpmTaskRepository).isNotNull();
    }

    @Test
    public void allTaskFinderWithoutStatus() throws Exception {
        //given

        PageRequest pageRequest = PageRequest.of(1,100);

        TaskEntity taskEntity1 = buildDefaultTaskSimpleId("123456", Task.TaskStatus.ASSIGNED);
        TaskEntity taskEntity2 = buildDefaultTaskSimpleId("654321", null);

        given(abpmTaskRepository.findAll(QTaskEntity.taskEntity.status.eq(Task.TaskStatus.ASSIGNED), eq(pageRequest)))
                .willReturn(new PageImpl<>(List.of(taskEntity1, taskEntity2), pageRequest, 11));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/abpm/allcases?page=1&size=100")
                .accept(MediaTypes.HAL_JSON_VALUE))
                //then
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult).isNotNull();
    }

    @Test
    public void allTaskFinder() throws Exception {
        //given

        PageRequest pageRequest = PageRequest.of(1,100);

        TaskEntity taskEntity1 = buildDefaultTaskSimpleId("123456", Task.TaskStatus.ASSIGNED);
        TaskEntity taskEntity2 = buildDefaultTaskSimpleId("654321", null);

        when(abpmTaskRepository.findAll(any(), eq(pageRequest)))
                .thenReturn(new PageImpl<>(List.of(taskEntity1, taskEntity2), pageRequest, 11));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/abpm/allcases?page=1&size=100")
                .accept(MediaTypes.HAL_JSON_VALUE))
                //then
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult).isNotNull();
    }

    private TaskEntity buildDefaultTask() {
        TaskEntity taskEntity = new TaskEntity(UUID.randomUUID().toString(),
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

        Set<TaskVariableEntity> taskVariableEntity = new HashSet<>();
        taskVariableEntity.add(buildVariable(1L));
        taskEntity.setVariables(taskVariableEntity);

        return taskEntity;
    }

    private TaskEntity buildDefaultTaskSimpleId(String number, Task.TaskStatus status) {
        TaskEntity taskEntity = new TaskEntity(
                number,
                "john",
                "Review",
                "Review the report",
                new Date(),
                new Date(),
                20,
                number,
                number,
                "My app",
                "My app",
                "1",
                null,
                null,
                status,
                new Date(),
                new Date(),
                "peter",
                null,
                "aFormKey"
        );

        Set<TaskVariableEntity> taskVariableEntity = new HashSet<>();
        taskVariableEntity.add(buildVariableSimpleId(123456L));
        taskVariableEntity.add(buildVariableSimpleId(654321L));
        taskEntity.setVariables(taskVariableEntity);

        return taskEntity;
    }

    private TaskVariableEntity buildVariable(Long id) {
        TaskVariableEntity variableEntity = new TaskVariableEntity(
                id,
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

    private TaskVariableEntity buildVariableSimpleId(Long id) {
        TaskVariableEntity variableEntity = new TaskVariableEntity(
                id,
                String.class.getName(),
                "firstName",
                id.toString(),
                "My app",
                "My app",
                "1",
                null,
                null,
                id.toString(),
                new Date(),
                new Date(),
                id.toString());
        variableEntity.setValue("John");
        return variableEntity;
    }

    @ExceptionHandler(ActivitiForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAppException(ActivitiForbiddenException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAppException(IllegalStateException ex) {
        return ex.getMessage();
    }
}