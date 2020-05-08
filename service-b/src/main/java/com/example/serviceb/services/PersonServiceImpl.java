package com.example.serviceb.services;

import com.example.serviceb.models.Person;
import com.example.serviceb.repositories.PersonRepository;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final Tracer tracer;
    private final DelayService delayService;
    private final PersonRepository personRepo;

    @Override
    public Optional<Person> getPersonById(Long personId) {

        Span span = tracer.buildSpan("PersonService: getToPersonById")
                .asChildOf(tracer.activeSpan())
                .start();

        Scope scope = tracer.activateSpan(span);
        delayService.delay();

        Optional<Person> result = personRepo.findById(personId);
        scope.close();

        span.log("Finish method getPersonById").finish();
        return result;
    }

    @Override
    public List<Person> getAllPersons() {

        Span span = tracer.buildSpan("PersonService: getAllPersons")
                .asChildOf(tracer.activeSpan())
                .start();

        Scope scope = tracer.activateSpan(span);
        delayService.delay();

        List<Person> persons = personRepo.findAll().parallelStream().collect(Collectors.toList());
        scope.close();

        span.log("Finish method getAllPersons").finish();
        return persons;
    }

    @Override
    public boolean removePerson(Long personId) {
        delayService.delay();
        personRepo.deleteById(personId);
        return true;
    }

    @Override
    public Optional<Person> saveUpdatePerson(Person person) {
        delayService.delay();
        if(person.getPersonId() == null || personRepo.existsById(person.getPersonId())) {
            Person entity = personRepo.save(person);
            return Optional.of(entity);
        } else {
            return Optional.empty();
        }
    }

}
