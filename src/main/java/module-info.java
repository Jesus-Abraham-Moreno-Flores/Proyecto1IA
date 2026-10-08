module uabc.computacion.proyectomedioia {
    requires javafx.controls;
    requires jade;

    opens uabc.computacion.proyectomedioia to javafx.graphics, jade;
    exports uabc.computacion.proyectomedioia;
}