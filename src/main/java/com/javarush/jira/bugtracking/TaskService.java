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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class TaskService extends BugtrackingService<Task, TaskTo, TaskRepository> {

    private static final int MAX_TAG_LENGTH = 32;
    private static final int MIN_TAG_LENGTH = 2;
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
        if (tags == null) {
            throw new IllegalArgumentException("The tags argument must not be null");
        }
        Set<String> validatedTags = tags.stream()
                .filter(tag -> tag.length() >= MIN_TAG_LENGTH && tag.length() <= MAX_TAG_LENGTH)
                .collect(Collectors.toUnmodifiableSet());
        if (validatedTags.size() != tags.size()) {
            throw new IllegalArgumentException("The tag length is out of bound");
        }
        return validatedTags;
    }

    public void subscribeUserToTask(Long taskId, Long userId) {
        User user = userRepository.getExisted(userId);
        Role role = user.getRoles().stream().findFirst().orElseThrow(() -> new RuntimeException("User has no roles"));
        UserBelong userBelong = new UserBelong();
        userBelong.setUserId(user.getId());
        userBelong.setObjectId(taskId);
        userBelong.setObjectType(ObjectType.TASK);
        userBelong.setUserTypeCode(role.toString());
        userBelongRepository.save(userBelong);
    }
}