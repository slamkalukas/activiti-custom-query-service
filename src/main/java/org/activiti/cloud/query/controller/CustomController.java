package org.activiti.cloud.query.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.api.task.model.Task;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedResourcesAssembler;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.TaskVariableRepository;
import org.activiti.cloud.services.query.model.QTaskEntity;
import org.activiti.cloud.services.query.model.QTaskVariableEntity;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.query.model.TaskVariableEntity;
import org.activiti.cloud.services.query.resources.TaskResource;
import org.activiti.cloud.services.query.resources.VariableResource;
import org.activiti.cloud.services.query.rest.TaskController;
import org.activiti.cloud.services.query.rest.assembler.TaskResourceAssembler;
import org.activiti.cloud.services.query.rest.assembler.TaskVariableResourceAssembler;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/abpm",
        produces = {
                MediaTypes.HAL_JSON_VALUE,
                MediaType.APPLICATION_JSON_VALUE
        })

public class CustomController {

  private TaskRepository taskRepository;
  private TaskVariableRepository variableRepository;
  private TaskResourceAssembler taskResourceAssembler;
  private EntityFinder entityFinder;
  private TaskLookupRestrictionService taskLookupRestrictionService;
  private SecurityManager securityManager;
  private TaskVariableResourceAssembler variableResourceAssembler;
  private AlfrescoPagedResourcesAssembler<TaskVariableEntity> pagedResourcesAssembler;
  private AlfrescoPagedResourcesAssembler<TaskEntity> pagedTaskResourcesAssembler;
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public CustomController(TaskRepository taskRepository,
                          TaskResourceAssembler taskResourceAssembler,
                          EntityFinder entityFinder,
                          TaskLookupRestrictionService taskLookupRestrictionService,
                          SecurityManager securityManager,
                          TaskVariableRepository variableRepository,
                          TaskVariableResourceAssembler variableResourceAssembler,
                          AlfrescoPagedResourcesAssembler<TaskVariableEntity> pagedResourcesAssembler,
                          AlfrescoPagedResourcesAssembler<TaskEntity> pagedTaskResourcesAssembler){
        this.taskRepository = taskRepository;
        this.taskResourceAssembler = taskResourceAssembler;
        this.entityFinder = entityFinder;
        this.taskLookupRestrictionService = taskLookupRestrictionService;
        this.securityManager = securityManager;
        this.variableRepository = variableRepository;
        this.variableResourceAssembler = variableResourceAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.pagedTaskResourcesAssembler = pagedTaskResourcesAssembler;
    }

    @RequestMapping(value = "/allcases/{status}", method = RequestMethod.GET)
    public PagedResources<TaskResource> getTasks(@PathVariable String status,
                                                 @QuerydslPredicate(root = TaskEntity.class) Predicate predicate,
                                                 Pageable pageable) {

        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);
        Predicate extendedPredicate = predicate;

        BooleanExpression expression = QTaskEntity.taskEntity.status.eq(taskStatus);
        extendedPredicate = (extendedPredicate != null) ? expression.and(extendedPredicate) : expression;

        Page<TaskEntity> tasks = taskRepository.findAll(extendedPredicate,
                pageable);

        return pagedTaskResourcesAssembler.toResource(pageable,
                tasks,
                taskResourceAssembler);
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public PagedResources<TaskResource> getTasksNew(@PathVariable String taskId,
                                                         @QuerydslPredicate(root = TaskEntity.class) Predicate predicate,
                                                         Pageable pageable) {

    QTaskEntity task = QTaskEntity.taskEntity;
    BooleanExpression expression = task.id.eq(taskId);

    Predicate extendedPredicated = expression;
    if (predicate != null) {
        extendedPredicated = expression.and(predicate);
    }

    Page<TaskEntity> tasks = taskRepository.findAll(extendedPredicated,
            pageable);

    return pagedTaskResourcesAssembler.toResource(pageable,
            tasks,
            taskResourceAssembler);
    }

    @RequestMapping(value = "/{taskId}/variables", method = RequestMethod.GET)
    public PagedResources<VariableResource> getVariables2(@PathVariable String taskId,
                                                         @QuerydslPredicate(root = TaskVariableEntity.class) Predicate predicate,
                                                         Pageable pageable) {

    QTaskVariableEntity variable = QTaskVariableEntity.taskVariableEntity;
    BooleanExpression expression = variable.taskId.eq(taskId);

    Predicate extendedPredicated = expression;
    if (predicate != null) {
        extendedPredicated = expression.and(predicate);
    }

    Page<TaskVariableEntity> variables = variableRepository.findAll(extendedPredicated,
            pageable);

    return pagedResourcesAssembler.toResource(pageable,
            variables,
            variableResourceAssembler);
    }
}
