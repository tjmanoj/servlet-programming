package com.example.servlet;

import com.example.database.DatabaseConnector;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class Login extends HttpServlet {
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

                try{
                    Connection connection = DatabaseConnector.getDbConnection();
                    String query = "select password,role from users where username=?";

                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1,userName);

                    ResultSet rs = statement.executeQuery();

                    if(rs.next() && rs.getString("password").equals(password)){
                        String role = rs.getString("role");
                        String token = Jwts.builder().setSubject(userName).claim("role",role).signWith(SignatureAlgorithm.HS256,
                                "mykey").compact();

                        out.println("Token:\n" + token);
                    }
                    else{
                        out.println("Invalid login credentials");
                    }

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
