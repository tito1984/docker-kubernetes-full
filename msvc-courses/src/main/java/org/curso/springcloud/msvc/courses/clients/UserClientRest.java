package org.curso.springcloud.msvc.courses.clients;

import org.curso.springcloud.msvc.courses.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-users")
public interface UserClientRest  {

    @GetMapping("/{id}")
    User findById(@PathVariable Long id);

    @PostMapping
    User create(@RequestBody User user);

    @GetMapping("/users-course")
    List<User> getUsersFromCourse(@RequestParam Iterable<Long> ids);
}
