package com.dimipet.jcesd;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class JCESDApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new JCESDView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JCESDApp
     */
    public static JCESDApp getApplication() {
        return Application.getInstance(JCESDApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(JCESDApp.class, args);
    }
}
