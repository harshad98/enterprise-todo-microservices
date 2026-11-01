package com.example.todoservice.service;

import lombok.RequiredArgsConstructor;
import com.example.todoservice.dto.TodoRequest;
import com.example.todoservice.entity.Todo;
import com.example.todoservice.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    // Create todo for authenticated user
    public Todo createTodo(Todo todo, Long userId) {

        todo.setId(null);

        // Never trust userId from request body
        todo.setUserId(userId);

        return todoRepository.save(todo);
    }

    // Get only logged-in user's todos
    public List<Todo> getTodosByUser(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    // Get a specific todo only if it belongs to user
    public Todo getTodoById(Long id, Long userId) {

        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Todo not found"
                ));
    }

    // Update only user's own todo
    @Transactional
    public Todo updateTodo(Long id, Todo updatedTodo, Long userId) {

        Todo existingTodo = getTodoById(id, userId);

        existingTodo.setTitle(updatedTodo.getTitle());
        existingTodo.setDescription(updatedTodo.getDescription());
        existingTodo.setCompleted(updatedTodo.isCompleted());

        return todoRepository.save(existingTodo);
    }

    // Delete only user's own todo
    @Transactional
    public void deleteTodo(Long id, Long userId) {

        Todo todo = getTodoById(id, userId);

        todoRepository.delete(todo);
    }

    // Admin-only use: retrieve all todos
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    //Admin methods
    // Admin: Get any todo by ID
    public Todo getTodoByIdForAdmin(Long id) {

        return todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Todo not found"
                ));
    }

    // Admin: Delete any user's todo
    @Transactional
    public void deleteTodoForAdmin(Long id) {

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Todo not found"
                ));

        todoRepository.delete(todo);
    }
}