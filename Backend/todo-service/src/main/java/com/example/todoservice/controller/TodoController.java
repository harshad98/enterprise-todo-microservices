package com.example.todoservice.controller;

import com.example.todoservice.service.TodoService;
import lombok.RequiredArgsConstructor;
import com.example.todoservice.entity.Todo;
import com.example.todoservice.repository.TodoRepository;
import com.example.todoservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // Create todo
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(
            @RequestBody Todo todo,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        return todoService.createTodo(todo, userId);
    }

    // Get logged-in user's todos
    @GetMapping
    public List<Todo> getMyTodos(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        return todoService.getTodosByUser(userId);
    }

    // Get a specific todo
    @GetMapping("/{id}")
    public Todo getTodo(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        return todoService.getTodoById(id, userId);
    }

    // Update own todo
    @PutMapping("/{id}")
    public Todo updateTodo(
            @PathVariable Long id,
            @RequestBody Todo todo,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        return todoService.updateTodo(id, todo, userId);
    }

    // Delete own todo
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        todoService.deleteTodo(id, userId);
    }

    // Admin: Get all todos
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }

    // Admin: Get any todo by ID
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Todo getTodoByAdmin(@PathVariable Long id) {
        return todoService.getTodoByIdForAdmin(id);
    }

    // Admin: Delete any user's todo
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoByAdmin(@PathVariable Long id) {
        todoService.deleteTodoForAdmin(id);
    }
}