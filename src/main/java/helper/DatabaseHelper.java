package helper;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2017/5/31.
 */
public class DatabaseHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final String FILE_NAME = "jdbc.properties";

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    static {
        Properties conf = PropsUtil.loadProps(FILE_NAME);
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                LOGGER.error("get connection fail", e);
            } finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    public static void closeConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }


    public static <T> List<T> queryEntityList(Class<T> clazz, String sql, Object... params) {
        List<T> entityList = null;
        Connection conn = getConnection();
        try {
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(clazz), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
        } finally {
            closeConnection();
        }
        return entityList;
    }

    public static <T> T queryEntity(Class<T> clazz, String sql, Object... params) {
        T entity = null;
        Connection conn = getConnection();
        try {
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(clazz), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
        } finally {
            closeConnection();
        }
        return entity;
    }


    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = getConnection();
        try {
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
        } finally {
            closeConnection();
        }
        return result;
    }

    public static int executeUpdate(String sql, Object... params) {
        int rows = 0;
        Connection conn = getConnection();
        try {
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
        } finally {
            closeConnection();
        }
        return rows;
    }

    public static <T> boolean inserEntity(Class<T> clazz , Map<String,Object> fieldMap){
        String sql = "insert into " + getTableName(clazz);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fileName:fieldMap.keySet()) {
            columns.append(fileName).append(",");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(), ")");
        values.replace(columns.lastIndexOf(", "),columns.length(), ")");
        sql += columns + " values " + values;
        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql,params) == 1;
    }

    public static <T> boolean updateEntity(Class<T> clazz , Map<String,Object> fieldMap, long id){
        String sql = "update " + getTableName(clazz) + " set ";
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fileName:fieldMap.keySet()) {
            columns.append(fileName).append("=?,");
        }
        sql += columns.substring(0,columns.lastIndexOf(", ")) + " where id =?";
        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        return executeUpdate(sql,params) == 1;
    }

    public static <T> boolean deleteEntity(Class<T> clazz, long id) {
        String sql = "delete from " + getTableName(clazz) + " where id = ?";
        return executeUpdate(sql, id) == 1;
    }

    private static String getTableName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
