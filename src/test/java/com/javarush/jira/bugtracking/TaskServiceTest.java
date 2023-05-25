package com.javarush.jira.bugtracking;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.model.UserBelong;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.UserBelongRepository;
import com.javarush.jira.bugtracking.to.ObjectType;
import com.javarush.jira.login.Role;
import com.javarush.jira.login.User;
import com.javarush.jira.login.internal.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskServiceTest extends AbstractControllerTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private UserBelongRepository userBelongRepository = mock(UserBelongRepository.class);
    private TaskRepository taskRepository = mock(TaskRepository.class);
    private final TaskMapper taskMapper = mock(TaskMapper.class);
    private TaskService taskService = new TaskService(taskRepository, taskMapper, userBelongRepository, userRepository);

    private final Role USER_ROLE = Role.ADMIN;

    @BeforeEach
    void resetMocks() {
        reset(userRepository, userBelongRepository, taskRepository, taskMapper);
    }

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userBelongRepository = mock(UserBelongRepository.class);
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository, taskMapper, userBelongRepository, userRepository);
    }

    @Test
    void addTaskTags_withValidTags_shouldAddTagsToTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTags(new HashSet<>());
        Set<String> tagsToAdd = new HashSet<>();
        tagsToAdd.add("tag1");
        tagsToAdd.add("tag2");

        when(taskRepository.getExisted(1L)).thenReturn(task);

        taskService.addTaskTags(1L, tagsToAdd);

        assertEquals(2, task.getTags().size());
    }

    @Test
    void addTaskTags_withNullTags_shouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.addTaskTags(1L, null));
    }

    @Test
    void addTaskTags_withInvalidTagLengthLess2_shouldThrowException() {
        Set<String> tagsToAdd = Collections.singleton("t");

        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.addTaskTags(1L, tagsToAdd));
    }

    @Test
    void addTaskTags_withInvalidTagLengthMore32_shouldThrowException() {
        Set<String> tagsToAdd = Collections.singleton("tsdfgfdsdfgfdsdfgfdsdfgfdsdfgfdsdfgfdsdfgfdsdfgsdfgsd");

        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.addTaskTags(1L, tagsToAdd));
    }

    @Test
    public void subscribeUserToTask_userExists_addsUserBelong() {
        Long taskId = 1L;
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        Set<Role> roles = new HashSet<>(List.of(USER_ROLE));
        user.setRoles(roles);
        when(userRepository.getExisted(userId)).thenReturn(user);

        taskService.subscribeUserToTask(taskId, userId);

        ArgumentCaptor<UserBelong> captor = ArgumentCaptor.forClass(UserBelong.class);
        verify(userBelongRepository).save(captor.capture());
        UserBelong userBelong = captor.getValue();
        assertEquals(userId, userBelong.getUserId());
        assertEquals(taskId, userBelong.getObjectId());
        assertEquals(ObjectType.TASK, userBelong.getObjectType());
        assertEquals(USER_ROLE.toString(), userBelong.getUserTypeCode());
    }
}
