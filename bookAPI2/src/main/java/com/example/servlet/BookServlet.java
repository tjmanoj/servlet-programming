package com.example.servlet;

import com.example.model.Book;
import com.example.database.DatabaseConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/book")
public class BookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String bookIdParam = request.getParameter("bookId");
        if(bookIdParam == null) {
            try {
                Connection connection = DatabaseConnector.getDbConnection();
                Statement statement = connection.createStatement();

                String getBooksQuery = "select * from book";

                ResultSet rs = statement.executeQuery(getBooksQuery);

                JSONArray jsonArray = new JSONArray();

                while (rs.next()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bookId", rs.getInt("bookId"));
                    jsonObject.put("bookName", rs.getString("bookName"));
                    jsonObject.put("bookAuthor", rs.getString("bookAuthor"));

                    jsonArray.put(jsonObject);
                }

                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println(jsonArray);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        else{
            int bookId = Integer.parseInt(bookIdParam);
            try{
                Connection connection = DatabaseConnector.getDbConnection();

                String getBookByIdQuery = "select * from book where bookId=?";

                PreparedStatement preparedStatement = connection.prepareStatement(getBookByIdQuery);
                preparedStatement.setInt(1,bookId);

                ResultSet rs = preparedStatement.executeQuery();
                if(!rs.next()){
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println("Book not found");
                }

                else{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bookId",bookId);
                    jsonObject.put("bookName",rs.getString("bookName"));
                    jsonObject.put("bookAuthor",rs.getString("bookAuthor"));

                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println(jsonObject);
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        String line;
        StringBuilder responseContent = new StringBuilder();

        try {
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            String jsonContent = responseContent.toString();

            if (jsonContent.isEmpty()) {
                out.println("JSON object empty");
            }
            else {
                JSONObject jsonObject = new JSONObject(jsonContent);
                String bookName = jsonObject.getString("bookName");
                String bookAuthor = jsonObject.getString("bookAuthor");

                try {
                    Connection connection = DatabaseConnector.getDbConnection();

                    String insertQuery = "Insert into book(bookName,bookAuthor) values(?,?)";

                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1,bookName);
                    preparedStatement.setString(2,bookAuthor);

                    preparedStatement.executeUpdate();

                    out.println(jsonObject);


                } catch (SQLException e) {
                    out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String bookIdParam = request.getParameter("bookId");

        BufferedReader reader = request.getReader();
        String line;
        StringBuilder responseContent = new StringBuilder();

        try{
            int bookId = Integer.parseInt(bookIdParam);
            while((line = reader.readLine()) != null){
                responseContent.append(line);
            }
            reader.close();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            String jsonContent = responseContent.toString();

            if(jsonContent.isEmpty()){
                out.println("JSON object empty");
            }
            else{
                JSONObject jsonObject = new JSONObject(jsonContent);

                String bookName = jsonObject.optString("bookName",null);
                String bookAuthor = jsonObject.optString("bookAuthor",null);

                String updateBookQuery = "UPDATE book SET bookName = COALESCE(?, bookName), bookAuthor = COALESCE(?, bookAuthor) WHERE bookId = ?";
                Connection connection = DatabaseConnector.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(updateBookQuery);
                statement.setString(1, bookName);
                statement.setString(2, bookAuthor);
                statement.setInt(3, bookId);

                statement.executeUpdate();

                String resultQuery = "select * from book where bookId=?";

                PreparedStatement statement1 = connection.prepareStatement(resultQuery);
                statement1.setInt(1,bookId);


                ResultSet rs = statement1.executeQuery();
                if(!rs.next()){
                    response.setContentType("application/json");
                    out.println("Book not found");
                }

                else{
                    JSONObject resultJsonObject = new JSONObject();
                    resultJsonObject.put("bookId",bookId);
                    resultJsonObject.put("bookName",rs.getString("bookName"));
                    resultJsonObject.put("bookAuthor",rs.getString("bookAuthor"));

                    out.println(resultJsonObject);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String bookIdParam = request.getParameter("bookId");

        try{

            int bookId = Integer.parseInt(bookIdParam);
            System.out.println(bookId);
            String deleteQuery = "delete from book where bookId=?";

            Connection connection = DatabaseConnector.getDbConnection();
            PreparedStatement statement = connection.prepareStatement(deleteQuery);

            statement.setInt(1,bookId);

            int affectedRows = statement.executeUpdate();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            if(affectedRows > 0){
                out.println("Book Deleted successfully");
            }
            else{
                out.println("Book not found");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

