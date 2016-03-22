package io.pivotal.singapore.repositories;

import io.pivotal.singapore.models.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long>{
    Optional<Command> findOneByName(String name);
}
