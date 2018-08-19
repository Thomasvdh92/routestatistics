package com.tdimco.routestatistics.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLJDBCUtil {

    public static Connection getConnection() throws SQLException, IOException {
        Connection conn;
        String resourcename = "dbproperties.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(resourcename)) {
            props.load(resourceStream);
        }
        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
}
