package org.curso.springcloud.msvc.courses.service;

import org.curso.springcloud.msvc.courses.model.User;
import org.curso.springcloud.msvc.courses.model.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> listAll();
    Optional<Course> byId(Long id);
    Course save(Course course);
    void delete(Long id);

    Optional<User> setUser(User user, Long courseId);
    Optional<User> createUser(User user, Long courseId);
    Optional<User> unsetUser(User user, Long courseId);
    Optional<Course> byIdWithUsers(Long id);

    void deleteCourseUserById(Long id);
}
