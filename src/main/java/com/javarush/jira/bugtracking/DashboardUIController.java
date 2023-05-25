package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.to.SprintTo;
import com.javarush.jira.bugtracking.to.TaskTo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping
public class DashboardUIController {

    private TaskService taskService;

    @GetMapping("/") // index page
    public String getAll(Model model) {
        List<TaskTo> tasks = taskService.getAll();
        Map<SprintTo, List<TaskTo>> taskMap = tasks.stream()
                .collect(Collectors.groupingBy(TaskTo::getSprint));
        model.addAttribute("taskMap", taskMap);
        return "index";
    }

    @GetMapping("/tasks/{id}/tags")
    public String addTag(@PathVariable("id") Long id, @RequestBody Set<String> tags){
        taskService.addTaskTags(id, tags);
        return "redirect:/";
    }

    @GetMapping("/tasks/{id}/users/{userId}")
    public String subscribeUserToTask(@PathVariable("id") Long taskId, @PathVariable("userId") Long userId){
        taskService.subscribeUserToTask(taskId, userId);
        return "redirect:/";
    }
}
