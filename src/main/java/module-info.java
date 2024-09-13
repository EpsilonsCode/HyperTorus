module com.example.threetorusjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens com.omicron.hypertorus to javafx.fxml;
    exports com.omicron.hypertorus;
}