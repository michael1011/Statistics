package eu.michael1011.statistics.commands;

import eu.michael1011.statistics.main.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

import static eu.michael1011.statistics.commands.Stats.*;

class GetData {

    static void getPlayerData(String id) {
        ResultSet rs = SQL.getResult("select * from stats where uuid='"+id+"'");

        try {
            assert rs != null;

            if(rs.next()) {
                ipAddress = rs.getString(2);
                timesConnected = rs.getInt(3);
                onlineTime = rs.getInt(4);
                lastSeen = rs.getString(5);
                firstJoin = rs.getString(6);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    static void getLifeData(String id) {
        ResultSet rs = SQL.getResult("select * from stats_life where uuid='"+id+"'");

        try {
            assert rs != null;

            if(rs.next()) {
                deaths = rs.getInt(2);
                killed = rs.getInt(3);
                killsEntities = rs.getInt(4);
                killsPlayers = rs.getInt(5);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    static void getServerData() {
        ResultSet rs = SQL.getResult("select * from stats_server");

        try {
            assert rs != null;

            rs.afterLast();

            if(rs.previous()) {
                time = rs.getString(1);
                tps = rs.getString(2);
                online = rs.getString(3);
                playersOnline = rs.getInt(4);
                ram = rs.getInt(5);
                freeRam = rs.getInt(6);
                ramUsage = rs.getString(7);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
