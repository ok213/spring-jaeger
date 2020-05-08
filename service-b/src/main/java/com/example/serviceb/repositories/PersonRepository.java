package com.example.serviceb.repositories;

import com.example.serviceb.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author
 * <pre>
 *  	JPA repository interface for {@link Person} class
 * </pre>
 */
@Repository
public interface PersonRepository  extends JpaRepository<Person, Long> {

}
