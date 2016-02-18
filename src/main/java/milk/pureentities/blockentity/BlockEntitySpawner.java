package milk.pureentities.blockentity;

import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ShortTag;

public class BlockEntitySpawner extends BlockEntitySpawnable{
    //TODO: This isn't implemeted yet

    protected int entityId = -1;
    protected int delay;
    protected int spawnRange;
    protected int minSpawnDelay;
    protected int maxSpawnDelay;
    protected int maxNearbyEntities;
    protected int requiredPlayerRange;

    public BlockEntitySpawner(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);

        if(!this.namedTag.contains("Delay") || !(this.namedTag.get("Delay") instanceof ShortTag)){
            this.namedTag.putShort("Delay", 120);
        }

        if(!this.namedTag.contains("SpawnRange") || !(this.namedTag.get("SpawnRange") instanceof ShortTag)){
            this.namedTag.putShort("SpawnRange", 25);
        }

        this.entityId = this.namedTag.getInt("EntityId");
        this.delay = this.namedTag.getInt("Delay");
        this.spawnRange = this.namedTag.getShort("SpawnRange");
    }

    @Override
    public CompoundTag getSpawnCompound(){
        return new CompoundTag()
            .putString("id", MOB_SPAWNER)
            .putInt("EntityId", this.entityId);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Item.MONSTER_SPAWNER;
    }

    public void setSpawnEntityType(int entityId){
        this.entityId = entityId;
    }

}
