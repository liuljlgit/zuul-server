package com.cloud.personal.zuulserver.route.nacos;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.cloud.personal.zuulserver.route.AbstractDynRouteLocator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author liulijun
 * @date 2020/6/15 09:48
 * @version 1.0
 * @description Nacos动态路由实现类
 */
@Slf4j
public class NacosDynRouteLocator extends AbstractDynRouteLocator {
  private static final String ZUUL_DATA_ID = "zuul-routes";
  private static final String ZUUL_GROUP_ID = "ZUUL_GATEWAY";

  private NacosConfigProperties nacosConfigProperties;

  private ApplicationEventPublisher publisher;

  private NacosDynRouteLocator locator;

  @Setter
  private List<NacosZuulRouteEntity> zuulRouteEntities;

  public NacosDynRouteLocator(
      NacosConfigProperties nacosConfigProperties,
      ApplicationEventPublisher publisher,
      String servletPath,
      ZuulProperties properties) {
    super(servletPath, properties);
    this.nacosConfigProperties = nacosConfigProperties;
    this.publisher = publisher;
    this.locator = this;
    addListener();
  }

  /** 添加Nacos监听 */
  private void addListener() {
    try {
      nacosConfigProperties
          .configServiceInstance()
          .addListener(
              ZUUL_DATA_ID,
              ZUUL_GROUP_ID,
              new Listener() {
                @Override
                public Executor getExecutor() {
                  return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                  // 赋值路由信息
                  locator.setZuulRouteEntities(getListByStr(configInfo));
                  RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(locator);
                  publisher.publishEvent(routesRefreshedEvent);
                }
              });
    } catch (NacosException e) {
      log.error("nacos-addListener-error", e);
    }
  }

  @Override
  public Map<String, ZuulRoute> loadDynamicRoute() {
    Map<String, ZuulRoute> routes = new LinkedHashMap<>();
    if (zuulRouteEntities == null) {
      zuulRouteEntities = getNacosConfig();
    }
    for (NacosZuulRouteEntity result : zuulRouteEntities) {
      if (StringUtils.isBlank(result.getPath()) || !result.isEnabled()) {
        continue;
      }
      ZuulRoute zuulRoute = new ZuulRoute();
      BeanUtils.copyProperties(result, zuulRoute);
      routes.put(zuulRoute.getPath(), zuulRoute);
    }
    return routes;
  }

  /** 查询zuul的路由配置 */
  private List<NacosZuulRouteEntity> getNacosConfig() {
    try {
      String content =
          nacosConfigProperties
              .configServiceInstance()
              .getConfig(ZUUL_DATA_ID, ZUUL_GROUP_ID, 5000);
      return getListByStr(content);
    } catch (NacosException e) {
      log.error("listenerNacos-error", e);
    }
    return new ArrayList<>(0);
  }

  public List<NacosZuulRouteEntity> getListByStr(String content) {
    if (StringUtils.isNotEmpty(content)) {
      return JSONObject.parseArray(content, NacosZuulRouteEntity.class);
    }
    return new ArrayList<>(0);
  }
}
