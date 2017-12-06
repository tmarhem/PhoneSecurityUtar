package my.utar.phonesecurat;

/**
 * Created by tmarh on 06/12/2017.
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
