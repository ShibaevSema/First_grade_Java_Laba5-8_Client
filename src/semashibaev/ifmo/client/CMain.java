package semashibaev.ifmo.client;

import semashibaev.ifmo.client.graphics.WindowRegistration;

public class CMain {
    public static void main(String[] args) throws InterruptedException, Exception {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        WindowRegistration wr = new WindowRegistration();
        wr.setVisible(true);
    }
}
