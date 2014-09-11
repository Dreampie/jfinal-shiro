package cn.dreampie.shiro.hasher;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

/**
 * Created by wangrenhui on 14-5-5.
 */
public class HasherKit {

  private static PasswordService passwordService = new DefaultPasswordService();

  public static HasherInfo hash(String hashText, Hasher hasher) {
    HasherInfo hasherInfo = null;
    if (hasher == Hasher.DEFAULT) {
      hasherInfo = new HasherInfo(hashText, passwordService.encryptPassword(hashText), hasher, "");
    }
    return hasherInfo;
  }

  public static boolean match(Object submittedPlaintext, String encrypted, Hasher hasher) {
    boolean result = false;
    if (hasher == Hasher.DEFAULT) {
      result = passwordService.passwordsMatch(submittedPlaintext, encrypted);
    }
    return result;
  }
}
