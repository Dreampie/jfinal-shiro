/**
 * Copyright (c) 2011-2013, dafei 李飞 (myaniu AT gmail DOT com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dreampie.shiro.core.handler;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * 基于角色的访问控制处理器，非单例模式运行。
 *
 * @author dafei
 */
public class RoleAuthzHandler extends AbstractAuthzHandler {

  private final Annotation annotation;

  public RoleAuthzHandler(Annotation annotation) {
    this.annotation = annotation;
  }

  public RoleAuthzHandler(String jdbcRole) {
    this.annotation = null;
  }

  @Override
  public void assertAuthorized() throws AuthorizationException {

    Subject subject = getSubject();

    if (!(annotation instanceof RequiresRoles)) return;
    RequiresRoles rrAnnotation = (RequiresRoles) annotation;
    String[] roles = rrAnnotation.value();

    if (roles.length == 1) {
      subject.checkRole(roles[0]);
      return;
    }
    if (Logical.AND.equals(rrAnnotation.logical())) {
      subject.checkRoles(Arrays.asList(roles));
      return;
    }
    if (Logical.OR.equals(rrAnnotation.logical())) {
      // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
      boolean hasAtLeastOneRole = false;
      for (String role : roles) if (subject.hasRole(role)) hasAtLeastOneRole = true;
      // Cause the exception if none of the role match, note that the exception message will be a bit misleading
      if (!hasAtLeastOneRole) subject.checkRole(roles[0]);
    }
  }
}
