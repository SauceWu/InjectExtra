package me.sauce.rxBus;

/**
 * Created by sauce on 2017/5/18.
 */

public class RxBus {
    public static RxBus INSTANCE;

    private RxBus() {
    }

    public static RxBus getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxBus();
                }
            }
        }
        return INSTANCE;
    }

    private void sujecg (){

    }
}
