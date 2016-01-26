package milk.entitymanager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.DroppedItem;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Projectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.entity.EntityDespawnEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import milk.entitymanager.entity.*;
import milk.entitymanager.task.AutoClearTask;
import milk.entitymanager.task.SpawnEntityTask;
import milk.entitymanager.util.Utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class EntityManager extends PluginBase implements Listener{

    static LinkedHashMap<String, Object> data;
    public static LinkedHashMap<String, Object> drops;
    public static LinkedHashMap<String, Object> spawner;

    static HashMap<Long, BaseEntity> entities = new HashMap<>();

    static HashMap<String, Class<? extends Entity>> shortNames = new HashMap<>();
    static HashMap<Integer, Class<? extends Entity>> knownEntities = new HashMap<>();

    public void onLoad(){
        final int[] count = {0};

        ArrayList<Class<? extends Entity>> clazz2 = new ArrayList<>();
        clazz2.add(Blaze.class);
        clazz2.add(CaveSpider.class);
        clazz2.add(Chicken.class);
        clazz2.add(Cow.class);
        clazz2.add(Creeper.class);
        clazz2.add(Enderman.class);
        clazz2.add(Ghast.class);
        clazz2.add(IronGolem.class);
        clazz2.add(MagmaCube.class);
        clazz2.add(Mooshroom.class);
        clazz2.add(Ocelot.class);
        clazz2.add(Pig.class);
        clazz2.add(PigZombie.class);
        clazz2.add(Rabbit.class);
        clazz2.add(Sheep.class);
        clazz2.add(Silverfish.class);
        clazz2.add(Skeleton.class);
        clazz2.add(Slime.class);
        clazz2.add(SnowGolem.class);
        clazz2.add(Spider.class);
        clazz2.add(Wolf.class);
        clazz2.add(Zombie.class);
        clazz2.add(ZombieVillager.class);
        clazz2.forEach(clazz -> count[0] += registerEntity(clazz) ? 1 : 0);

        Entity.registerEntity(FireBall.class);

        if(count[0] == clazz2.size()){
            this.getServer().getLogger().info(TextFormat.GOLD + "[EntityManager]All entities were registered");
        }else{
            this.getServer().getLogger().info(TextFormat.RED + "[EntityManager]ERROR, I can't registerd entity");
        }
    }

    static Entity create(Class<? extends Entity> clazz, FullChunk chunk, CompoundTag nbt, Object... args){
        if(clazz == null){
            return null;
        }

        Entity entity = null;
        for(Constructor constructor : clazz.getConstructors()){
            if(entity != null){
                break;
            }

            int count = (args == null || args.length == 0) ? 2 : 2 + args.length;
            if(constructor.getParameterCount() != count){
                continue;
            }

            try{
                if(count == 2){
                    entity = (Entity) constructor.newInstance(chunk, nbt);
                }else{
                    Object[] objects = new Object[args.length + 2];

                    objects[0] = chunk;
                    objects[1] = nbt;
                    System.arraycopy(args, 0, objects, 2, args.length);
                    entity = (Entity) constructor.newInstance(objects);
                }
            }catch(Exception ignore){}
        }

        return entity;
    }

    public static Entity create(Object type, Position source, Object... args){
        Class<? extends Entity> clazz = null;

        FullChunk chunk = source.getLevel().getChunk(((int) source.x) >> 4, ((int) source.z) >> 4, true);
        if(chunk == null) return null;
        if(!chunk.isGenerated()) chunk.setGenerated();
        if(!chunk.isPopulated()) chunk.setPopulated();

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

        if(type instanceof String && shortNames.containsKey(type)){
            clazz = shortNames.get(type);
        }else if(type instanceof Integer && knownEntities.containsKey(type)){
            clazz = knownEntities.get(type);
        }

        if(clazz == null){
            if(type instanceof String){
                return Entity.createEntity((String) type, chunk, nbt, args);
            }else if(type instanceof Integer){
                return Entity.createEntity((int) type, chunk, nbt, args);
            }
        }

        return create(clazz, chunk, nbt, args);
    }

    public static boolean registerEntity(Class<? extends Entity> clazz){
        if(clazz == null){
            return false;
        }

        Entity.registerEntity(clazz);
        try{
            int networkId = clazz.getField("NETWORK_ID").getInt(null);
            if(networkId != -1){
                knownEntities.put(networkId, clazz);
            }else{
                return false;
            }
            shortNames.put(clazz.getSimpleName(), clazz);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static Map<Long, BaseEntity> getEntities(){
        return entities;
    }

    public static void clear(){
        clear(new Class[]{BaseEntity.class}, null);
    }

    public static void clear(Class[] type){
        clear(type, null);
    }

    public static void clear(Class[] type, Level level){
        level = level == null ? Server.getInstance().getDefaultLevel() : level;
        for(Entity entity : level.getEntities()) for(Class clazz : type){
            if(clazz.isInstance(entity)){
                entity.close();
            }
        }
    }

    public void onEnable(){
        this.getDataFolder().mkdirs();

        File conFile = new File(this.getDataFolder(), "config.yml");
        if(!conFile.exists()){
            this.saveResource("config.yml");
        }

        File dropFile = new File(this.getDataFolder(), "drops.yml");
        File spawnFile = new File(this.getDataFolder(), "spawner.yml");

        EntityManager.data = (LinkedHashMap<String, Object>) new Config(conFile, Config.YAML).getAll();
        EntityManager.drops = (LinkedHashMap<String, Object>) new Config(dropFile, Config.YAML).getAll();
        EntityManager.spawner = (LinkedHashMap<String, Object>) new Config(spawnFile, Config.YAML).getAll();

        /*Drops Example
        //TYPE_ID
        32:
          #id  meta count
          [288, 0, "1,10"],
          [392, 0, "1,10"]
        36:
          [266, 0, "0,8"]
        */

        knownEntities.forEach((id, clazz) -> {
            Item item = Item.get(Item.SPAWN_EGG, id);
            if(!Item.isCreativeItem(item)) Item.addCreativeItem(item);
        });

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getLogger().info(TextFormat.GOLD + "[EntityManager]Plugin has been enabled");
        this.getServer().getScheduler().scheduleRepeatingTask(new SpawnEntityTask(this), this.getData("spawn.tick", 120));

        if(this.getData("autoclear.turn-on", true)){
            this.getServer().getScheduler().scheduleRepeatingTask(new AutoClearTask(), this.getData("autoclear.tick", 120));
        }
    }

    public void onDisable(){
        Config con = new Config(new File(this.getDataFolder(), "spawner.yml"), Config.YAML);
        con.setAll(EntityManager.spawner);
        con.save();

        Config con2 = new Config(new File(this.getDataFolder(), "drops.yml"), Config.YAML);
        con2.setAll(EntityManager.drops);
        con2.save();

        this.getServer().getLogger().info(TextFormat.GOLD + "[EntityManager]Plugin has been disable");
    }

    public <T> T getData(String key, T defaultValue){
        try{
            String[] vars = key.split(".");
            if(vars.length < 1)
                return defaultValue;

            String base = vars[0];
            if(!data.containsKey(base)){
                return defaultValue;
            }

            if(!(data.get(base) instanceof Map)){
                return (T) data.get(base);
            }

            Object nbase = data.get(base);

            int index = 0;
            while(++index < vars.length){
                String baseKey = vars[index];
                if(!(data.get(baseKey) instanceof Map)){
                    return (T) data.get(baseKey);
                }
                nbase = data.get(baseKey);
            }
            return (T) nbase;
        }catch(Exception e){
            return defaultValue;
        }
    }

    @EventHandler
    public void EntitySpawnEvent(EntitySpawnEvent ev){
        Entity entity = ev.getEntity();
        if(entity instanceof BaseEntity && !entity.closed){
            BaseEntity ent = (BaseEntity) entity;
            entities.put(ent.getId(), ent);
        }
    }

    @EventHandler
    public void EntityDespawnEvent(EntityDespawnEvent ev){
        Entity entity = ev.getEntity();
        if(entity instanceof BaseEntity){
            entities.remove(entity.getId());
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent ev){
        if(ev.getFace() == 255 || ev.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK) return;
        Item item = ev.getItem();
        Player player = ev.getPlayer();
        Block pos = ev.getBlock().getSide(ev.getFace());

        if(item.getId() == Item.SPAWN_EGG){
            Entity entity = create(item.getDamage(), pos);
            if(entity != null){
                entity.spawnToAll();
            }

            if(player.isSurvival()){
                item.count--;
                player.getInventory().setItemInHand(item);
            }
            ev.setCancelled();
        }else if(item.getId() == Item.MONSTER_SPAWNER){
            LinkedHashMap<String, Object> hashdata = new LinkedHashMap<>();
            hashdata.put("radius", 5);
            hashdata.put("mob-list", new ArrayList<String>(){{
                add("Cow");
                add("Pig");
                add("Sheep");
                add("Chicken");
                add("Zombie");
                add("Creeper");
                add("Skeleton");
                add("Spider");
                add("PigZombie");
                add("Enderman");
            }});
            spawner.put(String.format("%s:%s:%s:%s", (int) pos.x, (int) pos.y, (int) pos.z, pos.getLevel().getFolderName()), hashdata);
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev){
        Block pos = ev.getBlock();
        if(ev.isCancelled()) return;
        if(pos.getId() == Item.MONSTER_SPAWNER){
            spawner.remove(String.format("%s:%s:%s:%s", (int) pos.x, (int) pos.y, (int) pos.z, pos.getLevel().getFolderName()));
        }

        if(
            ev.getBlock().getId() == Block.STONE
            || ev.getBlock().getId() == Block.STONE_BRICK
            || ev.getBlock().getId() == Block.STONE_WALL
            || ev.getBlock().getId() == Block.STONE_BRICK_STAIRS
        ){
            if(ev.getBlock().getLightLevel() < 12 && Utils.rand(1,3) < 2){
                Silverfish entity = (Silverfish) EntityManager.create("Silverfish", pos);
                if(entity != null){
                    entity.spawnToAll();
                }
            }
        }
    }

    @EventHandler
    public void ExplosionPrimeEvent(ExplosionPrimeEvent ev){
        ev.setCancelled(!this.getData("entity.explode", false));
    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent ev){
        Entity entity = ev.getEntity();
        if(!(entity instanceof BaseEntity) || drops.containsKey(entity.NETWORK_ID + "")){
            return;
        }

        if(!(drops.get(entity.NETWORK_ID + "") instanceof List)){
            return;
        }

        //TODO: Change drop item
        /*List drops = (List) EntityManager.drops.get(entity.NETWORK_ID);
        drops.forEach(k -> {
            if(!(k instanceof List)){
                return;
            }

            List data = (List) k;
            if(data.size() < 3){
                return;
            }
        });
        foreach( as key => data){
            if(!isset(data[0]) || !isset(data[1]) || !isset(data[2])){
                unset(drops[entity.NETWORK_ID][key]);
                continue;
            }
            count = explode(",", data[2]);
            if(min(...count) !== count[0]){
                unset(drops[entity.NETWORK_ID][key]);
                continue;
            }
            item = Item.get(data[0], data[1]);
            item.setCount(max(Utils.rand(...count), 0));
            drops[] = item;
        }
        ev.setDrops(drops);*/
    }

    public boolean onCommand(CommandSender i, Command cmd, String label, String[] sub){
        String output = "[EntityManager]";
        switch(sub.length > 0 ? sub[0] : ""){
            case "remove":
                if(!i.hasPermission("entitymanager.command.remove")){
                    i.sendMessage(TextFormat.RED + "명령어를 사용할 권한이 없습니다");
                    //i.sendMessage(TextFormat.RED + "You do not have permission to use this command");
                    return true;
                }
                Level level;
                if(sub.length > 1){
                    level = this.getServer().getLevelByName(sub[1]);
                }else{
                    level = i instanceof Player ? ((Player) i).getLevel() : null;
                }

                clear(new Class[]{BaseEntity.class, Projectile.class, DroppedItem.class}, level);
                output += "All spawned entities were removed";
                break;
            case "check":
                if(!i.hasPermission("entitymanager.command.check")){
                    i.sendMessage(TextFormat.RED + "명령어를 사용할 권한이 없습니다");
                    //i.sendMessage(TextFormat.RED + "You do not have permission to use this command");
                    return true;
                }
                int mob = 0;
                int animal = 0;
                int item = 0;
                int projectile = 0;
                int other = 0;
                Level lv;
                if(sub.length > 1){ //sub[1]
                    lv = this.getServer().getLevelByName(sub[1]);
                }else{
                    lv = i instanceof Player ? ((Player) i).getLevel() : this.getServer().getDefaultLevel();
                }
                for(Entity ent : lv.getEntities()){
                    if(ent instanceof Monster){
                        mob++;
                    }else if(ent instanceof Animal){
                        animal++;
                    }else if(ent instanceof DroppedItem){
                        item++;
                    }else if(ent instanceof Projectile){
                        projectile++;
                    }else if(!(ent instanceof Player)){
                        other++;
                    }
                }
                String k = "--- 월드 " + lv.getName() + " 에 있는 모든 엔티티---\n";
                //String k = "--- All entities in Level " + level.getName() + " ---\n";
                k += TextFormat.YELLOW + "Monster: %s\n";
                k += TextFormat.YELLOW + "Animal: %s\n";
                k += TextFormat.YELLOW + "Items: %s\n";
                k += TextFormat.YELLOW + "Projectiles: %s\n";
                k += TextFormat.YELLOW + "Others: %s\n";
                output = String.format(k, mob, animal, item, projectile, other);
                break;
            case "create":
                if(!i.hasPermission("entitymanager.command.create")){
                    i.sendMessage(TextFormat.RED + "명령어를 사용할 권한이 없습니다");
                    //i.sendMessage(TextFormat.RED + "You do not have permission to use this command");
                    return true;
                }

                int type1 = -1;
                String type2 = sub.length > 1 ? sub[1] : "";
                try{
                    type1 = Integer.parseInt(type2);
                }catch(Exception ignore){}

                if((type1 == -1 || !knownEntities.containsKey(type1)) && !shortNames.containsKey(type2)){
                    output += "존재하지 않는 엔티티 이름이에요";
                    //output += "Entity's name is incorrect";
                    break;
                }

                Position pos = null;
                if(sub.length > 4){
                    Level lk = null;
                    if(sub.length > 5){
                        lk = this.getServer().getLevelByName(sub[5]);
                    }else if(i instanceof Player){
                        lk = ((Player) i).getLevel();
                    }

                    if(lk == null){
                        lk = this.getServer().getDefaultLevel();
                    }

                    pos = new Position(Double.parseDouble(sub[2]), Double.parseDouble(sub[3]), Double.parseDouble(sub[4]), lk);
                }else if(i instanceof Player){
                    pos = ((Player) i).getPosition();
                }

                if(pos == null){
                    output += "사용법: /" + label + " create <id/name> (x) (y) (z) (level)";
                    //output += "usage: /label create <id/name> (x) (y) (z) (level)";
                    break;
                }

                Entity ent;
                if((ent = EntityManager.create(type1, pos)) == null){
                    if((ent = EntityManager.create(type2, pos)) == null){
                        output += "엔티티를 소환하는도중 에러가 발생했습니다";
                        break;
                    }
                }
                ent.spawnToAll();
                break;
            default:
                output += "사용법: /" + label + " <remove/check/create>";
                //output += "usage: /label <remove/check/create>";
                break;
        }

        if(output.length() > 0){
            i.sendMessage(output);
        }
        return true;
    }

}