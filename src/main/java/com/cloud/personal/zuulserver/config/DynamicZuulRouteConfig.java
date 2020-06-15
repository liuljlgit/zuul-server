package com.cloud.personal.zuulserver.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.cloud.personal.zuulserver.route.db.DbDynRouteLocator;
import com.cloud.personal.zuulserver.route.nacos.NacosDynRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author liulijun
 * @date 2020/6/15 09:51
 * @version 1.0
 * @description nacos动态路由配置
 */
@Configuration
@ConditionalOnProperty(prefix = "gateway.dynamicRoute", name = "enabled", havingValue = "true")
public class DynamicZuulRouteConfig {
  @Autowired private ZuulProperties zuulProperties;

  @Autowired private DispatcherServletPath dispatcherServletPath;

  /** Nacos实现方式 */
  @Configuration
  @ConditionalOnProperty(
      prefix = "gateway.dynamicRoute",
      name = "dataType",
      havingValue = "nacos",
      matchIfMissing = true)
  public class NacosZuulRoute {
    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Bean
    public NacosDynRouteLocator nacosDynRouteLocator() {
      return new NacosDynRouteLocator(
          nacosConfigProperties, publisher, dispatcherServletPath.getPrefix(), zuulProperties);
    }
  }

  /** mysql实现方式 */
  @Configuration
  @ConditionalOnProperty(
          prefix = "gateway.dynamicRoute",
          name = "dataType",
          havingValue = "db",
          matchIfMissing = true)
  public class DbZuulRoute {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public DbDynRouteLocator dbDynRouteLocator() {
      return new DbDynRouteLocator(jdbcTemplate, dispatcherServletPath.getPrefix(), zuulProperties);
    }
  }

}
