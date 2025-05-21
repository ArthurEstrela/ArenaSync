package com.ajs.arenasync.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{


User findByEmail(String email);

}
