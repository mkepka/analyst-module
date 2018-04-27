package cz.zcu.kgm.analyst.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for parsing incoming timestamps as String to Date 
 * @author mkepka
 *
 */
public class DateUtil {

    //--------------------------------------------------------------------------------------------
    public static final String patternSecs = "yyyy-MM-dd HH:mm:ss";
    public static final int patternSecsLen = 19;
    /**
     * yyyy-MM-dd HH:mm:ss
     * length=19
     */
    public static SimpleDateFormat formatSecs = new SimpleDateFormat(patternSecs);
    //--------------------------------------------------------------------------------------------
    public static final String patternSecsWT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final int patternSecsWTLen = 19;
    /**
     * yyyy-MM-dd'T'HH:mm:ss
     * length=19
     */
    public static SimpleDateFormat formatSecsWT = new SimpleDateFormat(patternSecsWT);
    //--------------------------------------------------------------------------------------------
    public static final String patternMiliSecs = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final int patternMiliSecsLen = 23;
    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     * length=23
     */
    public static SimpleDateFormat formatMiliSecs = new SimpleDateFormat(patternMiliSecs);
    //--------------------------------------------------------------------------------------------
    public static final String patternMiliSecsWT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final int patternMiliSecsWTLen = 23;
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS
     * length=23
     */
    public static SimpleDateFormat formatMiliSecsWT = new SimpleDateFormat(patternMiliSecsWT);
    //--------------------------------------------------------------------------------------------
    public static final String patternSecsTZ = "yyyy-MM-dd HH:mm:ssZ";
    public static final int patternSecsTZLen = 24;
    /**
     * yyyy-MM-dd HH:mm:ssZ
     * length=24
     */
    public static SimpleDateFormat formatSecsTZ = new SimpleDateFormat(patternSecsTZ);
    //--------------------------------------------------------------------------------------------
    public static final String patternSecsTZwT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final int patternSecsTZwTLen = 24;
    /**
     * yyyy-MM-ddTHH:mm:ssZ
     * length=24
     */
    public static SimpleDateFormat formatSecsTZwT = new SimpleDateFormat(patternSecsTZwT);
    //--------------------------------------------------------------------------------------------
    public static final String patternISO = "yyyy-MM-dd HH:mm:ssZZ:ZZ";
    public static final int patternISOLen = 25;
    //--------------------------------------------------------------------------------------------
    public static final String patternMicroSec = "yyyy-MM-dd HH:mm:ss.SSSsss";
    public static final int patternMicroSecLen = 26;
    //--------------------------------------------------------------------------------------------
    public static final String patternMiliSecsTZ = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final int patternMiliSecsTZLen = 28;
    /**
     * yyyy-MM-dd HH:mm:ss.SSSZ
     * length=28
     */
    public static SimpleDateFormat formatMiliSecsTZ = new SimpleDateFormat(patternMiliSecsTZ);
    //--------------------------------------------------------------------------------------------
    public static final String patternMiliSecsTZwT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public int patternMiliSecsTZwTLen = 28;
    /**
     * yyyy-MM-ddTHH:mm:ss.SSSZ
     * length=28
     */
    public static SimpleDateFormat formatMiliSecsTZwT = new SimpleDateFormat(patternMiliSecsTZwT);
    //--------------------------------------------------------------------------------------------
    public static final String patternISOMilis = "yyyy-MM-dd HH:mm:ss.SSSZZ:ZZ";
    public static final int patternISOMilisLen = 29;
    //--------------------------------------------------------------------------------------------
    public static final String patternMicroSecTZ = "yyyy-MM-dd HH:mm:ss.SSSsssZ";
    public static final int patternMicroSecTZLen = 31;
    //--------------------------------------------------------------------------------------------
    
    /**
     * Method parses String timestamp to Date
     * uses several patterns to test correct format to parse
     * @param time - timestamp as String
     * @return Timestamp as Date
     * @throws ParseException 
     */
    public static Date parseTimestamp(String time) throws ParseException{
        /**
         * Checks whether timestring contains UTC time zone identifier
         */
        boolean containsZ = time.contains("Z");
        if(containsZ){
            time = time.replace("Z", "+0000");
        }
        /**
         * checks if the timestring ends with "+hh" or "-hh" time zone definition
         * modifies timestring with minutes to time zone
         */
        char suffixTZ = time.charAt(time.length()-3);
        if(suffixTZ == '+' || suffixTZ == '-'){
            time = time+"00";
        }
        int len = time.length();
        /**
         * checks patterns ending with seconds only
         */
        if(len == patternSecsLen){
            return parseTimestampWithSeconds(time);
        }
        /**
         * checks patterns ending with milliseconds
         */
        else if(len == patternMiliSecsLen){
            return parseTimestringWithMillis(time);
        }
        /**
         * checks patterns ending with seconds and time zone
         */
        else if(len == patternSecsTZLen){
            return parseTimestampWithSecondsWithTimeZone(time);
        }
        /**
         * checks patterns ending with milliseconds and time zone
         */
        else if(len == patternMiliSecsTZLen){
            return parseTimestringWithMillisWithTimeZone(time);
        }
        /**
         * checks patterns according to ISO pattern
         */
        else if(len == patternISOLen){
            return parseISOTimestring(time);
        }
        /**
         * checks patterns according to ISO pattern with milliseconds
         */
        else if(len == patternISOMilisLen){
            return parseISOTimestringWithMillis(time);
        }
        /**
         * checks patterns ending with microseconds
         */
        else if(len == patternMicroSecLen){
            return parseTimestringWithMicros(time);
        }
        /**
         * checks patterns ending with microseconds and time zone
         */
        else if(len == patternMicroSecTZLen){
            return parseTimestringWithMicrosWithTimeZone(time);
        }
        
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }

    /**
     * Method parses timestring containing seconds only
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseTimestampWithSeconds(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        if(!containsT && containsSpace){
            try {
                Date date = formatSecs.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else if(containsT && !containsSpace){
            try {
                Date date = formatSecsWT.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring containing seconds and time zone
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseTimestampWithSecondsWithTimeZone(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        if(!containsT && containsSpace){
            try {
                Date date = formatSecsTZ.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else if(containsT && !containsSpace){
            try {
                Date date = formatSecsTZwT.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring containing milliseconds at the end
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseTimestringWithMillis(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        int len = timestring.length();
        char dot = timestring.charAt(len-4);
        boolean containsDot = false;
        if(dot == '.'){
            containsDot = true;
        }
        if(!containsT && containsSpace && containsDot){
            try {
                Date date = formatMiliSecs.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else if(containsT && !containsSpace && containsDot){
            try {
                Date date = formatMiliSecsWT.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring containing milliseconds and time zone
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseTimestringWithMillisWithTimeZone(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        int len = timestring.length();
        char dot = timestring.charAt(len-9);
        boolean containsDot = false;
        if(dot == '.'){
            containsDot = true;
        }
        if(!containsT && containsSpace && containsDot){
            try {
                Date date = formatMiliSecsTZ.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else if(containsT && !containsSpace && containsDot){
            try {
                Date date = formatMiliSecsTZwT.parse(timestring);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring containing microseconds at the end
     * !! Microseconds are omitted !!
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseTimestringWithMicros(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        boolean containsDot = false;
        int len = timestring.length();
        char dot = timestring.charAt(len-7);
        if(dot == '.'){
            containsDot = true;
        }
        if(!containsT && containsSpace && containsDot){
            try {
                String milisTime = timestring.substring(0, patternMiliSecsLen);
                Date date = formatMiliSecs.parse(milisTime);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else if(containsT && !containsSpace && containsDot){
            try {
                String milisTime = timestring.substring(0, patternMiliSecsWTLen);
                Date date = formatMiliSecsWT.parse(milisTime);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring containing microseconds and time zone
     * !! Microseconds are omitted !! 
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseTimestringWithMicrosWithTimeZone(String timestring) throws ParseException {
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        boolean containsDot = false;
        int len = timestring.length();
        char dot = timestring.charAt(len-12);
        if(dot == '.'){
            containsDot = true;
        }
        if(!containsT && containsSpace && containsDot){
            try {
                String milisTime = timestring.substring(0, patternMiliSecsLen);
                String timeZone = timestring.substring(len-5, len);
                Date date = formatMiliSecsTZ.parse(milisTime+timeZone);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else if(containsT && !containsSpace && containsDot){
            try {
                String milisTime = timestring.substring(0, patternMiliSecsWTLen);
                String timeZone = timestring.substring(len-5, len);
                Date date = formatMiliSecsTZwT.parse(milisTime+timeZone);
                return date;
            } catch (ParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring according to ISO format
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseISOTimestring(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        int len = timestring.length();
        char colonTZ = timestring.charAt(len-3);
        if(colonTZ == ':'){
            String part1 = timestring.substring(0, len-3);
            String part2 = timestring.substring(len-2, len);
            try{
                if(containsT){
                    Date date = formatSecsTZwT.parse(part1+part2);
                    return date;
                }else if(containsSpace){
                    Date date = formatSecsTZ.parse(part1+part2);
                    return date;
                }
                else{
                    throw new ParseException("Unsupported pattern!", 0);
                }
            } catch(ParseException e){
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring according to ISO format with milliseconds
     * @param timestring - timestring to be parsed
     * @return parsed timestring as Date
     * @throws ParseException
     */
    private static Date parseISOTimestringWithMillis(String timestring) throws ParseException{
        boolean containsT = timestring.contains("T");
        boolean containsSpace = timestring.contains(" ");
        int len = timestring.length();
        char colonTZ = timestring.charAt(len-3);
        if(colonTZ == ':'){
            String part1 = timestring.substring(0, len-3);
            String part2 = timestring.substring(len-2, len);
            try{
                if(containsT){
                    Date date = formatMiliSecsTZwT.parse(part1+part2);
                    return date;
                }else if(containsSpace){
                    Date date = formatMiliSecsTZ.parse(part1+part2);
                    return date;
                }
                else{
                    throw new ParseException("Unsupported pattern!", 0);
                }
            } catch(ParseException e){
                throw new ParseException(e.getMessage(), 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * Method parses timestring containing microseconds and time zone
     * yyyy-MM-dd HH:mm:ss.SSSsssZZZZZ
     * (e.g. from PostgreSQL db)
     * @param microTime
     * @throws ParseException 
     */
    public static Date parseTimestampMicro(String microTime) throws ParseException{
        /**
         * time string contains microseconds
         */
        int len = microTime.length();
        int dotIndex = microTime.lastIndexOf(".");
        
        /**
         * yyyy-MM-dd HH:mm:ss.SSSsss
         */
        if(len == 26 && dotIndex == 19){
            String milisTime = microTime.substring(0, patternMiliSecsLen);
            return parseTimestringWithMillis(milisTime);
        }
        /**
         * yyyy-MM-dd HH:mm:ss.SSSsssZZZ
         */
        else if(len == 29 && dotIndex == 19){
            String milisTime = microTime.substring(0, patternMiliSecsLen);
            /**
             * checks if contains time zone
             * if yes add minutes to TZ
             */ 
            char suffixTZ = microTime.charAt(len-3);
            if(suffixTZ == '+' || suffixTZ == '-'){
                String timeZone = microTime.substring(len-3, len)+"00";
                return parseTimestringWithMillisWithTimeZone(""+milisTime+timeZone);
            }
            else{
                return parseTimestringWithMillis(milisTime);
            }
        }
        /**
         * yyyy-MM-dd HH:mm:ss.SSSsssZZZZZ
         */
        else if(len == 31 && dotIndex == 19){
            String milisTime = microTime.substring(0, patternMiliSecsLen);
            /**
             * checks if contains time zone
             * if yes add minutes to TZ
             */ 
            char suffixTZ = microTime.charAt(len-5);
            if(suffixTZ == '+' || suffixTZ == '-'){
                String timeZone = microTime.substring(len-5, len);
                return parseTimestringWithMillisWithTimeZone(""+milisTime+timeZone);
            }
            else{
                return parseTimestringWithMillis(milisTime);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
}