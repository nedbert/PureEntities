package milk.entitymanager.util;

public class Utils{

    public static int rand(int min, int max){
        return (int) (Math.random() * (max - min + 1) + min);
    }

}
