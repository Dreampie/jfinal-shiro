package cn.dreampie.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by wangrenhui on 14-1-3.
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {

  private String captcha;

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public CaptchaUsernamePasswordToken(String username, String password,
                                      boolean rememberMe, String host) {
    super(username, password, rememberMe, host);
  }


  public CaptchaUsernamePasswordToken(String username, String password,
                                      boolean rememberMe, String host, String captcha) {
    super(username, password, rememberMe, host);
    this.captcha = captcha;
  }
}