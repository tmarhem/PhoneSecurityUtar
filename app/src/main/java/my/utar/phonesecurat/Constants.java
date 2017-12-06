package my.utar.phonesecurat;

/**
 * TODO Work on Service
 * TODO Make BaseProfiling Activity working by extending the new GestureListener class from the package
 * TODO Make the windowsManager work via XML settings file
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
