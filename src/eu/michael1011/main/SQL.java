package eu.michael1011.main;

import java.sql.*;

import static eu.michael1011.main.Main.config;

public class SQL {

    public static Connection connection;

    private static String host, port, database, username, password;

    public static void establishMySQL() {

        host = config.getString("MySQL.host");
        port = config.getString("MySQL.port");
        database = config.getString("MySQL.database");
        username = config.getString("MySQL.username");
        password = config.getString("MySQL.password");

        try {
            openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                + host+ ":" + port + "/" + database, username, password);

        return connection;
    }

    public static void update(String s) {
        try {
            PreparedStatement ps = connection.prepareStatement(s);
            ps.execute();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static ResultSet getResult(String s) {
        try {
            PreparedStatement ps = connection.prepareStatement(s);
            return ps.executeQuery();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
