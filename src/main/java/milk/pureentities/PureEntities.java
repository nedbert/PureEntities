package milk.pureentities;

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
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import milk.pureentities.blockentity.BlockEntitySpawner;
import milk.pureentities.entity.animal.walking.*;
import milk.pureentities.entity.monster.walking.*;
import milk.pureentities.entity.projectile.EntityFireBall;
import milk.pureentities.entity.monster.flying.Blaze;
import milk.pureentities.entity.monster.flying.Ghast;
import milk.pureentities.task.AutoSpawnTask;
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
        //clazz2.add(Blaze.class);
        clazz2.add(CaveSpider.class);
        clazz2.add(Chicken.class);
        clazz2.add(Cow.class);
        //clazz2.add(Creeper.class);
        clazz2.add(Enderman.class);
        //clazz2.add(Ghast.class);
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
        BlockEntity.registerBlockEntity("MobSpawner", BlockEntitySpawner.class);

        Utils.logInfo("All entities were registered");
    }

    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getScheduler().scheduleRepeatingTask(new AutoSpawnTask(), 300);

        Utils.logInfo("Plugin has been enabled");
    }

    public void onDisable(){
        Utils.logInfo("Plugin has been disable");
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent ev){
        if(ev.getFace() == 255 || ev.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK){
            return;
        }

        Item item = ev.getItem();
        Block block = ev.getBlock();
        if(item.getId() == Item.SPAWN_EGG && block.getId() == Item.MONSTER_SPAWNER){
            ev.setCancelled(true);

            BlockEntity blockEntity = block.getLevel().getBlockEntity(block);
            if(blockEntity != null && blockEntity instanceof BlockEntitySpawner){
                ((BlockEntitySpawner) blockEntity).setSpawnEntityType(item.getDamage());
            }else{
                if(blockEntity != null){
                    blockEntity.close();
                }
                CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntity.MOB_SPAWNER)
                    .putInt("EntityId", item.getDamage())
                    .putInt("x", (int) block.x)
                    .putInt("y", (int) block.y)
                    .putInt("z", (int) block.z);

                new BlockEntitySpawner(block.getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);
            }
        }
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent ev){
        if(ev.isCancelled()){
            return;
        }

        Block block = ev.getBlock();
        if(block.getId() == Item.JACK_O_LANTERN || block.getId() == Item.PUMPKIN){
            if(block.getSide(Vector3.SIDE_DOWN).getId() == Item.SNOW_BLOCK && block.getSide(Vector3.SIDE_DOWN, 2).getId() == Item.SNOW_BLOCK){
                Entity entity = create("SnowGolem", block.add(0.5, -2, 0.5));
                if(entity != null){
                    entity.spawnToAll();
                }

                ev.setCancelled();
                block.getLevel().setBlock(block.add(0, -1, 0), new BlockAir());
                block.getLevel().setBlock(block.add(0, -2, 0), new BlockAir());
            }else if(
                block.getSide(Vector3.SIDE_DOWN).getId() == Item.IRON_BLOCK
                && block.getSide(Vector3.SIDE_DOWN, 2).getId() == Item.IRON_BLOCK
            ){
                block = block.getSide(Vector3.SIDE_DOWN);

                Block first, second = null;
                if(
                    (first = block.getSide(Vector3.SIDE_EAST)).getId() == Item.IRON_BLOCK
                    && (second = block.getSide(Vector3.SIDE_WEST)).getId() == Item.IRON_BLOCK
                ){
                    block.getLevel().setBlock(first, new BlockAir());
                    block.getLevel().setBlock(second, new BlockAir());
                }else if(
                    (first = block.getSide(Vector3.SIDE_NORTH)).getId() == Item.IRON_BLOCK
                    && (second = block.getSide(Vector3.SIDE_SOUTH)).getId() == Item.IRON_BLOCK
                ){
                    block.getLevel().setBlock(first, new BlockAir());
                    block.getLevel().setBlock(second, new BlockAir());
                }

                if(second != null){
                    Entity entity = PureEntities.create("IronGolem", block.add(0.5, -1, 0.5));
                    if(entity != null){
                        entity.spawnToAll();
                    }
                    block.getLevel().setBlock(block, new BlockAir());
                    block.getLevel().setBlock(block.add(0, -1, 0), new BlockAir());
                    ev.setCancelled();
                }
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev){
        if(ev.isCancelled()){
            return;
        }

        Block block = ev.getBlock();
        if((block.getId() == Block.STONE
            || block.getId() == Block.STONE_BRICK
            || block.getId() == Block.STONE_WALL
            || block.getId() == Block.STONE_BRICK_STAIRS
        ) && block.getLevel().getBlockLightAt((int) block.x, (int) block.y, (int) block.z) < 12 && Utils.rand(1, 5) == 1){
            //TODO: 돌만 붓시면 되긋나
            /*Silverfish entity = (Silverfish) create("Silverfish", block.add(0.5, 0, 0.5));
            if(entity != null){
                entity.spawnToAll();
            }*/
        }
    }

}
