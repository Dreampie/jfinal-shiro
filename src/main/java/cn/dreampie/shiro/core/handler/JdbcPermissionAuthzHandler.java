package cn.dreampie.shiro.core.handler;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

/**
 * Created by wangrenhui on 14-1-7.
 */
public class JdbcPermissionAuthzHandler extends AbstractAuthzHandler {
  private final String jdbcPermission;

  public JdbcPermissionAuthzHandler(String jdbcPermission) {
    this.jdbcPermission = jdbcPermission;
  }

  @Override
  public void assertAuthorized() throws AuthorizationException {
    Subject subject = getSubject();
    //数据库权限
    if (jdbcPermission != null) {
      subject.checkPermission(jdbcPermission);
      return;
    }
  }
}
