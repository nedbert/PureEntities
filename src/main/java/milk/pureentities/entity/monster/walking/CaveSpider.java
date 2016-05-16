package milk.pureentities.entity.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.monster.WalkingMonster;
import milk.pureentities.util.Utils;

import java.util.HashMap;

public class CaveSpider extends WalkingMonster{
    public static final int NETWORK_ID = 40;

    public CaveSpider(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.9f;
    }

    @Override
    public float getHeight(){
        return 0.8f;
    }

    @Override
    public double getSpeed(){
        return 1.3;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(12);
        this.setDamage(new int[]{0, 2, 3, 3});
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.32){
            this.attackDelay = 0;
            HashMap<Integer, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.MODIFIER_BASE, (float) this.getDamage());

            if(player instanceof Player){
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {{
                    put(Item.LEATHER_CAP, 1f);
                    put(Item.LEATHER_TUNIC, 3f);
                    put(Item.LEATHER_PANTS, 2f);
                    put(Item.LEATHER_BOOTS, 1f);
                    put(Item.CHAIN_HELMET, 1f);
                    put(Item.CHAIN_CHESTPLATE, 5f);
                    put(Item.CHAIN_LEGGINGS, 4f);
                    put(Item.CHAIN_BOOTS, 1f);
                    put(Item.GOLD_HELMET, 1f);
                    put(Item.GOLD_CHESTPLATE, 5f);
                    put(Item.GOLD_LEGGINGS, 3f);
                    put(Item.GOLD_BOOTS, 1f);
                    put(Item.IRON_HELMET, 2f);
                    put(Item.IRON_CHESTPLATE, 6f);
                    put(Item.IRON_LEGGINGS, 5f);
                    put(Item.IRON_BOOTS, 2f);
                    put(Item.DIAMOND_HELMET, 3f);
                    put(Item.DIAMOND_CHESTPLATE, 8f);
                    put(Item.DIAMOND_LEGGINGS, 6f);
                    put(Item.DIAMOND_BOOTS, 3f);
                }};

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }

                damage.put(EntityDamageEvent.MODIFIER_ARMOR, (float) (damage.getOrDefault(EntityDamageEvent.MODIFIER_ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.MODIFIER_BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, damage));
        }
    }

    @Override
    public Item[] getDrops(){
        return this.lastDamageCause instanceof EntityDamageByEntityEvent ? new Item[]{Item.get(Item.STRING, 0, Utils.rand(0, 2))} : new Item[0];
    }

}
