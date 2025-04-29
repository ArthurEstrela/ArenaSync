package com.ajs.arenasync.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.User;
import com.ajs.arenasync.Entities.Enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{


Optional<User> findByEmail(String email);

List<User> findByRole(Role role);


}
