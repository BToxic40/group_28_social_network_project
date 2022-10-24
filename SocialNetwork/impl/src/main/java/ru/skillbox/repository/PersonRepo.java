package ru.skillbox.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.model.Person;

@Repository
public interface PersonRepo extends JpaRepository<Integer, Person> {

  Optional<Person> findById(long id);

}