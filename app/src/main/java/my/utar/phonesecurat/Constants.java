package my.utar.phonesecurat;

/**
 * TODO Work on Service
 * TODO Make BaseProfiling Activity working by extending the new GestureListener class from the package
 * TODO Make the windowsManager work via XML settings file
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "my.utar.phonesecurat.action.main";
        public static String STARTFOREGROUND_ACTION = "my.utar.phonesecurat.action.startForeground";
        public static String STOPFOREGROUND_ACTION = "my.utar.phonesecurat.action.stopForeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
