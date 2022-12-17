package com.ruoyi.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GengXuelong
 * @version 1.0
 * @Mail 3349495429@qq.com
 * @Date 2022/12/17
 * @ClassName HelloController
 * @Description:
 */
@RestController
@RequestMapping("chat")
public class HelloController {
    @RequestMapping("hello")
    public String hello(){
        return "hello gengxuelong";
    }
}
