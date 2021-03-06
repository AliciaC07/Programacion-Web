package org.practica.services;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BootStrapService {
    private static Server server;


    public static void startDb()  {
        try {
            server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers", "-ifNotExists").start();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }


    public static void stopDb() throws SQLException {
        if(server!=null) {
            server.stop();
        }
    }

    public static void createTables() throws SQLException {
        Connection connection = DataBaseService.getInstance().getConnection();
        String sql_user = "create table IF NOT EXISTS USER\n" +
                "(\n" +
                "\tID INT auto_increment,\n" +
                "\tUSER_NAME VARCHAR(100) not null,\n" +
                "\tPASSWORD VARCHAR(200) not null,\n" +
                "\tconstraint USER_PK\n" +
                "\t\tprimary key (ID)\n" +
                ");\n" +
                "\n" +
                "create unique  index IF NOT EXISTS USER_ID_UINDEX\n" +
                "\ton USER (ID);\n" +
                "\n" +
                "create unique index IF NOT EXISTS USER_USER_NAME_UINDEX\n" +
                "\ton USER (USER_NAME);";
        String sql_client = "create table IF NOT EXISTS CLIENT\n" +
                "(\n" +
                "\tID int auto_increment,\n" +
                "\tEMAIL varchar(200) not null,\n" +
                "\tNAME VARCHAR(100),\n" +
                "\tLAST_NAME VARCHAR(100)\n" +
                ");\n" +
                "\n" +
                "create unique index IF NOT EXISTS CLIENT_EMAIL_UINDEX\n" +
                "\ton CLIENT (EMAIL);\n" +
                "\n" +
                "create unique index IF NOT EXISTS CLIENT_ID_UINDEX\n" +
                "\ton CLIENT (ID);\n" +
                "\n" +
                "alter table CLIENT\n" +
                "\tadd constraint IF NOT EXISTS CLIENT_PK\n" +
                "\t\tprimary key (ID);\n";
        String sql_product ="create table IF NOT EXISTS PRODUCT\n" +
                "(\n" +
                "\tID INT auto_increment,\n" +
                "\tNAME VARCHAR(200) not null,\n" +
                "\tPRICE FLOAT not null,\n" +
                "\tAMOUNT INT not null,\n" +
                "\tACTIVE BOOLEAN default true not null,\n" +
                "\tconstraint PRODUCT_PK\n" +
                "\t\tprimary key (ID)\n" +
                ");\n" +
                "\n" +
                "create unique index IF NOT EXISTS PRODUCT_ID_UINDEX\n" +
                "\ton PRODUCT (ID);\n" +
                "\n" +
                "create unique index IF NOT EXISTS PRODUCT_ID_UINDEX_2\n" +
                "\ton PRODUCT (ID);";
        String sql_receipt ="create table IF NOT EXISTS RECEIPT\n" +
                "(\n" +
                "\tID INT auto_increment,\n" +
                "\tDATE DATE not null,\n" +
                "\tEMAIL_CLIENT VARCHAR(150) not null,\n" +
                "\tID_USER INT not null,\n" +
                "\tTOTAL FLOAT not null,\n" +
                "\tconstraint RECEIPT_PK\n" +
                "\t\tprimary key (ID),\n" +
                "\tconstraint RECEIPT_CLIENT_ID_FK\n" +
                "\t\tforeign key (EMAIL_CLIENT) references CLIENT (EMAIL),\n" +
                "\tconstraint RECEIPT_USER_ID_FK\n" +
                "\t\tforeign key (ID_USER) references USER (ID)\n" +
                ");\n" +
                "\n" +
                "create unique index IF NOT EXISTS RECEIPT_ID_UINDEX\n" +
                "\ton RECEIPT (ID);";
        String sql_receipt_product = "create table IF NOT EXISTS RECEIPT_PRODUCT\n" +
                "(\n" +
                "ID_RECEIPT int not null,\n" +
                "\tID_PRODUCT int not null,\n" +
                "\tAMOUNT int not null,\n" +
                "\tPRICE FLOAT not null,\n" +
                "\tconstraint RECEIPT_PRODUCT_PRODUCT_ID_FK\n" +
                "\t\tforeign key (ID_PRODUCT) references PRODUCT,\n" +
                "\tconstraint RECEIPT_PRODUCT_RECEIPT_ID_FK\n" +
                "\t\tforeign key (ID_RECEIPT) references RECEIPT" +

                ");";
        String sql_cookie_verification = "create table IF NOT EXISTS COOKIE_VERIFICATION\n" +
                "(\n" +
                "\tID INT auto_increment,\n" +
                "\tUSERNAME VARCHAR(150) not null,\n" +
                "\tTOKEN VARCHAR not null,\n" +
                "\tDATE_CREATED DATE not null,\n" +
                "\tDATE_UPDATED DATE\n" +
                ");\n" +
                "\n" +
                "create unique index IF NOT EXISTS COOKIE_VERIFICATION_ID_UINDEX\n" +
                "\ton COOKIE_VERIFICATION (ID);\n" +
                "\n" +
                "alter table COOKIE_VERIFICATION\n" +
                "\tadd constraint IF NOT EXISTS COOKIE_VERIFICATION_PK\n" +
                "\t\tprimary key (ID);";

        Statement statement = connection.createStatement();
        statement.execute(sql_user);
        statement.execute(sql_client);
        statement.execute(sql_product);
        statement.execute(sql_receipt);
        statement.execute(sql_receipt_product);
        statement.execute(sql_cookie_verification);
        statement.close();
        connection.close();

    }


}
