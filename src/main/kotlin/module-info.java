module PR.Simulation.Foxes {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires tornadofx;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;
    exports app to javafx.graphics, tornadofx;
    exports view to tornadofx;
    exports controller to tornadofx;
}