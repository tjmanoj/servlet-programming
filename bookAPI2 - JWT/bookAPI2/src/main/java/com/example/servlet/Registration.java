package com.example.servlet;

import com.example.database.DatabaseConnector;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/register")
public class Registration extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        BufferedReader reader = request.getReader();
        String line;

        StringBuilder responseContent = new StringBuilder();

        try{
            while((line = reader.readLine()) != null){
                responseContent.append(line);
            }
            reader.close();

            String jsonContent = responseContent.toString();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            if(jsonContent.isEmpty()){
                out.println("json object empty");
            }
            else{
                JSONObject jsonObject = new JSONObject(jsonContent);
                String userName = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                String role = jsonObject.getString("role");

                try{
                    Connection connection = DatabaseConnector.getDbConnection();
                    String insertQuery = "Insert into users(username,password,role) values(?,?,?)";

                    PreparedStatement statement = connection.prepareStatement(insertQuery);
                    statement.setString(1,userName);
                    statement.setString(2,password);
                    statement.setString(3,role);

                    statement.executeUpdate();

                    out.println(jsonObject);
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
