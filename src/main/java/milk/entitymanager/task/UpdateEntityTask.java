package milk.entitymanager.task;

import cn.nukkit.scheduler.Task;
import milk.entitymanager.EntityManager;
import milk.entitymanager.entity.BaseEntity;

public class UpdateEntityTask extends Task{

    public void onRun(int currentTicks){
        EntityManager.getEntities().forEach((id, ent) -> {
            BaseEntity entity = (BaseEntity) ent;
            if(entity.isCreated()) entity.updateTick();
        });
    }

}
