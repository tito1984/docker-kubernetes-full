package org.curso.springcloud.msvc.courses.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import org.curso.springcloud.msvc.courses.model.User;
import org.curso.springcloud.msvc.courses.model.entity.Course;
import org.curso.springcloud.msvc.courses.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> findAll() {
        return ResponseEntity.ok(courseService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Course> optionalCourse = courseService.byIdWithUsers(id);
        //Optional<Course> optionalCourse = courseService.byId(id);
        if (optionalCourse.isPresent()) {
            return ResponseEntity.ok(optionalCourse.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Course course, BindingResult result) {
        if (result.hasErrors()) {
            return validate(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Course course, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validate(result);
        }
        Optional<Course> optionalCourse = courseService.byId(id);
        if (optionalCourse.isPresent()) {
            Course courseDb = optionalCourse.get();
            courseDb.setName(course.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(courseDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Course> optionalCourse = courseService.byId(id);
        if (optionalCourse.isPresent()) {
            courseService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/add-user/{courseId}")
    public ResponseEntity<?> addUser(@RequestBody User user, @PathVariable Long courseId) {
        try {
            Optional<User> optionalUser = courseService.setUser(user,courseId);
            if (optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(optionalUser.get());
            }
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Unable to find User in MSVC: " + e.getMessage()));
        }
    }

    @PostMapping("/create-user/{courseId}")
    public ResponseEntity<?> createUser(@RequestBody User user, @PathVariable Long courseId) {
        try {
            Optional<User> optionalUser = courseService.createUser(user,courseId);
            if (optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(optionalUser.get());
            }
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Unable to create User in MSVC: " + e.getMessage()));
        }
    }

    @DeleteMapping("/unset-user/{courseId}")
    public ResponseEntity<?> deleteUser(@RequestBody User user, @PathVariable Long courseId) {
        try {
            Optional<User> optionalUser = courseService.unsetUser(user,courseId);
            if (optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(optionalUser.get());
            }
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Unable to unset User in MSVC: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteCourseUserById(@PathVariable Long id) {
        courseService.deleteCourseUserById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validate(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Field " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
