/*
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
package cn.dreampie.shiro;

import cn.dreampie.ThreadLocalUtils;
import cn.dreampie.shiro.core.SubjectUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * Requires the requesting user to be authenticated for the request to continue, and if they are not, forces the user
 * to login via by redirecting them to the {@link #setLoginUrl(String) loginUrl} you configure.
 * <p/>
 * <p>This filter constructs a {@link org.apache.shiro.authc.UsernamePasswordToken UsernamePasswordToken} with the values found in</p>
 * {@link #setUsernameParam(String) username}, {@link #setPasswordParam(String) password},
 * and {@link #setRememberMeParam(String) rememberMe} request parameters.  It then calls
 * {@link org.apache.shiro.subject.Subject#login(org.apache.shiro.authc.AuthenticationToken) Subject.login(usernamePasswordToken)},
 * effectively automatically performing a login attempt.  Note that the login attempt will only occur when the
 * {@link #isLoginSubmission(javax.servlet.ServletRequest, javax.servlet.ServletResponse) isLoginSubmission(request,response)}
 * is <code>true</code>, which by default occurs when the request is for the {@link #setLoginUrl(String) loginUrl} and
 * is a POST request.
 * <p/>
 * <p>If the login attempt fails, the resulting <code>AuthenticationException</code> fully qualified class name will</p>
 * be set as a request attribute under the {@link #setFailureKeyAttribute(String) failureKeyAttribute} key.  This
 * FQCN can be used as an i18n key or lookup mechanism to explain to the user why their login attempt failed
 * (e.g. no user, incorrect password, etc).
 * <p/>
 * <p>If you would prefer to handle the authentication validation and login in your own code, consider using the</p>
 * {@link org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter} instead, which allows requests to the
 * {@link #loginUrl} to pass through to your application's code directly.
 *
 * @see org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter
 * @since 0.9
 */
public class MyFormAuthenticationFilter extends MyAuthenticatingFilter {

  //TODO - complete JavaDoc

  public static final String DEFAULT_ERROR_KEY_ATTRIBUTE_NAME = "shiroLoginFailure";

  public static final String DEFAULT_USERNAME_PARAM = "username";
  public static final String DEFAULT_PASSWORD_PARAM = "password";
  public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";

  private static final Logger log = LoggerFactory.getLogger(MyFormAuthenticationFilter.class);

  private String usernameParam = DEFAULT_USERNAME_PARAM;
  private String passwordParam = DEFAULT_PASSWORD_PARAM;
  private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;

  private String failureKeyAttribute = DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;

  public MyFormAuthenticationFilter() {
    setLoginUrl(DEFAULT_LOGIN_URL);
  }

  @Override
  public void setLoginUrlMap(Map<String, String> loginUrlMap) {

    if (log.isTraceEnabled()) {
      log.trace("Adding more login url to applied paths.");
    }
    //添加到拦截路径
    setUrlPath(loginUrlMap, getLoginUrlMap());
    super.setLoginUrlMap(loginUrlMap);
  }

  /**
   * 多个成功的跳转路径
   *
   * @param successUrlMap success url map
   */
  public void setSuccessUrlMap(Map<String, String> successUrlMap) {
    if (log.isTraceEnabled()) {
      log.trace("Adding more success url to applied paths.");
    }
    //添加到拦截路径
    setUrlPath(successUrlMap, getSuccessUrlMap());
    super.setSuccessUrlMap(successUrlMap);
  }

  /**
   * 多个失败的跳转路径
   *
   * @param failureUrlMap success url map
   */
  public void setFailureUrlMap(Map<String, String> failureUrlMap) {
    if (log.isTraceEnabled()) {
      log.trace("Adding more success url to applied paths.");
    }
    //添加到拦截路径
    setUrlPath(failureUrlMap, getSuccessUrlMap());
    super.setFailureUrlMap(failureUrlMap);
  }

  /**
   * 添加到拦截路径并删除原来的链接
   *
   * @param now      now
   * @param previous previous
   */
  private void setUrlPath(Map<String, String> now, Map<String, String> previous) {
    if (previous != null) {
      for (String key : previous.keySet()) {
        this.appliedPaths.remove(previous.get(key));
      }
    }

    if (now != null) {
      for (String key : now.keySet()) {
        this.appliedPaths.put(now.get(key), null);
      }
    }
  }

  @Override
  public void setLoginUrl(String loginUrl) {
    String previous = getLoginUrl();
    if (previous != null) {
      this.appliedPaths.remove(previous);
    }
    super.setLoginUrl(loginUrl);
    if (log.isTraceEnabled()) {
      log.trace("Adding login url to applied paths.");
    }
    this.appliedPaths.put(getLoginUrl(), null);
  }

  public String getUsernameParam() {
    return usernameParam;
  }

  /**
   * Sets the request parameter name to look for when acquiring the username.  Unless overridden by calling this
   * method, the default is <code>username</code>.
   *
   * @param usernameParam the name of the request param to check for acquiring the username.
   */
  public void setUsernameParam(String usernameParam) {
    this.usernameParam = usernameParam;
  }

  public String getPasswordParam() {
    return passwordParam;
  }

  /**
   * Sets the request parameter name to look for when acquiring the password.  Unless overridden by calling this
   * method, the default is <code>password</code>.
   *
   * @param passwordParam the name of the request param to check for acquiring the password.
   */
  public void setPasswordParam(String passwordParam) {
    this.passwordParam = passwordParam;
  }

  public String getRememberMeParam() {
    return rememberMeParam;
  }

  /**
   * Sets the request parameter name to look for when acquiring the rememberMe boolean value.  Unless overridden
   * by calling this method, the default is <code>rememberMe</code>.
   * <p/>
   * RememberMe will be <code>true</code> if the parameter value equals any of those supported by
   * {@link org.apache.shiro.web.util.WebUtils#isTrue(javax.servlet.ServletRequest, String) WebUtils.isTrue(request,value)}, <code>false</code>
   * otherwise.
   *
   * @param rememberMeParam the name of the request param to check for acquiring the rememberMe boolean value.
   */
  public void setRememberMeParam(String rememberMeParam) {
    this.rememberMeParam = rememberMeParam;
  }

  public String getFailureKeyAttribute() {
    return failureKeyAttribute;
  }

  public void setFailureKeyAttribute(String failureKeyAttribute) {
    this.failureKeyAttribute = failureKeyAttribute;
  }

  protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    if (isLoginRequest(request, response)) {
      if (isLoginSubmission(request, response)) {
        if (log.isTraceEnabled()) {
          log.trace("Login submission detected.  Attempting to execute login.");
        }
        return executeLogin(request, response);
      } else {
        if (log.isTraceEnabled()) {
          log.trace("Login page view.");
        }
        //allow them to see the login page ;)
        return true;
      }
    } else {

      if (log.isTraceEnabled()) {
        log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
            "Authentication url [" + getLoginUrl() + "]");
      }

      saveRequestAndRedirectToLogin(request, response);
      return false;
    }
  }

  /**
   * This default implementation merely returns <code>true</code> if the request is an HTTP <code>POST</code>,
   * <code>false</code> otherwise. Can be overridden by subclasses for custom login submission detection behavior.
   *
   * @param request  the incoming ServletRequest
   * @param response the outgoing ServletResponse.
   * @return <code>true</code> if the request is an HTTP <code>POST</code>, <code>false</code> otherwise.
   */
  @SuppressWarnings({"UnusedDeclaration"})
  protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
    return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
  }

  protected CaptchaUsernamePasswordToken createToken(ServletRequest request, ServletResponse response) {
    String username = getUsername(request);
    String password = getPassword(request);
    String captcha = getCaptcha(request);
    return createToken(username, password, captcha, request, response);
  }

  protected boolean isRememberMe(ServletRequest request) {
    return WebUtils.isTrue(request, getRememberMeParam());
  }

  protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                   ServletRequest request, ServletResponse response) throws Exception {
//    setUserAttribute(request, response);
    clearFailureAttribute(request, response);
    issueSuccessRedirect(request, response);
    //we handled the success redirect directly, prevent the chain from continuing:
    return false;
  }

  protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                   ServletRequest request, ServletResponse response) throws Exception {
    setFailureAttribute(request, response, e);
    issueFailureRedirect(request, response);
    if (ThreadLocalUtils.isJson()) {
      return false;
    } else {
      //login failed, let request continue back to the login page:
      return true;
    }
  }

  protected void setFailureAttribute(ServletRequest request, ServletResponse response, AuthenticationException ae) {
    String className = ae.getClass().getSimpleName();
    if (ThreadLocalUtils.isJson()) {
      request.setAttribute(getFailureKeyAttribute(), className);
    } else {
      Session session = getSubject(request, response).getSession();
      session.setAttribute(getFailureKeyAttribute(), className);
    }
  }

  protected void clearFailureAttribute(ServletRequest request, ServletResponse response) {
    if (ThreadLocalUtils.isJson()) {
      request.setAttribute("user", SubjectUtils.me().getUser());
      request.removeAttribute(getFailureKeyAttribute());
    } else {
      Session session = getSubject(request, response).getSession();
      session.removeAttribute(getFailureKeyAttribute());
      session.removeAttribute(usernameParam);
    }
  }

//  @Deprecated
//  protected void setUserAttribute(ServletRequest request, ServletResponse response) {
//    Session session = getSubject(request, response).getSession();
//    session.setAttribute(AppConstants.CURRENT_USER, session.getAttribute(AppConstants.TEMP_USER));
//  }

  protected String getUsername(ServletRequest request) {
    return WebUtils.getCleanParam(request, getUsernameParam());
  }

  protected String getPassword(ServletRequest request) {
    return WebUtils.getCleanParam(request, getPasswordParam());
  }

}
