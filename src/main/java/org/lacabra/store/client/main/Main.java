package org.lacabra.store.client.main;

import org.lacabra.store.client.Controller.MainController;
import org.lacabra.store.client.windows.WindowHome;

public class Main {
    public static void main(String[] args) {
        MainController mc= new MainController();
        WindowHome wh=new WindowHome(null, mc);
    }
}