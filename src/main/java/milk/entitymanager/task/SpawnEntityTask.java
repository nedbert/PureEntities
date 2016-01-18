package milk.entitymanager.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import milk.entitymanager.EntityManager;
import milk.entitymanager.util.Utils;

public class SpawnEntityTask extends Task{

    EntityManager owner;

    public SpawnEntityTask(EntityManager owner){
        this.owner = owner;
    }

    public void onRun(int currentTicks){
        EntityManager owner = this.owner;
        if(EntityManager.getEntities().size() >= ((int) owner.getData("entity.maximum"))) return;
        String[] rand = ((String) owner.getData("spawn.rand")).split("/");
        /*foreach(EntityManager.spawn as key => data){
            if(Utils.rand(...rand) > rand[0]) continue;
            if(count(data["mob-list"]) === 0){
                unset(EntityManager.spawn[key]);
                continue;
            }
            int radius = (int) data["radius"];
            Position pos = Position.fromObject(new Vector3(...(vec = explode(":", key))), (k = Server.getInstance().getLevelByName((string) array_pop(vec))) == null ? Server.getInstance().getDefaultLevel() : k);
            pos.y = pos.getLevel().getHighestBlockAt(pos.x += Utils.rand(-radius, radius), pos.z += Utils.rand(-radius, radius));
            EntityManager.createEntity(data["mob-list"][Utils.rand(0, count(data["mob-list"]) - 1)], pos);
        }*/
        if(!((boolean) owner.getData("autospawn.turn-on"))){
            return;
        }

        for(Player player : Server.getInstance().getOnlinePlayers().values()){
            if(Utils.rand(Integer.parseInt(rand[0]), Integer.parseInt(rand[1])) > Integer.parseInt(rand[0])){
                continue;
            }

            int radius = (int) owner.getData("autospawn.radius");
            Position pos = player.getPosition();
            pos.y = player.level.getHighestBlockAt((int) (pos.x += Utils.rand(-radius, radius)), (int) (pos.z += Utils.rand(-radius, radius))) + 2;

            String[][] ent = {
                {"Cow", "Pig", "Sheep", "Chicken", "Slime", "Wolf", "Ocelot", "Mooshroom", "Rabbit", "IronGolem", "SnowGolem"},
                {"Zombie", "Creeper", "Skeleton", "Spider", "PigZombie", "Enderman", "CaveSpider", "MagmaCube", "ZombieVillager", "Ghast", "Blaze"}
            };
            EntityManager.create(ent[Utils.rand(0, 1)][Utils.rand(0, 10)], pos);
        }
    }

}