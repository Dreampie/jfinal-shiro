package cn.dreampie.shiro.hasher;

/**
 * Created by wangrenhui on 14-5-5.
 */
public class HasherInfo {
  String hashText;
  String hashResult;
  Hasher hasher;
  String salt;

  public HasherInfo(String hashText, String hashResult, Hasher hasher, String salt) {
    this.hashText = hashText;
    this.hashResult = hashResult;
    this.hasher = hasher;
    this.salt = salt;
  }

  public Hasher getHasher() {
    return hasher;
  }

  public void setHasher(Hasher hasher) {
    this.hasher = hasher;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public String getHashText() {
    return hashText;
  }

  public void setHashText(String hashText) {
    this.hashText = hashText;
  }

  public String getHashResult() {
    return hashResult;
  }

  public void setHashResult(String hashResult) {
    this.hashResult = hashResult;
  }
}
