package com.example.servlet;

import com.example.model.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

@WebServlet("/book")
public class BookServlet extends HttpServlet {
    private final ArrayList<Book> bookList = new ArrayList<>();

    public void init() throws ServletException{
        try{
            Book book1 = new Book(1,"Adventures of Tom Sawyer","Mark Twain");
            Book book2 = new Book(2,"Animal Farm","George Orwell");
            bookList.add(book1);
            bookList.add(book2);
        }
        catch(Exception e){
            throw new ServletException("Initialization failed");
        }

    }


    private Book getBookById(int bookId){
        for(Book book : bookList){
            if(book.getBookId() == bookId){
                return book;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String bookIdParam = request.getParameter("bookId");
        if(bookIdParam == null){
            JSONArray jsonArray = new JSONArray();


            for(Book book : bookList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("bookId", book.getBookId());
                jsonObject.put("bookAuthor", book.getBookAuthor());
                jsonObject.put("bookName", book.getBookName());

                jsonArray.put(jsonObject);
            }
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(jsonArray);
        }

        else{
            try{
                int bookId = Integer.parseInt(bookIdParam);
                Book book = getBookById(bookId);

                if(book == null){
                    response.setStatus(404);
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println("Book not found!");
                }
                else{
                    JSONObject bookJson = new JSONObject();
                    bookJson.put("bookId", book.getBookId());
                    bookJson.put("bookName", book.getBookName());
                    bookJson.put("bookAuthor", book.getBookAuthor());

                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println(bookJson);
                }
            }
            catch (NumberFormatException e){
                response.setStatus(404);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println("Invalid bookId format");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        String line;
        StringBuilder responseContent = new StringBuilder();

        try{
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

                int bookId = jsonObject.getInt("bookId");
                String bookName = jsonObject.getString("bookName");
                String bookAuthor = jsonObject.getString("bookAuthor");

                Book newBook = new Book(bookId, bookName, bookAuthor);
                bookList.add(newBook);
                out.println(jsonObject);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String bookIdParam = request.getParameter("bookId");

        int bookId = Integer.parseInt(bookIdParam);
        Book book = getBookById(bookId);

        if(book == null){
            response.setStatus(404);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("Book not found");
        }
        else{
            BufferedReader reader = request.getReader();
            String line;
            StringBuilder responseContent = new StringBuilder();

            try{
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
                    if(bookName != null){
                        book.setBookName(bookName);
                    }

                    String bookAuthor = jsonObject.optString("bookAuthor",null);
                    if(bookAuthor != null){
                        book.setBookAuthor(bookAuthor);
                    }

                    JSONObject updatedBook = new JSONObject();
                    updatedBook.put("bookId", bookId);
                    updatedBook.put("bookName",book.getBookName());
                    updatedBook.put("bookAuthor",book.getBookAuthor());

                    out.println(updatedBook);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String bookIdParam = request.getParameter("bookId");

        int bookId = Integer.parseInt(bookIdParam);
        Book book = getBookById(bookId);

        if(book == null){
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("Book not found");
        }
        else{
            bookList.remove(book);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("Book removed successfully");
        }
    }
}
