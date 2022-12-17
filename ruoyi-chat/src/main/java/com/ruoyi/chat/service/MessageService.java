package com.ruoyi.chat.service;



import com.ruoyi.chat.mapper.MessageMapper;
import com.ruoyi.chat.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author GengXuelong
 * @version 1.0
 * @Mail 3349495429@qq.com
 * @Date 2022/12/17
 * @ClassName MessageService
 * @Description:
 *      service层
 */
@Service
public class MessageService {
    @Resource
    MessageMapper messageMapper;

    public List<Message> getAllMessageFromDao() {
        return messageMapper.selectAll();
    }

    public void addNewMessageToDao(Message message) {
        messageMapper.insert(message);
    }
}
