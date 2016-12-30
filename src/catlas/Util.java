package catlas;

import java.util.HashMap;

/**
 * Created by julek on 29-Dec-16.
 */
public class Util {
    static HashMap<Integer, Module> hmapModules = new HashMap<>();
    public static int genId(String s){
        long mod = 1000000007;
        long pot = 401;
        long cur = 1;
        long res = 0;
        for(int i = 0; i < s.length(); i++){
            res *= pot;
            res %= mod;
            res += (int)(s.charAt(i));
            res %= mod;
        }
        return (int)res;
    }
}
