package com.cloud.personal.zuulserver.route.db;

import com.alibaba.nacos.api.exception.NacosException;
import com.cloud.personal.zuulserver.route.AbstractDynRouteLocator;
import com.cloud.personal.zuulserver.route.nacos.NacosZuulRouteEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liulijun
 * @date 2020/6/15 09:48
 * @version 1.0
 * @description Mysql动态路由实现类
 */
@Slf4j
public class DbDynRouteLocator extends AbstractDynRouteLocator {

  @Setter
  private JdbcTemplate jdbcTemplate;

  public DbDynRouteLocator(JdbcTemplate jdbcTemplate,String servletPath, ZuulProperties properties) {
    super(servletPath, properties);
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void refresh() {
    super.refresh();
  }

  @Override
  public Map<String, ZuulRoute> loadDynamicRoute() {
    Map<String, ZuulRoute> routes = new LinkedHashMap<>();
    List<DbZuulRouteEntity> dbZuulRouteEntities = getDbConfig();
    for (DbZuulRouteEntity result : dbZuulRouteEntities) {
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
  private List<DbZuulRouteEntity> getDbConfig() {
    try {
      List<DbZuulRouteEntity> results = jdbcTemplate.query(
              "select * from gateway_api_define where enabled = 1 ",
              new BeanPropertyRowMapper<>(DbZuulRouteEntity.class));
      for (DbZuulRouteEntity result : results) {
        if(StringUtils.isEmpty(result.getPath()) || (StringUtils.isEmpty(result.getServiceId()) && StringUtils.isEmpty(result.getUrl()))){
          continue;
        }
        if(StringUtils.isNotBlank(result.getSensitiveHeadersStr())){
          result.setSensitiveHeaders(Arrays.stream(result.getSensitiveHeadersStr().split(","))
                  .collect(Collectors.toSet()));
        }
        if(Objects.nonNull(result.getRetryableType())){
          result.setRetryable(DbRouteEnum.codeMap.get(result.getRetryableType()));
        }
        if(Objects.nonNull(result.getStripPrefixType())){
          result.setStripPrefix(DbRouteEnum.codeMap.get(result.getStripPrefixType()));
        }
        if(Objects.nonNull(result.getCustomSensitiveHeadersType())){
          result.setCustomSensitiveHeaders(DbRouteEnum.codeMap.get(result.getCustomSensitiveHeadersType()));
        }
      }
      return results;
    } catch (Exception e) {
      log.error("get config from db error", e);
    }
    return new ArrayList<>(0);
  }

}
