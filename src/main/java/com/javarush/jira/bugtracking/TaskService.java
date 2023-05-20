package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.model.UserBelong;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.UserBelongRepository;
import com.javarush.jira.bugtracking.to.ObjectType;
import com.javarush.jira.bugtracking.to.TaskTo;
import com.javarush.jira.login.Role;
import com.javarush.jira.login.User;
import com.javarush.jira.login.internal.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


@Service
public class TaskService extends BugtrackingService<Task, TaskTo, TaskRepository> {

    private final UserRepository userRepository;

    private final UserBelongRepository userBelongRepository;

    public TaskService(TaskRepository repository, TaskMapper mapper, UserBelongRepository userBelongRepository, UserRepository userRepository) {
        super(repository, mapper);
        this.userRepository = userRepository;
        this.userBelongRepository = userBelongRepository;
    }

    public List<TaskTo> getAll() {
        return mapper.toToList(repository.getAll());
    }

    public void addTaskTags(Long id, Set<String> tags) {
        Task task = repository.getExisted(id);
        Set<String> validTag = validateTags(tags);
        task.getTags().addAll(validTag);
        repository.save(task);
    }

    private Set<String> validateTags(Set<String> tags) {
        tags.removeIf(tag -> tag.length() < 2 || tag.length() > 32);
        return tags;
    }

    public void subscribeUserToTask(Long taskId, Long userId) {
        User user = userRepository.getExisted(userId);
        Set<Role> roles = user.getRoles();
        if (roles.size() > 0){
            Iterator<Role> iterator = roles.iterator();
            Role role = iterator.next();
                UserBelong userBelong = new UserBelong();
                userBelong.setUserId(user.getId());
                userBelong.setObjectId(taskId);
                userBelong.setObjectType(ObjectType.TASK);
                userBelong.setUserTypeCode(role.toString());
                userBelongRepository.save(userBelong);
        }
    }
}