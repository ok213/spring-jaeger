package com.example.serviceb.controllers;

import com.example.serviceb.models.Person;
import com.example.serviceb.services.PersonService;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/person")
public class PersonController {

    private final Tracer tracer;
    private final PersonService personService;

    /**
     *
     * @return expose GET endpoint to return {@link List} of all available persons
     */
    @GetMapping
    public ResponseEntity<List<Person>> getAllPerson() {
        Span span = tracer.activeSpan();
        span.setOperationName("PersonController: getAllPersons");

        List<Person> persons = personService.getAllPersons();
        if (persons == null || persons.size() == 0) {
            Tags.ERROR.set(span, true);
            span.log("result: null").finish();
            return new ResponseEntity<List<Person>>(HttpStatus.NOT_FOUND);
        }

        span.log("result: get " + persons.size() + " persons").finish();
        return ResponseEntity.ok(persons);
    }

    /**
     *
     * @param personId supplied as path variable
     * @return expose GET endpoint to return  {@link Person} for the supplied person id
     * return HTTP 404 in case person is not found in database
     */
    @GetMapping(value = "/{personId}")
    public ResponseEntity<Person> getPerson(@PathVariable("personId") Long personId,
                                            @RequestHeader MultiValueMap<String, String> rawHeaders) {

        Span span = tracer.activeSpan();
        span.setOperationName("PersonController: getPerson");

        Person person = personService.getPersonById(personId).orElse(null);
        if (person == null) {
            Tags.ERROR.set(span, true);
            span.log("result: null").finish();
            return new ResponseEntity<Person>(HttpStatus.NOT_FOUND);
        }


        span.log("result: " + person.getFirstName() + " " + person.getLastName()).finish();

        return ResponseEntity.ok(person);
    }

    /**
     *
     * @param person JSON body
     * @return  expose POST mapping and return newly created person in case of successful operation
     * return HTTP 417 in case of failure
     */
    @PostMapping
    public ResponseEntity<Person> addNewPerson(@RequestBody Person person) {
        return personService.saveUpdatePerson(person)
                .map(p -> {
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    return new ResponseEntity<Person>(HttpStatus.EXPECTATION_FAILED);
        });
    }

    /**
     *
     * @param person JSON body
     * @return  expose PUT mapping and return newly created or updated person in case of successful operation
     * return HTTP 417 in case of failure
     *
     */
    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestBody Person person) {
        return personService.saveUpdatePerson(person)
                .map(p -> {
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    return new ResponseEntity<Person>(HttpStatus.EXPECTATION_FAILED);
        });
    }

    /**
     *
     * @param personId person id to be deleted
     * @return expose DELETE mapping and return success message if operation was successful.
     *  return HTTP 417 in case of failure
     *
     */
    @DeleteMapping(value = "/{personId}")
    public ResponseEntity<String> deletePerson(@PathVariable("personId") Long personId) {
        if (personService.removePerson(personId)) {
            return ResponseEntity.ok("Person with id : " + personId + " removed");
        } else {
            return new ResponseEntity<String>("Error deleting enitty ", HttpStatus.EXPECTATION_FAILED);
        }
    }

}
