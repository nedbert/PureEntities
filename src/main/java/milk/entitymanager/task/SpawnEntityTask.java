package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import milk.entitymanager.EntityManager;
import milk.entitymanager.util.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpawnEntityTask implements Runnable{

    EntityManager owner;

    public SpawnEntityTask(EntityManager owner){
        this.owner = owner;
    }

    @Override
    public void run(){
        EntityManager owner = this.owner;
        if(EntityManager.getEntities().size() >= (owner.getData("entity.maximum", 120))){
            return;
        }

        int[] rand;
        try{
            String[] k = owner.getData("spawn.rand", "1/4").split("/");
            rand = new int[]{Integer.parseInt(k[0]), Integer.parseInt(k[1])};
        }catch(Exception e){
            Server.getInstance().getLogger().warning("[EntityManager]에러 발생! \"config.yml\"파일을 확인해주세요");
            return;
        }

        EntityManager.spawner.forEach((key, k) -> {
            if(
                !(k instanceof Map)
                || Utils.rand(rand[0], rand[1]) > rand[0]
            ){
                return;
            }

            Map data = (Map) k;
            int radius = (int) data.get("radius");

            String[] vec = key.split(":");
            Position pos = new Position(Integer.parseInt(vec[0]), Integer.parseInt(vec[1]), Integer.parseInt(vec[2]));
            pos.level = Server.getInstance().getLevelByName(vec[3]);
            pos.x += Utils.rand(-radius, radius);
            pos.z += Utils.rand(-radius, radius);
            pos.y = pos.getLevel().getHighestBlockAt((int) pos.x, (int) pos.z);

            if(!(data.get("mob-list") instanceof List)){
                return;
            }

            List list = (List) data.get("mob-list");
            EntityManager.create(list.get(Utils.rand(1, list.size()) - 1), pos);
        });

        if(!owner.getData("autospawn.turn-on", true)){
            return;
        }

        Server.getInstance().getOnlinePlayers().forEach((id, player) -> {
            if(Utils.rand(rand[0], rand[1]) > rand[0]){
                return;
            }

            int radius = owner.getData("autospawn.radius", 25);
            Position pos = player.getPosition();
            pos.y = player.level.getHighestBlockAt((int) (pos.x += Utils.rand(-radius, radius)), (int) (pos.z += Utils.rand(-radius, radius))) + 1;

            String[][] ent = {
                {"Cow", "Pig", "Sheep", "Chicken", "Slime", "Wolf", "Ocelot", "Mooshroom", "Rabbit", "IronGolem", "SnowGolem"},
                {"Zombie", "Creeper", "Skeleton", "Spider", "PigZombie", "Enderman", "CaveSpider", "MagmaCube", "ZombieVillager", "Ghast", "Blaze"}
            };
            EntityManager.create(ent[Utils.rand(0, 1)][Utils.rand(0, 10)], pos);
        });
    }

}