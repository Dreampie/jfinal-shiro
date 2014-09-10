package cn.dreampie.shiro.core;

import cn.dreampie.shiro.core.handler.AuthzHandler;

import java.util.Map;

/**
 * Created by wangrenhui on 14-1-7.
 */
public interface JdbcAuthzService {
  public Map<String, AuthzHandler> getJdbcAuthz();
}
