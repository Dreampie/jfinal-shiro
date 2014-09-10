package cn.dreampie.shiro.core.handler;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

/**
 * Created by wangrenhui on 14-1-7.
 */
public class JdbcRoleAuthzHandler extends AbstractAuthzHandler {
  private final String jdbcRole;

  public JdbcRoleAuthzHandler(String jdbcRole) {
    this.jdbcRole = jdbcRole;
  }

  @Override
  public void assertAuthorized() throws AuthorizationException {
    Subject subject = getSubject();
    //数据库权限
    if (jdbcRole != null) {
      subject.checkRole(jdbcRole);
      return;
    }
  }
}
