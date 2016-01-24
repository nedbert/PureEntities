package milk.entitymanager.thread;

import cn.nukkit.scheduler.Task;
import milk.entitymanager.EntityManager;
import milk.entitymanager.entity.BaseEntity;

public class EntityThread extends Task{

    public void onRun(int currentTicks){
        try{
            EntityManager.getEntities().values().stream().filter(BaseEntity::isCreated).forEach(BaseEntity::updateTick);
        }catch(Exception ignore){}
    }

}
