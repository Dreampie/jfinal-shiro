/**
 *  2013-1-7 上午11:12:33
 */
package cn.dreampie.shiro.hasher;

/**
 * @author wangrenhui
 */
public enum Hasher {
  DEFAULT("default_hasher");

  private final String value;

  private Hasher(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
