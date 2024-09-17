package com.example.database;

import java.sql.*;

public class DatabaseConnector {

    public static Connection getDbConnection(){

        Connection connection = null;
        String db_url = "jdbc:mysql://127.0.0.1:3306/books_schema";
        String db_user = "root";
        String db_password = "";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(db_url,db_user,db_password);

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }
}
