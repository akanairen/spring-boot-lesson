package com.pb.springbootlesson2.controller;

import com.pb.springbootlesson2.domain.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
@RequestMapping("/student/")
public class StudentController {

    private List<Student> students;
    private Integer idSeq = 10;

    @PostConstruct
    public void init() {
        students = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Student student = new Student();
            student.setId(i);
            student.setName(UUID.randomUUID().toString().substring(0, 8));
            student.setAge(new Random().nextInt(100));

            students.add(student);
        }
    }

    @RequestMapping("")
    public String home(ModelMap modelMap) {
        modelMap.addAttribute("students", students);
        modelMap.addAttribute("clazz", new Random().nextInt(10));

        return "student";
    }

    /**
     * 操作路由
     * @param modelMap
     * @param command
     * @param id
     * @return
     */
    @RequestMapping("{command}")
    public String route(ModelMap modelMap, @PathVariable("command") String command, @RequestParam(required = false) Integer id) {
        if ("to_add".equals(command)) {
            return toAdd();
        } else if ("to_edit".equals(command)) {
            return toEdit(modelMap, id);
        } else {
            return "redirect:";
        }
    }

    /**
     * 新增跳转
     * @return
     */
    public String toAdd() {
        return "student_input";
    }

    /**
     * 新增
     * @param student
     * @return
     */
    @RequestMapping("add")
    synchronized public String add(Student student) {
        student.setId(idSeq);
        students.add(student);

        idSeq++;
        return "redirect:";
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("del")
    public String delete(Integer id) {
        synchronized (students) {
            Iterator<Student> iterator = students.iterator();
            while (iterator.hasNext()) {
                Student student = iterator.next();
                if (student.getId().intValue() == id) {
                    iterator.remove();
                    break;
                }
            }
        }

        return "redirect:";
    }

    public String toEdit(ModelMap modelMap, Integer id) {
        for (Student student : students) {
            if (student.getId().intValue() == id) {
                modelMap.put("student", student);
                break;
            }
        }
        return "student_input";
    }

    @RequestMapping("edit")
    public String edit(Student student) {
        synchronized (students) {
            for (Student s1 : students) {
                if (s1.getId().intValue() == student.getId()) {
                    s1.setName(student.getName());
                    s1.setAge(student.getAge());
                    break;
                }
            }
        }
        return "redirect:";
    }
}
