package cn.dreampie.shiro;

/**
 * Created by wangrenhui on 14-1-3.
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import cn.dreampie.encription.EncriptionKit;
import cn.dreampie.shiro.exception.IncorrectCaptchaException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * An <code>AuthenticationFilter</code> that is capable of automatically performing an authentication attempt
 * based on the incoming request.
 *
 * @since 0.9
 */
public abstract class ShiroAuthenticatingFilter extends ShiroAuthenticationFilter {
  public static final String PERMISSIVE = "permissive";
  public static final String DEFAULT_CAPTCHA_PARAM = "captcha";
  public static boolean useCaptcha = true;

  //TODO - complete JavaDoc

  protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
    UsernamePasswordToken token = createToken(request, response);
    if (token == null) {
      String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
          "must be created in order to execute a login attempt.";
      throw new IllegalStateException(msg);
    }
    try {
      if (useCaptcha)
        doCaptchaValidate((CaptchaUsernamePasswordToken) token);
      Subject subject = getSubject(request, response);
      subject.login(token);
      return onLoginSuccess(token, subject, request, response);
    } catch (AuthenticationException e) {

      return onLoginFailure(token, e, request, response);
    }
  }

  protected abstract UsernamePasswordToken createToken(ServletRequest request, ServletResponse response) throws Exception;

  protected UsernamePasswordToken createToken(String username, String password,
                                              ServletRequest request, ServletResponse response) {
    boolean rememberMe = isRememberMe(request);
    String host = getHost(request);
    return createToken(username, password, rememberMe, host);
  }

  protected UsernamePasswordToken createToken(String username, String password,
                                              boolean rememberMe, String host) {
    return new UsernamePasswordToken(username, password, rememberMe, host);
  }


  // 创建 Token
  protected UsernamePasswordToken createToken(String username, String password, String captcha,
                                              ServletRequest request, ServletResponse response) {
    boolean rememberMe = isRememberMe(request);
    String host = getHost(request);
    return new CaptchaUsernamePasswordToken(username, password, rememberMe, host, captcha);
  }

  protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                   ServletRequest request, ServletResponse response) throws Exception {
    return true;
  }

  protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                   ServletRequest request, ServletResponse response) throws Exception {
    return false;
  }

  /**
   * Returns the host name or IP associated with the current subject.  This method is primarily provided for use
   * during construction of an <code>AuthenticationToken</code>.
   * <p>
   * The default implementation merely returns {@link javax.servlet.ServletRequest#getRemoteHost()}.
   *
   * @param request the incoming ServletRequest
   * @return the <code>InetAddress</code> to associate with the login attempt.
   */
  protected String getHost(ServletRequest request) {
    return request.getRemoteHost();
  }

  /**
   * Returns <code>true</code> if &quot;rememberMe&quot; should be enabled for the login attempt associated with the
   * current <code>request</code>, <code>false</code> otherwise.
   * <p>
   * This implementation always returns <code>false</code> and is provided as a template hook to subclasses that
   * support <code>rememberMe</code> logins and wish to determine <code>rememberMe</code> in a custom mannner
   * based on the current <code>request</code>.
   *
   * @param request the incoming ServletRequest
   * @return <code>true</code> if &quot;rememberMe&quot; should be enabled for the login attempt associated with the
   * current <code>request</code>, <code>false</code> otherwise.
   */
  protected boolean isRememberMe(ServletRequest request) {
    return false;
  }

  /**
   * Determines whether the current subject should be allowed to make the current request.
   * <p>
   * The default implementation returns <code>true</code> if the user is authenticated.  Will also return
   * <code>true</code> if the {@link #isLoginRequest} returns false and the &quot;permissive&quot; flag is set.
   *
   * @return <code>true</code> if request should be allowed access
   */
  @Override
  protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
    return super.isAccessAllowed(request, response, mappedValue) ||
        (!isLoginRequest(request, response) && isPermissive(mappedValue));
  }

  /**
   * Returns <code>true</code> if the mappedValue contains the {@link #PERMISSIVE} qualifier.
   *
   * @param mappedValue mappedValue
   * @return <code>true</code> if this filter should be permissive
   */
  protected boolean isPermissive(Object mappedValue) {
    if (mappedValue != null) {
      String[] values = (String[]) mappedValue;
      return Arrays.binarySearch(values, PERMISSIVE) >= 0;
    }
    return false;
  }

  /**
   * Overrides the default behavior to call {@link #onAccessDenied} and swallow the exception if the exception is
   * {@link org.apache.shiro.authz.UnauthenticatedException}.
   */
  @Override
  protected void cleanup(ServletRequest request, ServletResponse response, Exception existing) throws ServletException, IOException {
    if (existing instanceof UnauthenticatedException || (existing instanceof ServletException && existing.getCause() instanceof UnauthenticatedException)) {
      try {
        onAccessDenied(request, response);
        existing = null;
      } catch (Exception e) {
        existing = e;
      }
    }
    super.cleanup(request, response, existing);

  }

  protected String getCaptcha(ServletRequest request) {
    return WebUtils.getCleanParam(request, DEFAULT_CAPTCHA_PARAM);
  }


  // 验证码校验
  protected void doCaptchaValidate(CaptchaUsernamePasswordToken token) {
    Session session = SecurityUtils.getSubject().getSession();
    if (session == null) {
      throw new UnknownSessionException("Unable found required Session");
    } else {
      if (session.getAttribute(DEFAULT_CAPTCHA_PARAM) != null) {
        String captcha = session.getAttribute(DEFAULT_CAPTCHA_PARAM).toString();
        // String captcha = CookieUtils.getCookie(request, AppConstants.CAPTCHA_NAME);
        if (token.getCaptcha() != null &&
            captcha.equalsIgnoreCase(EncriptionKit.encrypt(token.getCaptcha().toLowerCase()))) {
          return;
        }
      }
      throw new IncorrectCaptchaException();
    }
  }
}
