package mathew.petcreeper;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.config.Configuration;

public class PetConfig
{
    public static Material chicken;
    public static Material cow;
    public static Material creeper;
    public static Material enderman;
    public static Material giant;
    public static Material pig;
    public static Material pigZombie;
    public static Material sheep;
    //public static Material slime;
    public static Material spider;
    public static Material squid;
    public static Material zombie;

    public static boolean provokable;
    public static boolean ridable;
    public static boolean attackTame;
    public static int idleDistance;
    
    public static boolean defend;
    public static boolean creeperDefend;
    
    //public static boolean usePermissions;

    public static void load(File settings)
    {
        Configuration config = new Configuration(settings);
        config.load();

        //Mob material
        chicken = Material.getMaterial(config.getInt("Chicken", 0));
        cow = Material.getMaterial(config.getInt("Cow", 0));
        creeper = Material.getMaterial(config.getInt("Creeper", 0));
        enderman = Material.getMaterial(config.getInt("Enderman", 0));
        giant = Material.getMaterial(config.getInt("Giant", 0));
        pig = Material.getMaterial(config.getInt("Pig", 0));
        pigZombie = Material.getMaterial(config.getInt("PigZombie", 0));
        sheep = Material.getMaterial(config.getInt("Sheep", 0));
        //slime = Material.getMaterial(config.getInt("Slime", 0));
        spider = Material.getMaterial(config.getInt("Spider", 0));
        squid = Material.getMaterial(config.getInt("Squid", 0));
        zombie = Material.getMaterial(config.getInt("Zombie", 0));

        //Other options
        provokable = config.getBoolean("Provokable", true);
        ridable = config.getBoolean("Ridable", true);
        attackTame = config.getBoolean("AttackTame", false);
        idleDistance = config.getInt("IdleDistance", 5);
        
        //Pet attack options
        defend = config.getBoolean("Defend", true);
        creeperDefend = config.getBoolean("CreeperDefend", false);
        
        //usePermissions = config.getBoolean("Permissions", false);
    }

    public static Material getBait(final Creature pet)
    {
        if(pet instanceof Chicken)
            return chicken;
        if(pet instanceof Cow)
            return cow;
        if(pet instanceof Creeper)
            return creeper;
        if(pet instanceof Enderman)
            return enderman;
        if(pet instanceof Giant)
            return giant;
        if(pet instanceof Pig)
            return pig;
        if(pet instanceof PigZombie)
            return pigZombie;
        if(pet instanceof Sheep)
            return sheep;
        //if(pet instanceof Slime)
        //    return slime;
        if(pet instanceof Spider)
            return spider;
        if(pet instanceof Squid)
            return squid;
        if(pet instanceof Zombie)
            return zombie;
        return Material.AIR;
    }
}
