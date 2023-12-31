package com.example.c195.DAO;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.example.c195.DAO.DBConnection.conn;
public class Query {
    private static String query;
    private static Statement stmt;
    private static ResultSet result;

    public static void makeQuery(String q){
        query=q;
        try{
            stmt=conn.createStatement();

            if(query.toLowerCase().startsWith("select"))
                result=stmt.executeQuery(q);
            if(query.toLowerCase().startsWith("delete")||query.toLowerCase().startsWith("insert")||query.toLowerCase().startsWith("update"))
                stmt.executeUpdate(q);


        }
        catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
        }
    }
    public static ResultSet getResult(){
        return result;
    }

    public static void prepare(String sqlStatement) {
    }

    public static void setParam(String userName) {
    }
}
