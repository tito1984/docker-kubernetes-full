package org.curso.springcloud.msvc.users.service;

import org.curso.springcloud.msvc.users.models.entity.User;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> list();
    Optional<User> byId(Long id);
    User save(User user);
    void delete(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAllByIds(Iterable<Long> ids);

}
