package cn.dreampie.shiro.listeners;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * Created by wangrenhui on 14-1-4.
 */
public class MySessionListener implements SessionListener {
  @Override
  public void onStart(Session session) {

  }

  @Override
  public void onStop(Session session) {
  }

  @Override
  public void onExpiration(Session session) {

  }
}
