package com.pb.springbootlesson5.controller;

import com.pb.springbootlesson5.entity.Person;
import com.pb.springbootlesson5.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/persons/")
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @PostMapping("")
    public Person save(@RequestBody Person person) {
        repository.savePerson(person);
        return person;
    }

    @GetMapping("{id}")
    public Person get(@PathVariable("id") Integer id) {
        return repository.findPerson(id);
    }

    @DeleteMapping("{id}")
    public Person delete(@PathVariable("id") Integer id) {
        Person person = repository.findPerson(id);
        repository.deletePerson(id);
        return person;
    }

}