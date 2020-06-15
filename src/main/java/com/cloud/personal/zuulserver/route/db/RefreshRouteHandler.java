package com.cloud.personal.zuulserver.route.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RefreshRouteHandler {

    @Autowired
    DbDynRouteLocator dbDynRouteLocator;

    @RequestMapping("/routes/refresh")
    public Boolean refreshRoute(){
        try{
            dbDynRouteLocator.refresh();
            return true;
        } catch (Exception e){
            log.info("refresh db routes error!");
            return false;
        }
    }

}
