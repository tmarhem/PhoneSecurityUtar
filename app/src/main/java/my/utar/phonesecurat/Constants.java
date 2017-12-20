package my.utar.phonesecurat;

/**
 *Saving class for Constants
 */

public class Constants {
    public interface ACTION {
        String MAIN_ACTION = "my.utar.phonesecurat.action.main";
        String START_FOREGROUND_ACTION = "my.utar.phonesecurat.action.startForeground";
        String STOP_FOREGROUND_ACTION = "my.utar.phonesecurat.action.stopForeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public interface TOAST{
        int CREATION = 1;
        int DESTRUCTION = 2;
    }
}
