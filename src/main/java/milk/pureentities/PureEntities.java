package milk.pureentities;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.*;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import milk.pureentities.blockentity.BlockEntitySpawner;
import milk.pureentities.entity.animal.walking.*;
import milk.pureentities.entity.monster.walking.*;
import milk.pureentities.entity.projectile.EntityFireBall;
import milk.pureentities.entity.monster.flying.Blaze;
import milk.pureentities.entity.monster.flying.Ghast;
import milk.pureentities.util.Utils;

import java.util.*;

public class PureEntities extends PluginBase implements Listener{

    public static Entity create(Object type, Position source, Object... args){
        FullChunk chunk = source.getLevel().getChunk((int) source.x >> 4, (int) source.z >> 4, true);
        if(!chunk.isGenerated()){
            chunk.setGenerated();
        }
        if(!chunk.isPopulated()){
            chunk.setPopulated();
        }

        CompoundTag nbt = new CompoundTag()
            .putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", source.x))
                .add(new DoubleTag("", source.y))
                .add(new DoubleTag("", source.z)))
            .putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0)))
            .putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", source instanceof Location ? (float) ((Location) source).yaw : 0))
                .add(new FloatTag("", source instanceof Location ? (float) ((Location) source).pitch : 0)));

        return Entity.createEntity(type.toString(), chunk, nbt, args);
    }

    public void onLoad(){
        ArrayList<Class<? extends Entity>> clazz2 = new ArrayList<>();
        clazz2.add(Blaze.class);
        clazz2.add(CaveSpider.class);
        clazz2.add(Chicken.class);
        clazz2.add(Cow.class);
        clazz2.add(Creeper.class);
        clazz2.add(Enderman.class);
        clazz2.add(Ghast.class);
        clazz2.add(IronGolem.class);
        //clazz2.add(MagmaCube.class);
        clazz2.add(Mooshroom.class);
        clazz2.add(Ocelot.class);
        clazz2.add(Pig.class);
        clazz2.add(PigZombie.class);
        clazz2.add(Rabbit.class);
        clazz2.add(Sheep.class);
        clazz2.add(Silverfish.class);
        clazz2.add(Skeleton.class);
        //clazz2.add(Slime.class);
        clazz2.add(SnowGolem.class);
        clazz2.add(Spider.class);
        clazz2.add(Wolf.class);
        clazz2.add(Zombie.class);
        clazz2.add(ZombieVillager.class);
        clazz2.forEach(clazz -> {
            try{
                Entity.registerEntity(clazz.getSimpleName(), clazz);
                int id = clazz.getField("NETWORK_ID").getInt(null);
                if(
                    id == IronGolem.NETWORK_ID
                    || id == SnowGolem.NETWORK_ID
                    || id == ZombieVillager.NETWORK_ID
                ){
                    return;
                }
                Item item = Item.get(Item.SPAWN_EGG, id);
                if(!Item.isCreativeItem(item)){
                    Item.addCreativeItem(item);
                }
            }catch(Exception ignore){}
        });
        Entity.registerEntity("FireBall", EntityFireBall.class);

        this.getServer().getLogger().info(TextFormat.GOLD + "[PureEntities]All entities were registered");
    }

    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getLogger().info(TextFormat.GOLD + "[PureEntities]Plugin has been enabled");
    }

    public void onDisable(){
        this.getServer().getLogger().info(TextFormat.GOLD + "[PureEntities]Plugin has been disable");
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent ev){
        if(ev.getFace() == 255 || ev.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK){
            return;
        }

        Item item = ev.getItem();
        Block target = ev.getBlock();
        Player player = ev.getPlayer();
        Block block = target.getSide(ev.getFace());

        if(item.getId() == Item.SPAWN_EGG){
            /*if(target.getId() == Item.MONSTER_SPAWNER){
                CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntity.MOB_SPAWNER)
                    .putInt("EntityId", item.getDamage())
                    .putInt("x", (int) target.x)
                    .putInt("y", (int) target.y)
                    .putInt("z", (int) target.z)
                    *//*.putShort("SpawnRange", 0)
                    .putShort("Delay", 0)
                    .putShort("MinSpawnDelay", 0)
                    .putShort("MaxSpawnDelay", 0)
                    .putShort("MaxNearbyEntities", 0)
                    .putShort("RequiredPlayerRange", 0)*//*;

                new BlockEntitySpawner(block.getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);
            }else{*/
                Entity entity = create(item.getDamage(), block);
                if(entity != null){
                    entity.spawnToAll();
                }

                if(player.isSurvival()){
                    item.count--;
                    player.getInventory().setItemInHand(item);
                }
                ev.setCancelled();
            //}
        }
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent ev){
        if(ev.isCancelled()){
            return;
        }

        Block block = ev.getBlock();
        if(block.getId() == Item.JACK_O_LANTERN || block.getId() == Item.PUMPKIN){
            //TODO: spawn IronGolem
            /*if(
                block.getSide(Vector3.SIDE_DOWN).getId() == Item.IRON_BLOCK
                && block.getSide(Vector3.SIDE_DOWN, 2).getId() == Item.IRON_BLOCK
            ){

            }else */if(
                block.getSide(Vector3.SIDE_DOWN).getId() == Item.SNOW_BLOCK
                && block.getSide(Vector3.SIDE_DOWN, 2).getId() == Item.SNOW_BLOCK
            ){
                for(int y = 0; y < 3; y++){
                    block.getLevel().setBlock(block.add(0, -y, 0), new BlockAir());
                }
                create("SnowGolem", block.add(0.5, -2, 0.5));
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev){
        Block pos = ev.getBlock();
        if(ev.isCancelled()){
            return;
        }

        if(
            ev.getBlock().getId() == Block.STONE
            || ev.getBlock().getId() == Block.STONE_BRICK
            || ev.getBlock().getId() == Block.STONE_WALL
            || ev.getBlock().getId() == Block.STONE_BRICK_STAIRS
        ){
            if(ev.getBlock().getLightLevel() < 12 && Utils.rand(1,3) < 2){
                Silverfish entity = (Silverfish) create("Silverfish", pos.add(0.5, 0, 0.5));
                if(entity != null){
                    entity.spawnToAll();
                }
            }
        }
    }

}