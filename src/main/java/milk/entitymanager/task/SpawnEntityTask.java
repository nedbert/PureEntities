package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import milk.entitymanager.EntityManager;
import milk.entitymanager.util.Utils;

public class SpawnEntityTask extends Task{
    //TODO: 아... 답이없다... PMMP의 잔재 ㅆㅃ!!!!!

    public void onRun(int currentTicks){
        /*EntityManager owner = (EntityManager) this.owner;
        if(EntityManager.getEntities().size() >= owner.getData("entity.maximum")) return;
        rand = explode("/", owner.getData("spawn.rand"));
        foreach(EntityManager.spawn as key => data){
            if(Utils.rand(...rand) > rand[0]) continue;
            if(count(data["mob-list"]) === 0){
                unset(EntityManager.spawn[key]);
                continue;
            }
            int radius = (int) data["radius"];
            Position pos = Position.fromObject(new Vector3(...(vec = explode(":", key))), (k = Server.getInstance().getLevelByName((string) array_pop(vec))) == null ? Server.getInstance().getDefaultLevel() : k);
            pos.y = pos.getLevel().getHighestBlockAt(pos.x += Utils.rand(-radius, radius), pos.z += Utils.rand(-radius, radius));
            EntityManager.createEntity(data["mob-list"][Utils.rand(0, count(data["mob-list"]) - 1)], pos);
        }
        if(!owner.getData("autospawn.turn-on")) return;
        foreach(this.owner.getServer().getOnlinePlayers() as player){
            if(Utils.rand(...rand) > rand[0]) continue;
            radius = (int) owner.getData("autospawn.radius");
            pos = player.getPosition();
            pos.y = player.level.getHighestBlockAt(pos.x += Utils.rand(-radius, radius), pos.z += Utils.rand(-radius, radius))+2;

            ent = [
            ["Cow", "Pig", "Sheep", "Chicken", "Slime", "Wolf", "Ocelot", "Mooshroom", "Rabbit", "IronGolem", "SnowGolem"],
            ["Zombie", "Creeper", "Skeleton", "Spider", "PigZombie", "Enderman", "CaveSpider", "MagmaCube", "ZombieVillager", "Ghast", "Blaze"]
            ];
            EntityManager.createEntity(ent[Utils.rand(0, 1)][Utils.rand(0, 10)], pos);
        }*/
    }

}