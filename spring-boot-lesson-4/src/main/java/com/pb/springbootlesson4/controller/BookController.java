package com.pb.springbootlesson4.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pb.springbootlesson4.dao.BookMapper;
import com.pb.springbootlesson4.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/book/")
public class BookController {

    @Autowired
    private BookMapper bookMapper;

    @RequestMapping("")
    public String list(@RequestParam(required = false, defaultValue = "1") Integer page, ModelMap modelMap) {
        PageHelper.startPage(page, 10);
        List<Book> books = bookMapper.find();
        PageInfo<Book> pageInfo = new PageInfo<>(books);

        modelMap.put("pageInfo", pageInfo);
        return "books";
    }
}
