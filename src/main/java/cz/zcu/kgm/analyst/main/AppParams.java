package cz.zcu.kgm.analyst.main;

import java.text.SimpleDateFormat;

/**
 * Singleton class with application parameters
 * @author mkepka
 *
 */
public class AppParams {

    /**
     * Date formatter for pattern: 'yyyy-MM-dd HH:mm:ssZ'
     */
    public static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
}
