package com.pb.springbootlesson5.repository;

import com.pb.springbootlesson5.entity.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonMongoRepository extends MongoRepository<Person, Integer> {
}
