package com.pb.springbootlesson5.services;

import com.pb.springbootlesson5.entity.Person;
import com.pb.springbootlesson5.repository.PersonMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMongoRepository personMongoRepository;

    @Override
    public Person find(Integer id) {
        System.out.println("Find Person: " + id);
        return personMongoRepository.findOne(id);
    }

    @Override
    public Person save(Person person) {
        return personMongoRepository.save(person);
    }

    @Override
    public void delete(Integer id) {
        System.out.println("Delete Person: " + id);
        personMongoRepository.delete(id);
    }
}
