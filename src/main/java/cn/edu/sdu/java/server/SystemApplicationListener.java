package cn.edu.sdu.java.server;

import cn.edu.sdu.java.server.services.SystemService;
import cn.edu.sdu.java.server.services.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SystemApplicationListener 系统应用实践处理程序
 */
@Component
@Order(0)
@Slf4j
public class SystemApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    private final SystemService systemService;  //系统服务对象自动注入
    public SystemApplicationListener(SystemService systemService) {
        this.systemService = systemService;
    }

    /**
     * 系统实践处理方法 系统启动后自动加载数据字典
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("SystemInitStart");
        systemService.initDictionary();
        systemService.initSystem();
        log.info("systemInitEnd");
    }

}