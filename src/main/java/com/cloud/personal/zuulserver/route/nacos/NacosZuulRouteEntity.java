package com.cloud.personal.zuulserver.route.nacos;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * zuul路由实体
 *
 * @author liulijun
 * @date 2020/06/15
 */
@Setter
@Getter
public class NacosZuulRouteEntity {
  /** The ID of the route (the same as its map key by default). */
  private String id;
  /** The path (pattern) for the route, e.g. /foo/**. */
  private String path;
  /**
   * The service ID (if any) to map to this route. You can specify a physical URL or a service, but
   * not both.
   */
  private String serviceId;
  /**
   * A full physical URL to map to the route. An alternative is to use a service ID and service
   * discovery to find the physical address.
   */
  private String url;
  /**
   * Flag to determine whether the prefix for this route (the path, minus pattern patcher) should be
   * stripped before forwarding.
   */
  private boolean stripPrefix = true;
  /**
   * Flag to indicate that this route should be retryable (if supported). Generally retry requires a
   * service ID and ribbon.
   */
  private Boolean retryable;
  /**
   * sensitiveHeaders
   */
  private Set<String> sensitiveHeaders = new LinkedHashSet();
  /**
   * enabled
   */
  private boolean enabled = true;
  /**
   * customSensitiveHeaders
   */
  private boolean customSensitiveHeaders = true;
}
