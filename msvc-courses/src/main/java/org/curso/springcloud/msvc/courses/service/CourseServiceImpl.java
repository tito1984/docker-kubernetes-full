package org.curso.springcloud.msvc.courses.service;

import org.curso.springcloud.msvc.courses.clients.UserClientRest;
import org.curso.springcloud.msvc.courses.model.User;
import org.curso.springcloud.msvc.courses.model.entity.Course;
import org.curso.springcloud.msvc.courses.model.entity.CourseUser;
import org.curso.springcloud.msvc.courses.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private UserClientRest userClientRest;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Course> listAll() {
        return (List<Course>) courseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> byId(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<User> setUser(User user, Long courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            User userMsvc = userClientRest.findById(user.getId());
            Course course = optionalCourse.get();
            CourseUser courseUser = new CourseUser(userMsvc.getId());
            course.addCourseUser(courseUser);
            courseRepository.save(course);
            return Optional.of(userMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<User> createUser(User user, Long courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            User userMsvc = userClientRest.create(user);
            Course course = optionalCourse.get();
            CourseUser courseUser = new CourseUser(userMsvc.getId());
            course.addCourseUser(courseUser);
            courseRepository.save(course);
            return Optional.of(userMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<User> unsetUser(User user, Long courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            User userMsvc = userClientRest.findById(user.getId());
            Course course = optionalCourse.get();
            CourseUser courseUser = new CourseUser(userMsvc.getId());
            course.removeCourseUser(courseUser);
            courseRepository.save(course);
            return Optional.of(userMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> byIdWithUsers(Long id) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            if (!course.getCourseUsers().isEmpty()) {
                List<Long> ids = course.getCourseUsers().stream().map(courseUser -> courseUser.getUserId())
                        .collect(Collectors.toList());
                List<User> users = userClientRest.getUsersFromCourse(ids);
                course.setUsers(users);
            }
            return Optional.of(course);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteCourseUserById(Long id) {
        courseRepository.deleteCourseUserById(id);
    }
}
