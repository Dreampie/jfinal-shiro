package cn.dreampie.shiro.hasher;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

/**
 * Created by wangrenhui on 14-5-5.
 */
public class HasherUtils {

  private static HasherUtils hasherUtils = new HasherUtils();

  private static PasswordService passwordService = new DefaultPasswordService();

  private HasherUtils() {
  }

  public static HasherUtils me() {
    return hasherUtils;
  }

  public HasherInfo hash(String hashText, Hasher hasher) {
    HasherInfo hasherInfo = null;
    if (hasher == Hasher.DEFAULT) {
      hasherInfo = new HasherInfo(hashText, passwordService.encryptPassword(hashText), hasher, "");
    }
    return hasherInfo;
  }

  public boolean match(Object submittedPlaintext, String encrypted, Hasher hasher) {
    boolean result = false;
    if (hasher == Hasher.DEFAULT) {
      result = passwordService.passwordsMatch(submittedPlaintext, encrypted);
    }
    return result;
  }
}
