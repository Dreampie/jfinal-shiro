jfinal-shiro
============

jfinal  shiro plugin 最简单,最灵活的权限框架实现，查看其他插件-> [Maven](http://search.maven.org/#search%7Cga%7C1%7Ccn.dreampie)

maven 引用  ${jfinal-shiro.version}替换为相应的版本如:0.2
```xml
<dependency>
   <groupId>cn.dreampie</groupId>
   <artifactId>jfinal-shiro</artifactId>
   <version>${jfinal-shiro.version}</version>
</dependency>
```

推荐的数据库权限表结构设计
```sql

DROP TABLE IF EXISTS sec_user;
CREATE TABLE sec_user (
  id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50)  NOT NULL COMMENT '登录名',
  providername  VARCHAR(50)  NOT NULL COMMENT '提供者',
  email         VARCHAR(200) COMMENT '邮箱',
  phone        VARCHAR(50) COMMENT '联系电话',
  password      VARCHAR(200) NOT NULL COMMENT '密码',
  hasher        VARCHAR(200) NOT NULL COMMENT '加密类型',
  salt          VARCHAR(200) NOT NULL COMMENT '加密盐',
  avatar_url    VARCHAR(255) COMMENT '头像',
  first_name    VARCHAR(10) COMMENT '名字',
  last_name     VARCHAR(10) COMMENT '姓氏',
  full_name     VARCHAR(20) COMMENT '全名',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
) ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='用户';

DROP TABLE IF EXISTS sec_user_info;
CREATE TABLE sec_user_info (
  id          BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT    NOT NULL COMMENT '用户id',
  creator_id  BIGINT COMMENT '创建者id',
  gender      INT DEFAULT 0 COMMENT '性别0男，1女',
  province_id BIGINT COMMENT '省id',
  city_id     BIGINT COMMENT '市id',
  county_id   BIGINT COMMENT '县id',
  street      VARCHAR(500) COMMENT '街道',
  zip_code    VARCHAR(50) COMMENT '邮编',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
) ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='用户信息';

DROP TABLE IF EXISTS sec_role;
CREATE TABLE sec_role (
  id         BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(50)   NOT NULL COMMENT '名称',
  value      VARCHAR(50)  NOT NULL COMMENT '值',
  intro      VARCHAR(255) COMMENT '简介',
  pid        BIGINT DEFAULT 0 COMMENT '父级id',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
) ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='角色';

DROP TABLE IF EXISTS sec_user_role;
CREATE TABLE sec_user_role (
  id      BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL
) ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='用户角色';

DROP TABLE IF EXISTS sec_permission;
CREATE TABLE sec_permission (
  id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(50) NOT NULL COMMENT '名称',
  value      VARCHAR(50) NOT NULL COMMENT '值',
  url        VARCHAR(255) COMMENT 'url地址',
  intro      VARCHAR(255) COMMENT '简介',
  pid        BIGINT DEFAULT 0 COMMENT '父级id',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
) ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='权限';


DROP TABLE IF EXISTS sec_role_permission;
CREATE TABLE sec_role_permission (
  id            BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  role_id       BIGINT NOT NULL,
  permission_id BIGINT NOT NULL
) ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='角色权限';


-- create role--

INSERT INTO sec_role(id,name, value, intro, pid,created_at)
VALUES (1,'超级管理员','R_ADMIN','',0, current_timestamp),
       (2,'系统管理员','R_MANAGER','',1,current_timestamp),
       (3,'总部','R_MEMBER','',2,current_timestamp),
       (4,'分部','R_USER','',2,current_timestamp);

-- create permission--
INSERT INTO sec_permission(id, name, value, url, intro,pid, created_at)
VALUES (1,'管理员目录','P_D_ADMIN','/admin/**','',0,current_timestamp),
       (2,'角色权限管理','P_ROLE','/admin/role/**','',1,current_timestamp),
       (3,'用户管理','P_USER','/admin/user/**','',1,current_timestamp),
       (4,'总部目录','P_D_MEMBER','/member/**','',0,current_timestamp),
       (5,'分部目录','P_D_USER','/user/**','',0,current_timestamp),
       (6,'用户处理','P_USER_CONTROL','/user/branch**','',5,current_timestamp),
       (7,'订单','P_ORDER','/order/**','',0,current_timestamp),
       (8,'订单处理','P_ORDER_CONTROL','/order/deliver**','',7,current_timestamp),
       (9,'订单更新','P_ORDER_UPDATE','/order/update**','',7,current_timestamp),
       (10,'支部订单','P_ORDER_BRANCH','/order/branch**','',7,current_timestamp),
       (11,'区域支行处理','P_REGION_CONTROL','/order/region**','',7,current_timestamp),
       (12,'收货地址','P_Address','/address/**','',0,current_timestamp);

INSERT INTO sec_role_permission(id,role_id, permission_id)
VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,1,5),(6,1,6),(7,1,7),(8,1,8),(9,1,9),(10,1,10),(11,1,11),(12,1,12),
       (13,2,1),(14,2,3),(15,2,4),(16,2,5),(17,2,6),(18,2,7),(19,2,8),(20,2,9),(21,2,10),(22,2,11),(23,2,12),
       (24,3,4),(25,3,5),(26,3,6),(27,3,11),
       (28,4,5),(29,4,7),(30,4,9),(31,4,12);

-- user data--
-- create  admin--
INSERT INTO sec_user(id,username, providername, email, phone, password, hasher, salt, avatar_url, first_name, last_name, full_name, created_at)
VALUES (1,'admin','shengmu','wangrenhui1990@gmail.com','15611434500','$shiro1$SHA-256$500000$iLqsOFPx5bjMGlB0JiNjQQ==$1cPTj9gyPGmYcKGQ8aw3shybrNF1ixdMCm/akFkn71o=','default_hasher','','','管理员','圣牧','圣牧.管理员',current_timestamp);

-- create user_info--
INSERT INTO sec_user_info(id,user_id, creator_id, gender,province_id,city_id,county_id,street,created_at)
VALUES (1,1,0,0,1,2,3,'人民大学',current_timestamp);

-- create user_role--
INSERT INTO sec_user_role(id, user_id, role_id)
VALUES (1,1,1);

```

实现数据库权限的初始化加载：
```java

public class MyJdbcAuthzService implements JdbcAuthzService {
  @Override
  public Map<String, AuthzHandler> getJdbcAuthz() {
    //加载数据库的url配置
    //按长度倒序排列url
    Map<String, AuthzHandler> authzJdbcMaps = Collections.synchronizedMap(new TreeMap<String, AuthzHandler>(
        new Comparator<String>() {
          public int compare(String k1, String k2) {
            int result = k2.length() - k1.length();
            if (result == 0) {
              return k1.compareTo(k2);
            }
            return result;
          }
        }));
    //遍历角色
    List<Role> roles = Role.dao.findAll();
    List<Permission> permissions = null;
    for (Role role : roles) {
      //角色可用
      if (role.getDate("daleted_at") == null) {
        permissions = Permission.dao.findByRole("", role.get("id"));
        //遍历权限
        for (Permission permission : permissions) {
          //权限可用
          if (permission.getDate("daleted_at") == null) {
            if (permission.getStr("url") != null && !permission.getStr("url").isEmpty()) {
              authzJdbcMaps.put(permission.getStr("url"), new JdbcPermissionAuthzHandler(permission.getStr("value")));
            }
          }
        }
      }
    }
    return authzJdbcMaps;
  }
}

```

实现shiro的用户数据加载：
```java

public class MyJdbcRealm extends AuthorizingRealm {

  /**
   * 登录认证
   *
   * @param token
   * @return
   * @throws org.apache.shiro.authc.AuthenticationException
   */
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    UsernamePasswordToken userToken = (UsernamePasswordToken) token;
    User user = null;
    String username = userToken.getUsername();
    if (ValidateKit.isEmail(username)) {
      user = User.dao.findFirstBy(" `user`.email =? AND `user`.deleted_at is null", username);
    } else if (ValidateKit.isMobile(username)) {
      user = User.dao.findFirstBy(" `user`.mobile =? AND `user`.deleted_at is null", username);
    } else {
      user = User.dao.findFirstBy(" `user`.username =? AND `user`.deleted_at is null", username);
    }
    if (user != null) {
      SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getStr("password"), getName());
      return info;
    } else {
      return null;
    }
  }

  /**
   * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
   *
   * @param principals 用户信息
   * @return
   */
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    String loginName = ((User) principals.fromRealm(getName()).iterator().next()).get("username");
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    Set<String> roleSet = new LinkedHashSet<String>(); // 角色集合
    Set<String> permissionSet = new LinkedHashSet<String>();  // 权限集合
    List<Role> roles = null;
    User user = User.dao.findFirstBy(" `user`.username =? AND `user`.deleted_at is null", loginName);
    if (user != null) {
      //遍历角色
      roles = Role.dao.findUserBy("", user.getLong("id"));
    } else {
      SubjectKit.getSubject().logout();
    }

    loadRole(roleSet, permissionSet, roles);
    info.setRoles(roleSet); // 设置角色
    info.setStringPermissions(permissionSet); // 设置权限
    return info;
  }

  /**
   * @param roleSet
   * @param permissionSet
   * @param roles
   */
  private void loadRole(Set<String> roleSet, Set<String> permissionSet, List<Role> roles) {
    List<Permission> permissions;
    for (Role role : roles) {
      //角色可用
      if (role.getDate("deleted_at") == null) {
        roleSet.add(role.getStr("value"));
        permissions = Permission.dao.findByRole("", role.getLong("id"));
        loadAuth(permissionSet, permissions);
      }
    }
  }

  /**
   * @param permissionSet
   * @param permissions
   */
  private void loadAuth(Set<String> permissionSet, List<Permission> permissions) {
    //遍历权限
    for (Permission permission : permissions) {
      //权限可用
      if (permission.getDate("deleted_at") == null) {
        permissionSet.add(permission.getStr("value"));
      }
    }
  }

  /**
   * 更新用户授权信息缓存.
   */

  public void clearCachedAuthorizationInfo(Object principal) {
    SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
    clearCachedAuthorizationInfo(principals);
  }

  /**
   * 清除所有用户授权信息缓存.
   */
  public void clearAllCachedAuthorizationInfo() {
    Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
    if (cache != null) {
      for (Object key : cache.keys()) {
        cache.remove(key);
      }
    }
  }
}

```

配置shiro.ini,放在resources(可放在其他目录，但必须保证能编译到classes)下，

```ini

[users]
guest = guest,guest

[main]
authc = cn.dreampie.shiro.ShiroFormAuthenticationFilter
authc.useCaptcha = false
;默认登陆数据提交路径
authc.loginUrl = /signin
#分角色登录提交配置
;authc.loginUrlMap = R_ADMIN:/admin/signin
;默认或者successUrlMap没有该角色时
authc.successUrl = /order
;不同角色登陆到不同的url,R_USER:/order可以不配置，会默认使用successUrl
authc.successUrlMap = R_USER:/order,R_MEMBER:/order/region,R_MANAGER:/order/branch,R_ADMIN:/order/branch
authc.failureUrl = /
;不同角色登陆失败跳转的路径
;authc.failureUrlMap =R_ADMIN:/admin/login

signout = cn.dreampie.shiro.ShiroLogoutFilter
;默认的退出url，redirectUrlMap里没有该角色使用该url
signout.redirectUrl = /
;如果你要区分不同角色推出到不同的url，使用map
;signout.redirectUrlMap = R_ADMIN:/admin/index

#realm
jdbcRealm = cn.dreampie.common.shiro.MyJdbcRealm
securityManager.realm = $jdbcRealm

passwordService = org.apache.shiro.authc.credential.DefaultPasswordService
passwordMatcher = cn.dreampie.shiro.ShiroPasswordMatcher
passwordMatcher.passwordService = $passwordService
jdbcRealm.credentialsMatcher = $passwordMatcher

#cache
shiroCacheManager = org.apache.shiro.cache.ehcache.EhCacheManager
shiroCacheManager.cacheManagerConfigFile = classpath:ehcache.xml
securityManager.cacheManager = $shiroCacheManager

#session
sessionDAO = org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
sessionDAO.activeSessionsCacheName = shiro-activeSessionCache
sessionManager.sessionDAO = $sessionDAO
securityManager.sessionManager = $sessionManager
sessionListener = cn.dreampie.shiro.listeners.ShiroSessionListener
securityManager.sessionManager.sessionListeners = $sessionListener

securityManager.sessionManager.globalSessionTimeout = 1200000
securityManager.sessionManager.sessionValidationSchedulerEnabled = false
securityManager.sessionManager.deleteInvalidSessions = false

[urls]
/signin = authc
/signout = signout
/** = anon

```

启用shiroplugin
```java

//shiro权限框架，添加到plugin
plugins.add(new ShiroPlugin(routes, new MyJdbcAuthzService()));

//添加shiro的过滤器到interceptor
interceptors.add(new ShiroInterceptor());

//用户操作相关  使用 SubjectKit工具类
```

web.xml添加shiroFilter过滤

```xml

<!--权限过滤器 start-->
<listener>
    <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
</listener>

<filter>
    <filter-name>shiroFilter</filter-name>
    <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>shiroFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>INCLUDE</dispatcher>
    <dispatcher>ERROR</dispatcher>
</filter-mapping>
<!--权限过滤器 end-->

```

除了使用数据的url数据判断权限外，还可以在方法上使用注解的方式过滤权限


1.Shiro共有5个注解，分别如下：

RequiresAuthentication：使用该注解标注的类，实例，方法在访问或调用时，当前Subject必须在当前session中已经过认证。

RequiresGuest：使用该注解标注的类，实例，方法在访问或调用时，当前Subject可以是“gust”身份，不需要经过认证或者在原先的session中存在记录。

RequiresPermissions：当前Subject需要拥有某些特定的权限时，才能执行被该注解标注的方法。如果当前Subject不具有这样的权限，则方法不会被执行。

RequiresRoles：当前Subject必须拥有所有指定的角色时，才能访问被该注解标注的方法。如果当天Subject不同时拥有所有指定角色，则方法不会执行还会抛出AuthorizationException异常。

RequiresUser：当前Subject必须是应用的用户，才能访问或调用被该注解标注的类，实例，方法。

2.Shiro的认证注解处理是有内定的处理顺序的，如果有个多个注解的话，前面的通过了会继续检查后面的，若不通过则直接返回，处理顺序依次为（与实际声明顺序无关）：
RequiresRoles
RequiresPermissions
RequiresAuthentication
RequiresUser
RequiresGuest

例如：你同时使用了了RequiresRoles和RequiresPermissions，那就要求拥有此角色的同时还得拥有相应的权限。

RequiresRoles可以用在Controller或者方法上。可以多个roles，默认逻辑为 AND也就是所有具备所有role才能访问。
示例：
//属于user角色
@RequiresRoles("user")

//必须同时属于user和admin角色
@RequiresRoles({"user","admin"})

//属于user或者admin之一。
@RequiresRoles(value={"user","admin"},logical=Logical.OR)

欢迎使用与反馈意见