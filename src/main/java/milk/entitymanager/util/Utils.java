package milk.entitymanager.util;

import java.util.Random;

public class Utils{

    public static int rand(int min, int max){
        return (int) (min + Math.random() * (max - min + 1));
    }

    public static boolean rand(){
        return new Random().nextBoolean();
    }

}
