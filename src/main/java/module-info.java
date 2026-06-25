module com.example.miniproyecto350zoedadleprabra {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.miniproyecto350zoedadleprabra to javafx.fxml;
    opens com.example.miniproyecto350zoedadleprabra.controller to javafx.fxml;

    exports com.example.miniproyecto350zoedadleprabra;
    exports com.example.miniproyecto350zoedadleprabra.controller;
    exports com.example.miniproyecto350zoedadleprabra.model;
    exports com.example.miniproyecto350zoedadleprabra.util;
    exports com.example.miniproyecto350zoedadleprabra.view;
}
