package cn.dreampie.shiro.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Created by wangrenhui on 14-1-3.
 */
public class IncorrectCaptchaException extends AuthenticationException {

  public IncorrectCaptchaException() {
    super();
  }

  public IncorrectCaptchaException(String message, Throwable cause) {
    super(message, cause);
  }

  public IncorrectCaptchaException(String message) {
    super(message);
  }

  public IncorrectCaptchaException(Throwable cause) {
    super(cause);
  }
}