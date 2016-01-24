package milk.entitymanager.util;

import java.util.Random;

public class Utils{

    public static int rand(int min, int max){
        if(max == min){
            return max;
        }
        return min + new Random().nextInt(max - min);
    }

    public static boolean rand(){
        return new Random().nextBoolean();
    }

}
