package org.jivesoftware.database;

/**
 * Simple class for tracking profiling stats for individual SQL queries.
 *
 * @author Jive Software
 */
public class ProfiledConnectionEntry {
    /**
     * The SQL query.
     */
    public String sql;

    /**
     * Number of times the query has been executed.
     */
    public int count;

    /**
     * The total time spent executing the query (in milliseconds).
     */
    public int totalTime;

    public ProfiledConnectionEntry(String sql) {
        this.sql = sql;
        count = 0;
        totalTime = 0;
    }
}
