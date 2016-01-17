# EntityManager  
  
**현재 미완성 입니다**  
  
Author: **[@milk0417(승원)](https://github.com/milk0417)**  

**[NOTICE] This plugin is NOT perfect at all.  
It can cause your server crash, or something bad else :P**
  
EntityManager is a plugin for managing entities, literally.  
Most entities and mobs are moving around, and jumps if needed.

EntityManager also has simple API for developers,  
such as clear(Class[] type, Level level) (Clears all entities with given type.),  
create(int|String type, Position pos). See documentation page for details.

### Method
  * EntityManager
    * public static Map<Integer, BaseEntity> getEntities();
    * public static Map<Integer, BaseEntity> getEntities(Level level); #returns array of all entities in given server.
    * public static void clear();
    * public static void clear(Class[] type, type);
    * public static void clear(Class[] type, Level level); #clears all entities with given type.
    * public static BaseEntity create(int|String type, Position pos, Object... args); #spawns entity with given type and pos
  * BaseEntity
    * public boolean isCreated(); #check if entity is created.
    * public boolean isMovement() #check if entity is movable.
    * public boolean isWallCheck() #check if entity can go through walls.
    * public void setMovement(boolean value) #literally.
    * public void setWallCheck(boolean value) #set if entity will check walls when it moves.
  * Monster
    * public double getDamage();
    * public double getDamage(int difficulty); #returns monster's damage in given difficulty.
    * public void setDamage(double damage)
    * public void setDamage(double[] damage)
    * public void setDamage(double damage, int difficulty) #set monster's damage with given difficulty.
  * PigZombie
    * public boolean isAngry() #r u angry?
    * public void setAngry(int angry) #set how angry he is.  
  
### Commands
  * /entitymanager
    * usage: /entitymanager (check|remove|spawn)
    * permission: entitymanager.command
  * /entitymanager check
    * usage: /entitymanager check (Level="")
    * permission: entitymanager.command.check
    * description: Check the number of entities(If blank, it is set as a default Level)
  * /entitymanager remove
    * usage: /entitymanager remove (Level="")
    * permission: entitymanager.command.remove
    * description: Remove all entities in Level(If blank, it is set as a default Level)
  * /entitymanager spawn:
    * usage: /entitymanager spawn (type) (x="") (y="") (z="") (Level="")
    * permission: entitymanager.command.spawn
    * description: literally(If blank, it is set as a Player

### Method Examples
``` java  
//Entity Method  
EntityManager.getEntities().forEach((id, baseEntity) -> {  
    if(!baseEntity.isMovement()){  
        baseEntity.setMovement(true);  
    }  
    if(baseEntity instanceof Monster){
        Monster mob = (Monster) baseEntity;
        
        mob.setDamage(10);
          
        mob.setMaxDamage(10);  
        mob.setMinDamage(10);  
    }  
});  
  
//Create Entity  
Entity arrow = EntityManager.create("Arrow", position, player, true); //Nukkit default Class
Entity baseEntity = EntityManager.create("Zombie", position);  
  
//Remove Entity  
EntityManager.clear(new Class[]{BaseEntity.class, Projectile.class, Item.class});  
```
