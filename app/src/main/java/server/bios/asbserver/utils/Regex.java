package server.bios.asbserver.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BIOS on 9/11/2016.
 */
public class Regex {
    private volatile static Regex regex;

    private Regex() {
    }

    public static Regex getInstance(){
        if(regex == null){
            synchronized (Regex.class) {
                regex = new Regex();
                return regex;
            }
        }
        return regex;
    }

    public String parser(String regex, String input) {
        return parser(regex, input, 0, 0);
    }

    public String parser(String regex, String input, int group) {
        return parser(regex, input, group, 0);
    }

    public String parser(String regex, String input, int group, int flags) {
        Pattern pattern = Pattern.compile(regex, flags);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return "";
    }
}
