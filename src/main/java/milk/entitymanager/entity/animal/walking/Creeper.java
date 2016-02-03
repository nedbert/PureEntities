package milk.entitymanager.entity.animal.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class Creeper extends WalkingMonster implements EntityExplosive{
    public static final int NETWORK_ID = 33;

    int bombTime = 0;

    public Creeper(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.72f;
    }

    @Override
    public float getHeight(){
        return 1.8f;
    }

    @Override
    public double getSpeed(){
        return 0.9;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        if(this.namedTag.contains("BombTime")){
            this.bombTime = this.namedTag.getInt("BombTime");
        }
    }

    @Override
    public void saveNBT(){
        super.saveNBT();
        this.namedTag.putInt("BombTime", this.bombTime);
    }

    @Override
    public String getName(){
        return "Creeper";
    }

    @Override
    public void explode(){
        ExplosionPrimeEvent ev = new ExplosionPrimeEvent(this, 2.8);
        this.server.getPluginManager().callEvent(ev);

        if(!ev.isCancelled()){
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if(ev.isBlockBreaking()){
                explosion.explodeA();
            }
            explosion.explodeB();
            this.close();
        }
    }

    public void attackEntity(Entity player){
        if(this.distanceSquared(player) > 38){
            if(this.bombTime > 0){
                this.bombTime -= Math.min(2, this.bombTime);
            }
        }else{
            this.bombTime++;
            if(this.bombTime >= Utils.rand(55, 70)){
                this.explode();
            }
        }
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0 :
                    return new Item[]{Item.get(Item.FLINT, 0, 1)};
                case 1 :
                    return new Item[]{Item.get(Item.GUNPOWDER, 0, 1)};
                case 2 :
                    return new Item[]{Item.get(Item.REDSTONE_DUST, 0, 1)};
            }
        }
        return new Item[0];
    }

}