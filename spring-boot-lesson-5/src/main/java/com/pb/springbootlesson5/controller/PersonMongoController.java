package com.pb.springbootlesson5.controller;

import com.pb.springbootlesson5.entity.Person;
import com.pb.springbootlesson5.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mongo/persons/")
public class PersonMongoController {

    @Autowired
    private PersonService personService;

    @PostMapping("")
    public Person save(@RequestBody Person person) {
        personService.save(person);
        return person;
    }

    @GetMapping("{id}")
    public Person find(@PathVariable Integer id) {
        return personService.find(id);
    }

    @DeleteMapping("{id}")
    public Boolean delete(@PathVariable Integer id) {
        personService.delete(id);
        return true;
    }
}
