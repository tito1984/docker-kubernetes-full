package org.curso.springcloud.msvc.users.controller;

import jakarta.validation.Valid;
import org.curso.springcloud.msvc.users.models.entity.User;
import org.curso.springcloud.msvc.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment env;

    @GetMapping("/crash")
    public void crash() {
        ((ConfigurableApplicationContext)context).close();
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        Map<String, Object> body = new HashMap<>();
        body.put("users", userService.list());
        body.put("pod_info", env.getProperty("MY_POD_NAME") + ":" + env.getProperty("MY_POD_IP"));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.byId(id);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return validate(result);
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Collections
                    .singletonMap("message","The email already exist"));
        }
        return  ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody User user, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validate(result);
        }
        Optional<User> optionalUser = userService.byId(id);
        if (optionalUser.isPresent()) {
            User userDb = optionalUser.get();
            if (!user.getEmail().equalsIgnoreCase(userDb.getEmail()) && userService.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("message", "Email already exist"));
            }
            userDb.setUser(user.getName(),user.getEmail(),user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<User> optionalUser = userService.byId(id);
        if (optionalUser.isPresent()) {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users-course")
    public ResponseEntity<?> getUsersFromCourse(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(userService.findAllByIds(ids));
    }

    @GetMapping("/authorized")
    public Map<String, Object> authorized(@RequestParam String code){
        return Collections.singletonMap("code", code);
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    private ResponseEntity<Map<String, String>> validate(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Field " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
