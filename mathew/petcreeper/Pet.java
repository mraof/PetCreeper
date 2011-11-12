package mathew.petcreeper;

import org.bukkit.Location;
import org.bukkit.entity.*;

public class Pet
{
    public Creature pet = null;
    public CreatureType type = null;
    public Player master = null;
    boolean following = false;
    
    private LivingEntity target = null;
    private String name = null;
    
    public Pet(Creature pet, Player master)
    {
        this.pet = pet;
        this.master = master;
        this.following = true;
        this.target = null;
        
        this.name = null;
        
        this.type = getTypeOf(pet);
    }
    
    public Pet(Creature pet, String name, Player master)
    {
        this(pet, master);
        this.name = name;
    }
    
    public String getName()
    {
        if(name != null) return name;
        
        if(type == CreatureType.PIG_ZOMBIE)
            return "ZombiePigman";
        return type.getName();//.toLowerCase();
    }
    
    public String getSpecies()
    {
        if(type == CreatureType.PIG_ZOMBIE)
            return "zombie pigman";
        return type.getName().toLowerCase();
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setTarget(LivingEntity target)
    {
        //Don't set the targets of non-monsters
        if(pet instanceof Monster)
        {
            this.target = target;
            pet.setTarget(target);
        }
    }
    
    public LivingEntity getTarget()
    {
        return target;
    }
    
    public void teleport()
    {
        Location pos = master.getLocation().clone();
        pos.setY(pos.getY() + 1);
        
        //Instead of teleporting, spawn a new pet at the location.
        //Fixes some bugs.
        pet.remove();
        pet = (Creature)master.getWorld().spawnCreature(pos, type);
        
        target = null;
        pet.setTarget(null);
    }
    
    public static CreatureType getTypeOf(Creature c)
    {
        //Get the type
        if(c instanceof Chicken)
            return CreatureType.CHICKEN;
        if(c instanceof Cow)
            return CreatureType.COW;
        if(c instanceof Creeper)
            return CreatureType.CREEPER;
        if(c instanceof Enderman)
            return CreatureType.ENDERMAN;
        if(c instanceof Giant)
            return CreatureType.GIANT;
        if(c instanceof Pig)
            return CreatureType.PIG;
        if(c instanceof PigZombie)
            return CreatureType.PIG_ZOMBIE;
        if(c instanceof Sheep)
            return CreatureType.SHEEP;
        if(c instanceof Slime)
            return CreatureType.SLIME;
        if(c instanceof Spider)
            return CreatureType.SPIDER;
        if(c instanceof Squid)
            return CreatureType.SQUID;
        if(c instanceof Zombie)
            return CreatureType.ZOMBIE;
        return null;
    }
}
