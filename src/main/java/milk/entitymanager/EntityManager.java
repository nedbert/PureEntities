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
import cn.nukkit.utils.TextFormat;
import milk.entitymanager.entity.*;
import milk.entitymanager.thread.EntityThread;
import milk.entitymanager.util.Utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EntityManager extends PluginBase implements Listener{

    String path;

    static Map data;
    //static HashMap drops;
    static LinkedHashMap<String, Object> spawner;

    static Map<Long, BaseEntity> entities = new HashMap<>();

    static Map<String, Class<? extends Entity>> shortNames = new HashMap<>();
    static Map<Integer, Class<? extends Entity>> knownEntities = new HashMap<>();

    public void onLoad(){
        final int[] count = {0};

        ArrayList<Class<? extends Entity>> clazz2 = new ArrayList<>();
        clazz2.add(Enderman.class);
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

        if(entity == null){
            return Entity.createEntity(clazz.getSimpleName(), chunk, nbt, args);
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
        return create(clazz, chunk, nbt, args);
    }

    public static boolean registerEntity(Class<? extends Entity> clazz){
        if(clazz == null){
            return false;
        }

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
        path = this.getServer().getDataPath() + "plugins/EntityManager/";
        File file = new File(path);
        if(!file.isDirectory()){
            try{
                file.mkdirs();
            }catch(Exception ignore){}
        }

        /*function getData(ar, key, default){
            vars = explode(".", key);
            base = array_shift(vars);
            if(!isset(ar[base])) return default;
            base = ar[base];
            while(count(vars) > 0){
                baseKey = array_shift(vars);
                if(!is_array(base) || !isset(base[baseKey])) return default;
                base = base[baseKey];
            }
            return base;
        }

        data = [];
        if(file_exists(path + "config.yml")){
            data = yaml_parse(this.yaml(path + "config.yml"));
        }
        data = [
            "entity" => [
                "maximum" => getData(data, "entity.maximum", 30),
                "explode" => getData(data, "entity.explode", true),
            ],
            "spawn" => [
                "rand" => getData(data, "spawn.rand", "1/3"),
                "tick" => getData(data, "spawn.tick", 120),
            ],
            "autospawn" => [
                "turn-on" => getData(data, "autospawn.turn-on", getData(data, "spawn.auto", true)),
                "radius" => getData(data, "autospawn.radius", getData(data, "spawn.radius", 25)),
            ]
        ];
        file_put_contents(path + "config.yml", yaml_emit(data, YAML_UTF8_ENCODING));

        if(file_exists(path. "SpawnerData.yml")){
            spawn = yaml_parse(this.yaml(path + "SpawnerData.yml"));
            unlink(path. "SpawnerData.yml");
        }else if(file_exists(path. "spawner.yml")){
            spawn = yaml_parse(this.yaml(path + "spawner.yml"));
        }else{
            spawn = [];
            file_put_contents(path + "spawner.yml", yaml_emit([], YAML_UTF8_ENCODING));
        }

        if(file_exists(path. "drops.yml")){
            drops = yaml_parse(this.yaml(path + "drops.yml"));
        }else{
            drops = [
                Zombie.NETWORK_ID => [
                    //[Item id, Item meta, Count, Percentage]
                    //example: [Item.FEATHER, 0, "1,10", "1/1"]
                ],
                Creeper.NETWORK_ID => [

                ],
            ];
            file_put_contents(path + "drops.yml", yaml_emit([], YAML_UTF8_ENCODING));
        }*/

        knownEntities.forEach((id, clazz) -> {
            Item item = Item.get(Item.SPAWN_EGG, id);
            if(!Item.isCreativeItem(item)) Item.addCreativeItem(item);
        });

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getLogger().info(TextFormat.GOLD + "[EntityManager]Plugin has been enabled");
        this.getServer().getScheduler().scheduleRepeatingTask(new EntityThread(), 1);
        //this.getServer().getScheduler().scheduleRepeatingTask(new SpawnEntityTask(), (int) this.getData("spawn.tick"));
    }

    public void onDisable(){
        this.getServer().getLogger().info(TextFormat.GOLD + "[EntityManager]Plugin has been disable");
        //file_put_contents(this.getServer().getDataPath() + "plugins/EntityManager/spawner.yml", yaml_emit(spawn, YAML_UTF8_ENCODING));
    }

    public <T> T getData(String key, T defaultValue){
        String[] vars = key.split(".");
        if(vars.length < 1) return defaultValue;

        Object base = vars[0];
        if(!data.containsKey(base)) return defaultValue;

        if(!(data.get(base) instanceof Map)){
            return (T) data.get(base);
        }
        base = data.get(base);

        int index = 0;
        while(++index < vars.length){
            String baseKey = vars[index];
            if(!(data.get(baseKey) instanceof Map)){
                return (T) data.get(baseKey);
            }
            base = data.get(baseKey);
        }
        return (T) base;
    }

    public Object getData(String key){
        String[] vars = key.split(".");
        if(vars.length < 1) return null;

        Object base = vars[0];
        if(!data.containsKey(base)) return null;

        if(!(data.get(base) instanceof Map)){
            return data.get(base);
        }
        base = data.get(base);

        int index = 0;
        while(++index < vars.length){
            String baseKey = vars[index];
            if(!(data.get(baseKey) instanceof Map)){
                return data.get(baseKey);
            }
            base = data.get(baseKey);
        }
        return base;
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
            hashdata.put("mob-list", new ArrayList<ArrayList<String>>(){{
                add(new ArrayList<String>(){{
                    add("Cow");
                    add("Pig");
                    add("Sheep");
                    add("Chicken");
                }});
                add(new ArrayList<String>(){{
                    add("Zombie");
                    add("Creeper");
                    add("Skeleton");
                    add("Spider");
                    add("PigZombie");
                    add("Enderman");
                }});
            }});
            spawner.put(String.format("%s:%s:%s:%s", pos.x, pos.y, pos.z, pos.getLevel().getFolderName()), hashdata);
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev){
        Block pos = ev.getBlock();
        if(ev.isCancelled()) return;
        if(pos.getId() == Item.MONSTER_SPAWNER){
            /*if(isset(spawn["{pos.x}:{pos.y}:{pos.z}"])){
                unset(spawn["{pos.x}:{pos.y}:{pos.z}"]);
            }else if(isset(spawn["{pos.x}:{pos.y}:{pos.z}:{pos.getLevel().getFolderName()}"])){
                unset(spawn["{pos.x}:{pos.y}:{pos.z}:{pos.getLevel().getFolderName()}"]);
            }*/
        }

        if(
            ev.getBlock().getId() == Block.STONE
            || ev.getBlock().getId() == Block.STONE_BRICK
            || ev.getBlock().getId() == Block.STONE_WALL
            || ev.getBlock().getId() == Block.STONE_BRICK_STAIRS
        ){
            if(ev.getBlock().getLightLevel() < 12 && Utils.rand(1,3) < 2){
                Silverfish entity = (Silverfish) create("Silverfish", pos);
                if(entity != null){
                    entity.spawnToAll();
                }
            }
        }
    }

    @EventHandler
    public void ExplosionPrimeEvent(ExplosionPrimeEvent ev){
        ev.setCancelled(!this.getData("entity.explode", true));
    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent ev){
        /*Entity entity = ev.getEntity();
        if(!(entity instanceof BaseEntity) || !isset(drops[entity.NETWORK_ID])) return;
        drops = [];
        foreach(drops[entity.NETWORK_ID] as key => data){
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
                    }else if(ent instanceof Animal || ent instanceof cn.nukkit.entity.Animal){
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
                if((ent = create(type1, pos)) == null){
                    if((ent = create(type2, pos)) == null){
                        output += "엔티티를 소환하는도중 에러가 발생했습니다";
                        break;
                    }
                }
                output = "";
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