module com.example.c195 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    opens com.example.c195 to javafx.fxml;
    exports com.example.c195;
    exports com.example.c195.controller;
    opens com.example.c195.controller to javafx.fxml;
    exports com.example.c195.model;
    opens com.example.c195.model to javafx.base, javafx.fxml;
    opens com.example.c195.DAO to javafx.fxml;
}