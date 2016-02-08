package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import milk.entitymanager.EntityManager;
import milk.entitymanager.util.Utils;

import java.util.LinkedHashMap;
import java.util.List;

public class AutoSpawnTask implements Runnable{

    public void run(){
        EntityManager owner = (EntityManager) Server.getInstance().getPluginManager().getPlugin("EntityManager");
        final int[] rand = {1, 4};
        try{
            String[] rs = owner.getData("autospawn.rand", "1/4").split("/");
            rand[0] = Integer.parseInt(rs[0]);
            rand[1] = Integer.parseInt(rs[1]);
        }catch(Exception ignore){}

        Server.getInstance().getOnlinePlayers().forEach((id, player) -> {
            if(Utils.rand(rand[0], rand[1]) > rand[0]){
                return;
            }

            LinkedHashMap data = owner.getData("autospawn.entities", new LinkedHashMap<>());
            int radius = owner.getData("autospawn.radius", 25);
            Position pos = new Position((int) player.x + 0.5, 0, (int) player.z + 0.5, player.level);
            pos.y = player.level.getHighestBlockAt((int) (pos.x += Utils.rand(-radius, radius)), (int) (pos.z += Utils.rand(-radius, radius))) + 1;


            List list;
            if(Utils.rand()){
                if(!(data.get("animal") instanceof List)){
                    return;
                }
                list = (List) data.get("animal");
            }else{
                if(!(data.get("monster") instanceof List)){
                    return;
                }
                list = (List) data.get("monster");
            }
            EntityManager.create(list.get(Utils.rand(0, list.size() - 1)), pos);
        });
    }

}