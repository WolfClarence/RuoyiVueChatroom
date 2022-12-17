package com.ruoyi.chat.config;

import com.ruoyi.chat.filter.EncodingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GengXuelong
 * @version 1.0
 * @Mail 3349495429@qq.com
 * @Date 2022/12/18
 * @ClassName WebMvcConfig
 * @Description:
 */
@Configuration
public class WebMvcConfig {

    /**
     * @author GengXuelong
     * <p> 函数功能描述如下:
     * @Description:
     *     将自定义得过滤器注册到spring容器中
     */
    @Bean
    public FilterRegistrationBean<EncodingFilter> registerFilter(){
        FilterRegistrationBean<EncodingFilter> registrationBean = new FilterRegistrationBean<EncodingFilter>();
        registrationBean.setFilter(new EncodingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("Filter1");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
