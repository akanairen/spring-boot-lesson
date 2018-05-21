package com.pb.springbootlesson5.repository;

import com.pb.springbootlesson5.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepositoryImpl implements PersonRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Person findPerson(Integer id) {
        System.out.println("find persion: " + id);
        return (Person) redisTemplate.opsForValue().get(id);
    }

    @Override
    public boolean savePerson(Person person) {
        System.out.println("save Person: " + person);
        redisTemplate.opsForValue().set(person.getId(), person);
        return true;
    }

    @Override
    public boolean deletePerson(Integer id) {
        System.out.println("delete Person: " + id);
        redisTemplate.delete(id);
        return true;
    }
}
