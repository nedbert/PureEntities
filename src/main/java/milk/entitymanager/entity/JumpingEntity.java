package milk.entitymanager.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class JumpingEntity extends BaseEntity{
    //TODO: 귀찮아...

    public JumpingEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    void checkTarget(){
        if(this.isKnockback()){
            return;
        }
    }


    @Override
    public Vector3 updateMove(int tickDiff){
        return null;
    }

}
