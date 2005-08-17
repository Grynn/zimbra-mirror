package com.liquidsys.coco.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liquidsys.coco.db.DbPool.Connection;
import com.liquidsys.coco.service.ServiceException;

/**
 * <code>DbUtil</code> contains some database utility methods and
 * automates the task of running SQL statements.  It
 * wraps the JDBC <code>Connection</code>and <code>PreparedStatement</code>
 * classes and frees the caller from having to handle <code>SQLException</code>
 * and allocate and deallocate database resources.<p>
 * 
 * Query results are read entirely into memory and returned in the form of a
 * {@link com.liquidsys.coco.db.DbResults} object.  This improves concurrency,
 * but potentially increases memory consumption.  Code that deals with
 * large result sets should use the JDBC API's directly.
 * 
 * @author bburtin
 */
public class DbUtil {

    public static final int INTEGER_TRUE = 1;
    public static final int INTEGER_FALSE = 0;
    
    public static final int getBooleanIntValue(boolean b) {
        if (b) {
            return INTEGER_TRUE;
        }
        return INTEGER_FALSE;
    }
    
    private static final Pattern PAT_ESCAPED_QUOTES1 = Pattern.compile("\\\\'");
    private static final Pattern PAT_ESCAPED_QUOTES2 = Pattern.compile("''");
    private static final Pattern PAT_STRING_CONSTANT = Pattern.compile("'[^']+'");
    private static final Pattern PAT_INTEGER_CONSTANT = Pattern.compile("\\d+");
    private static final Pattern PAT_NULL_CONSTANT = Pattern.compile("NULL");
    private static final Pattern PAT_BEGIN_SPACE = Pattern.compile("^\\s+");
    private static final Pattern PAT_TRAILING_SPACE = Pattern.compile("\\s+$");
    private static final Pattern PAT_MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern PAT_IN_CLAUSE = Pattern.compile("IN \\([^\\)]+\\)", Pattern.DOTALL);

    /**
     * Converts the given <code>SQL</code> string to a normalized version.
     * Strips out any numbers, string constants and extra whitespace.  Flattens
     * the contents of <code>IN</code> clauses.
     */
    public static String normalizeSql(String sql) {
        Matcher matcher = PAT_ESCAPED_QUOTES1.matcher(sql);
        sql = matcher.replaceAll("");
        matcher = PAT_ESCAPED_QUOTES2.matcher(sql);
        sql = matcher.replaceAll("XXX");
        matcher = PAT_STRING_CONSTANT.matcher(sql);
        sql = matcher.replaceAll("XXX");
        matcher = PAT_INTEGER_CONSTANT.matcher(sql);
        sql = matcher.replaceAll("XXX");
        matcher = PAT_NULL_CONSTANT.matcher(sql);
        sql = matcher.replaceAll("XXX");
        matcher = PAT_BEGIN_SPACE.matcher(sql);
        sql = matcher.replaceAll("");
        matcher = PAT_TRAILING_SPACE.matcher(sql);
        sql = matcher.replaceAll("");
        matcher = PAT_MULTIPLE_SPACES.matcher(sql);
        sql = matcher.replaceAll(" ");
        matcher = PAT_IN_CLAUSE.matcher(sql);
        sql = matcher.replaceAll("IN (...)");
        return sql;
    }

    /**
     * Executes the specified query using the specified connection.
     * 
     * @throws ServiceException if the query cannot be executed or an error
     * occurs while retrieving results
     */
    public static DbResults executeQuery(Connection conn, String query, Object[] params)
    throws ServiceException {
        PreparedStatement stmt = null;
        DbResults results = null;
        
        try {
            stmt = conn.prepareStatement(query);
            
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            ResultSet rs = stmt.executeQuery();
            results = new DbResults(rs);
        } catch (SQLException e) {
            String message = "SQL: '" + query + "'";
            throw ServiceException.FAILURE(message, e);
        } finally {
            DbPool.closeStatement(stmt);
        }
        
        return results;
    }
    
    /**
     * Executes the specified query using a connection from the connection pool.
     * 
     * @throws ServiceException if the query cannot be executed or an error
     * occurs while retrieving results
     */
    public static DbResults executeQuery(Connection conn, String query, Object param)
    throws ServiceException {
        Object[] params = { param };
        return executeQuery(conn, query, params);
    }
    
    /**
     * Executes the specified query using the specified connection.
     * 
     * @throws ServiceException if the query cannot be executed or an error
     * occurs while retrieving results
     */
    public static DbResults executeQuery(Connection conn, String query)
    throws ServiceException {
        return executeQuery(conn, query, null);
    }
    
    /**
     * Executes the specified query using a connection from the connection pool.
     * 
     * @throws ServiceException if the query cannot be executed or an error
     * occurs while retrieving results
     */
    public static DbResults executeQuery(String query, Object[] params)
    throws ServiceException {
        Connection conn = null;
        try {
            conn = DbPool.getConnection();
            return executeQuery(conn, query, params);
        } finally {
            DbPool.quietClose(conn);
        }
    }
    
    /**
     * Executes the specified query using a connection from the connection pool.
     * 
     * @throws ServiceException if the query cannot be executed or an error
     * occurs while retrieving results
     */
    public static DbResults executeQuery(String query, Object param)
    throws ServiceException {
        Object[] params = { param };
        return executeQuery(query, params);
    }
    
    /**
     * Executes the specified query using a connection from the connection pool.
     * 
     * @throws ServiceException if the query cannot be executed or an error
     * occurs while retrieving results
     */
    public static DbResults executeQuery(String query)
    throws ServiceException {
        return executeQuery(query, null);
    }
    
    /**
     * Executes the specified update using the specified connection.
     * 
     * @return the number of rows affected
     * @throws ServiceException if the update cannot be executed
     */
    public static int executeUpdate(Connection conn, String sql, Object[] params)
    throws ServiceException {
        PreparedStatement stmt = null;
        int numRows = 0;
        
        try {
            stmt = conn.prepareStatement(sql);
            
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            numRows = stmt.executeUpdate();
        } catch (SQLException e) {
            String message = "SQL: '" + sql + "'";
            throw ServiceException.FAILURE(message, e);
        } finally {
            DbPool.closeStatement(stmt);
        }
        
        return numRows;
    }
    
    /**
     * Executes the specified update using the specified connection.
     * 
     * @return the number of rows affected
     * @throws ServiceException if the update cannot be executed
     */
    public static int executeUpdate(Connection conn, String sql, Object param)
    throws ServiceException {
        Object[] params = { param };
        return executeUpdate(conn, sql, params);
    }
    
    /**
     * Executes the specified update using the specified connection.
     * 
     * @return the number of rows affected
     * @throws ServiceException if the update cannot be executed
     */
    public static int executeUpdate(Connection conn, String sql)
    throws ServiceException {
        return executeUpdate(conn, sql, null);
    }
    
    /**
     * Executes the specified update using a connection from the connection pool.
     * 
     * @return the number of rows affected
     * @throws ServiceException if the update cannot be executed
     */
    public static int executeUpdate(String sql, Object[] params)
    throws ServiceException {
        Connection conn = DbPool.getConnection();
        try {
            int numRows = executeUpdate(conn, sql, params);
            conn.commit();
            return numRows;
        } finally {
            DbPool.quietClose(conn);
        }
    }
    
    /**
     * Executes the specified update using a connection from the connection pool.
     * 
     * @return the number of rows affected
     * @throws ServiceException if the update cannot be executed
     */
    public static int executeUpdate(String sql, Object param)
    throws ServiceException {
        Object[] params = { param };
        return executeUpdate(sql, params);
    }
    
    public static int executeUpdate(String sql)
    throws ServiceException {
        return executeUpdate(sql, null);
    }
}
