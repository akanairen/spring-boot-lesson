package com.pb.springbootlesson4.dao;

import com.pb.springbootlesson4.entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {

    List<Book> find();
}
