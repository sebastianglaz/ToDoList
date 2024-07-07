package com.List.ToDoList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/")
public class TaskController {

    private final TaskRepository repository;

    @Autowired
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/addTask")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "addTask";
    }

    @PostMapping("/addTask")
    public String addTask(@Valid @ModelAttribute Task task, Errors errors) {
        if (errors.hasErrors()) {
            return "addTask";
        }
        task.setCreatedAt(new Date());
        repository.insertTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        List<Task> tasks = repository.selectAllTasks();
        model.addAttribute("tasks", tasks);
        return "listTasks";
    }

    @PostMapping("/tasks/complete")
    public String completeTask(@RequestParam Long id) {
        Task task = repository.selectTask(id);
        if (task != null) {
            task.setCompleted(true);
            repository.updateTask(task);
        }
        return "redirect:/tasks";
    }
}
