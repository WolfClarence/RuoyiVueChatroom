package com.ruoyi.chat.controller;


import com.ruoyi.chat.pojo.Message;
import com.ruoyi.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author GengXuelong
 * @version 1.0
 * @Mail 3349495429@qq.com
 * @Date 2022/12/17
 * @ClassName MessageController
 * @Description:
 */
@CrossOrigin
@RestController
@RequestMapping("chat")
public class MessageController {
    @Autowired
    MessageService messageService;
    @RequestMapping("/hello")
    public List<Message> hello(){
        return messageService.getAllMessageFromDao();
    }

    @PostMapping("add")
    public String add(@RequestBody Message message){
        System.out.println(message.getUsername()+"------"+message.getText());
        messageService.addNewMessageToDao(message);
        return "发言成功";
    }
}
