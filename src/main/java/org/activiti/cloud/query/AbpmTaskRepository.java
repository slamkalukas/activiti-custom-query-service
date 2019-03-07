package org.activiti.cloud.query;

import com.querydsl.core.types.dsl.StringPath;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.model.QTaskEntity;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@Repository
public interface AbpmTaskRepository extends TaskRepository {

    @Override
    default void customize(QuerydslBindings bindings,
                           QTaskEntity root) {

        bindings.bind(String.class).first((StringPath path, String value) -> path.eq(value));
        bindings.bind(root.createdFrom).first((path, value) -> root.createdDate.after(value));
        bindings.bind(root.createdTo).first((path, value) -> root.createdDate.before(value));
        bindings.bind(root.lastModifiedFrom).first((path, value) -> root.lastModified.after(value));
        bindings.bind(root.lastModifiedTo).first((path, value) -> root.lastModified.before(value));
        bindings.bind(root.lastClaimedFrom).first((path, value) -> root.claimedDate.after(value));
        bindings.bind(root.lastClaimedTo).first((path, value) -> root.claimedDate.before(value));
        bindings.bind(root.completedFrom).first((path, value) -> root.completedDate.after(value));
        bindings.bind(root.completedTo).first((path, value) -> root.completedDate.before(value));
        bindings.bind(root.name).first((path, value) -> path.like("%"+value.toString()+"%"));
        bindings.bind(root.description).first((path, value) -> path.like("%"+value.toString()+"%"));
        bindings.bind(root.dueDate).first((path, value) -> root.dueDate.after(value));
    }

}
