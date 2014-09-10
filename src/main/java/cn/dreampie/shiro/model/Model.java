package cn.dreampie.shiro.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

import java.util.Date;
import java.util.List;

/**
 * Created by wangrenhui on 2014/7/1.
 */
public abstract class Model<M extends Model> extends com.jfinal.plugin.activerecord.Model<M> {

  private Table table;
  private String tableName;
  private String primaryKey;
  private String modelName;

  private String selectSql;
  private String exceptSelectSql;
  private String updateSql;
  private String deleteSql;
  private String dropSql;
  private String countSql;

  protected static String blank = " ";

  public List<M> findAll() {
    return find(getSelectSql() + getExceptSelectSql());
  }

  public List<M> findBy(String where, Object... paras) {
    return find(getSelectSql() + getExceptSelectSql() + getWhere(where), paras);
  }

  public List<M> findTopBy(int topNumber, String where, Object... paras) {
    return paginate(1, topNumber, getSelectSql(), getExceptSelectSql() + getWhere(where), paras).getList();
  }

  public M findFirstBy(String where, Object... paras) {
    return findFirst(getSelectSql() + getExceptSelectSql() + getWhere(where), paras);
  }

  public Page<M> paginateAll(int pageNumber, int pageSize) {
    return paginate(pageNumber, pageSize, getSelectSql(), getExceptSelectSql());
  }

  public Page<M> paginateBy(int pageNumber, int pageSize, String where, Object... paras) {
    return paginate(pageNumber, pageSize, getSelectSql(), getExceptSelectSql() + getWhere(where), paras);
  }

  public boolean updateAll(String set, Object... paras) {
    return Db.update(getUpdateSql() + getSet(set), paras) > 0;
  }

  public boolean updateBy(String set, String where, Object... paras) {
    return Db.update(getUpdateSql() + getSet(set) + getWhere(where), paras) > 0;
  }

  public boolean deleteAll() {
    return Db.update(getDeleteSql()) > 0;
  }

  public boolean deleteBy(String where, Object... paras) {
    return Db.update(getDeleteSql() + getWhere(where), paras) > 0;
  }

  public boolean dropAll() {
    return Db.update(getDropSql()) > 0;
  }

  public boolean dropBy(String where, Object... paras) {
    return Db.update(getDropSql() + getWhere(where), paras) > 0;
  }

  public long countAll() {
    long result = Db.queryFirst(getCountSql());
    return result;
  }

  public long countBy(String where, Object... paras) {
    long result = Db.queryFirst(getCountSql() + getWhere(where), paras);
    return result;
  }

  protected String getSet(String set) {
    if (set != null && !set.isEmpty() && !set.trim().toUpperCase().startsWith("SET")) {
      set = " SET " + set;
    }
    return set;
  }

  protected String getWhere(String where) {
    if (where != null && !where.isEmpty() && !where.trim().toUpperCase().startsWith("WHERE")) {
      where = " WHERE " + where;
    }
    return where;
  }

  public Table getTable() {
    if (table == null) {
      Class clazz = getClass();
      table = TableMapping.me().getTable(clazz);
    }
    return table;
  }

  public String getPrimaryKey() {
    if (primaryKey == null) {
      primaryKey = getTable().getPrimaryKey();
    }
    return primaryKey;
  }

  public String getTableName() {
    if (tableName == null) {
      tableName = getTable().getName();
    }
    return tableName;
  }

  public String getModelName() {
    if (modelName == null) {
      Class clazz = getClass();
      byte[] items = clazz.getSimpleName().getBytes();
      items[0] = (byte) ((char) items[0] + ('a' - 'A'));
      modelName = new String(items);
    }
    return modelName;
  }

  public String getSelectSql() {
    if (selectSql == null) {
      selectSql = " SELECT `" + getModelName() + "`.* ";
    }
    return selectSql;
  }

  public String getExceptSelectSql() {
    if (exceptSelectSql == null) {
      exceptSelectSql = " FROM " + getTableName() + " `" + getModelName() + "` ";
    }
    return exceptSelectSql;
  }

  public String getUpdateSql() {
    if (updateSql == null) {
      updateSql = " UPDATE " + getTableName() + " `" + getModelName() + "` ";
    }
    return updateSql;
  }

  public String getDeleteSql() {
    if (deleteSql == null) {
      deleteSql = " UPDATE " + getTableName() + " `" + getModelName() + "` SET `" + getModelName() + "`.deleted_at='" + new Date() + "' ";
    }
    return deleteSql;
  }

  public String getDropSql() {
    if (dropSql == null) {
      dropSql = " DELETE FROM " + getTableName() + " ";
    }
    return dropSql;
  }

  public String getCountSql() {
    if (countSql == null) {
      countSql = " SELECT COUNT(*) count FROM " + getTableName() + " `" + getModelName() + "` ";
    }
    return countSql;
  }

  public String getNextSql(String where) {
    String nextSql = " WHERE `" + getModelName() + "`." + getPrimaryKey()
        + "=(SELECT MIN(`_" + getModelName() + "`." + getPrimaryKey() + ") FROM " + getTableName() + " `_" + getModelName() + "`" + getWhere(where) + ")";

    return nextSql;
  }

  public String getPreviousSql(String where) {
    String previousSql = " WHERE `" + getModelName() + "`." + getPrimaryKey()
        + "=(SELECT MAX(`_" + getModelName() + "`." + getPrimaryKey() + ") FROM " + getTableName() + " `_" + getModelName() + "`" + getWhere(where) + ")";
    return previousSql;
  }
}
