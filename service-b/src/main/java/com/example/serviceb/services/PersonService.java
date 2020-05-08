package com.example.serviceb.services;

import com.example.serviceb.models.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {

    /**
     *
     * @param personId
     * @return {@link Optional} {@link Person} objects if present in database
     *         for supplied person ID
     */
    public Optional<Person> getPersonById(Long personId);

    /**
     *
     * @return {@link List} of {@link Person} model class fo rall available
     *         entities
     */
    public List<Person> getAllPersons();

    /**
     *
     * @param personId
     * @return Delete the person from database for supplied id
     */
    public boolean removePerson(Long personId);

    /**
     *
     * @param person
     * @return {@link Optional} {@link Person} objects after save or update Save
     *         if no personId present else update
     */
    public Optional<Person> saveUpdatePerson(Person person);

}
