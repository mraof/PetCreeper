package mathew.petcreeper;

import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

public class PetMain extends JavaPlugin
{
    private final PetPlayerListener playerListener = new PetPlayerListener(this);
    private final PetEntityListener entityListener = new PetEntityListener(this);
    
    public ArrayList<PetData> petDataList = new ArrayList<PetData>();

    private final HashMap<Player, ArrayList<Pet>> pets = new HashMap<Player, ArrayList<Pet>>();

    private File dataFolder;

    PetMainLoop mainLoop;

    public void onEnable()
    {
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
        
        pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.EXPLOSION_PRIME, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        

        // Register our commands
        getCommand("pctp").setExecutor(this);
        getCommand("pcfree").setExecutor(this);
        getCommand("pcattack").setExecutor(this);
        getCommand("pcname").setExecutor(this);

        // Load the configuration
        dataFolder = new File("plugins/PetCreeper");
        if(!dataFolder.exists())
            dataFolder.mkdirs();

        PetConfig.load(new File(dataFolder, "config.yml"));
        
        //Load the list of players with creepers
        /*File creeperFile = new File(dataFolder, "pets.txt");
        if(creeperFile.exists())
        {
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(creeperFile));
                String line;
                while((line = in.readLine()) != null)
                {
                    if(!line.equals("\n"))
                    {
                        String[] parts = line.split("\t", 5);
                        if(CreatureType.fromName(parts[1]) == CreatureType.SHEEP)
                            petDataList.add(new PetData(parts[0], Integer.parseInt(parts[2]), Boolean.parseBoolean(parts[3]), Byte.parseByte(parts[4])));
                        else if(CreatureType.fromName(parts[1]) == CreatureType.PIG)
                            petDataList.add(new PetData(parts[0], Integer.parseInt(parts[2]), Boolean.parseBoolean(parts[3])));
                        else
                            petDataList.add(new PetData(parts[0], CreatureType.fromName(parts[1]), Integer.parseInt(parts[2])));
                    }
                }
                in.close();
            }
            catch(Exception e)
            {
            }
        }*/

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is enabled!");

        //Start the main loop
        mainLoop = new PetMainLoop(this);
    }

    public void onDisable()
    {
        //End main loop
        mainLoop.end();
        
        //Save any players to a file
        /*for(Map.Entry entry: pets.entrySet())
        {
            Player p = (Player)entry.getKey();
            Creature c = (Creature)entry.getValue();

            if(c instanceof Sheep)
            {
                Sheep s = (Sheep)c;
                petDataList.add(new PetData(p.getName(), s.getHealth(), s.isSheared(), s.getColor().getData()));
            }
            else if(c instanceof Pig)
            {
                Pig pig = (Pig)c;
                petDataList.add(new PetData(p.getName(), pig.getHealth(), pig.hasSaddle()));
            }
            else
                petDataList.add(new PetData(p.getName(), getPetTypeOf(p), c.getHealth()));
            c.remove();
        }

        //Write the list of players with pets
        File petFile = new File(dataFolder, "pets.txt");
        try
        {
            BufferedWriter in = new BufferedWriter(new FileWriter(petFile, false));
            for(PetData pet : petDataList)
            {
                in.write(pet.player + "\t" + pet.type.getName() + "\t" + pet.hp);
                if(pet.type == CreatureType.SHEEP)
                    in.write("\t" + pet.sheared + "\t" + pet.color);
                else if(pet.type == CreatureType.PIG)
                    in.write("\t" + pet.saddled);
                in.write("\n");
            }
            in.close();
        }
        catch(Exception e)
        {
        }*/

        PluginDescriptionFile pdf = this.getDescription();
        System.out.println(pdf.getName() + " version " + pdf.getVersion() + " is disabled.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(!(sender instanceof Player))
            return false;
        Player p = (Player)sender;
        
        if(commandLabel.equalsIgnoreCase("pctp"))
        {
            /*if(!isPermitted(p, "pctp"))
            {
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }*/

            if(isPetOwner(p))
            {
                if(args.length == 0)
                {
                    //Teleport all pets
                    ArrayList<Pet> petList = getPetsOf(p);
                    for(Pet pet : petList)
                        pet.teleport();
                    if(petList.size() == 1)
                        p.sendMessage(ChatColor.GREEN + "Your pet teleported to you.");
                    else
                        p.sendMessage(ChatColor.GREEN + "Your pets teleported to you.");
                }
                else
                {
                    Pet pet = getPetOf(p, args[0]);
                    pet.teleport();
                    p.sendMessage(ChatColor.GREEN + pet.getName() + " teleported to you.");
                }
            }
            else
                p.sendMessage(ChatColor.RED + "You don't own any pets.");
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("pcfree"))
        {
            if(isPetOwner(p))
            {
                if(args.length == 0)
                {
                    if(getPetsOf(p).size() == 1)
                        p.sendMessage("You freed your pet.");
                    else
                        p.sendMessage("You freed your pets.");
                    untameAllPets(p);
                }
                else
                {
                    Pet pet = getPetOf(p, args[0]);
                    if(pet != null)
                    {
                        p.sendMessage("You freed " + pet.getName() + ".");
                        untamePet(pet);
                    }
                    else p.sendMessage(ChatColor.RED + "You don't own that pet.");
                }
            }
            else
                p.sendMessage(ChatColor.RED + "You don't own any pets.");
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("pcattack"))
        {
            /*if(!isPermitted(p, "pcattack"))
            {
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }*/
            
            if(args.length < 1)
            {
                p.sendMessage(ChatColor.RED + "Usage: /pcattack <Player> <Petname>");
                return true;
            }
            
            if(isPetOwner(p))
            {
                LivingEntity target = getServer().getPlayer(args[0]);
                if(args.length < 2)
                {
                    ArrayList<Pet> petList = getPetsOf(p);
                    for(Pet pet : petList)
                        pet.setTarget(target);
                    
                    if(petList.size() == 1)
                    {
                        if(target != null)
                            p.sendMessage(ChatColor.GREEN + "Your pet is now attacking " + ((Player)target).getDisplayName() + ".");
                        else
                            p.sendMessage(ChatColor.GREEN + "Your pet is now not attacking anything.");
                    }
                    else
                    {
                        if(target != null)
                            p.sendMessage(ChatColor.GREEN + "Your pets are now attacking " + ((Player)target).getDisplayName() + ".");
                        else
                            p.sendMessage(ChatColor.GREEN + "Your pets are now not attacking anything.");
                    }
                }
                else
                {
                    Pet pet = getPetOf(p, args[1]);
                    if(pet != null)
                    {
                        pet.setTarget(target);
                        if(target != null)
                            p.sendMessage(ChatColor.GREEN + pet.getName() + " is now attacking " + ((Player)target).getDisplayName() + ".");
                        else
                            p.sendMessage(ChatColor.GREEN + pet.getName() + " is now not attacking anything.");
                    }
                    else
                        p.sendMessage(ChatColor.RED + "You don't own that pet.");
                }
            }
            else
                p.sendMessage(ChatColor.RED + "You don't own any pets.");
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("pcname"))
        {
            if(args.length != 2)
            {
                p.sendMessage(ChatColor.RED + "Usage: /pcname <Petname> <New petname>");
                return true;
            }
            
            Pet pet = getPetOf(p, args[0]);
            p.sendMessage(pet.getName() + " has been renamed to " + args[1] + ".");
            pet.setName(args[1]);
            
        }
        return false;
    }

    public boolean isPermitted(Player p, String permission)
    {
        return p.hasPermission("petcreeper." + permission);
    }
    
    public ArrayList<Pet> getPetsOf(Player p)
    {
        return pets.get(p);
    }
    
    public ArrayList<Pet> getForcePetsOf(Player p)
    {
        ArrayList<Pet> petList = pets.get(p);
        if(petList == null)
        {
            petList = new ArrayList<Pet>();
            pets.put(p, petList);
        }
        return petList;
    }
    
    /*public Creature spawnPetOf(Player p, CreatureType type)
    {
        Creature c = getPetOf(p);
        if(c != null)
        {
            c.remove();
            untamePet(p);
        }
        
        Location pos = p.getLocation().clone();
        pos.setY(pos.getY() + 1);
        c = (Creature)p.getWorld().spawnCreature(pos, type);
        if(c == null)
            return null;
        tamePet(p, c);
        return c;
    }*/

    public void tamePet(final Player p, final Creature pet)
    {
        if(pet != null)
        {
            ArrayList<Pet> petList = getForcePetsOf(p);
            petList.add(new Pet(pet, p));
        }
    }
    
    public void untamePet(final Pet pet)
    {
        if(pets.containsKey(pet.master))
        {
            ArrayList<Pet> petList = getPetsOf(pet.master);
            for(int i = 0; i < petList.size(); i++)
                if(petList.get(i) == pet)
                {
                    petList.remove(i);
                    if(petList.isEmpty())
                        pets.remove(pet.master);
                    break;
                }
        }
    }

    public void untamePet(final Player player, final Creature pet)
    {
        if(pets.containsKey(player))
        {
            ArrayList<Pet> petList = getPetsOf(player);
            for(int i = 0; i < petList.size(); i++)
                if(petList.get(i).pet == pet)
                {
                    petList.remove(i);
                    if(petList.isEmpty())
                        pets.remove(player);
                    break;
                }
        }
    }
    
    public void untamePet(final Player player, final String name)
    {
        if(pets.containsKey(player))
        {
            ArrayList<Pet> petList = getPetsOf(player);
            if(petList == null) return;
            
            for(int i = 0; i < petList.size(); i++)
                if(petList.get(i).getName().equals(name))
                {
                    petList.remove(i);
                    if(petList.isEmpty())
                        pets.remove(player);
                    break;
                }
        }
    }
    
    public void untameAllPets(final Player player)
    {
        if(pets.containsKey(player))
        {
            pets.remove(player);
        }
    }

    public Pet getPetOf(final Player player, final String name)
    {
        if(pets.containsKey(player))
        {
            ArrayList<Pet> petList = getPetsOf(player);
            for(Pet p : petList)
                if(p.getName().equalsIgnoreCase(name)) return p;
            return null;
        }
        else
        {
            return null;
        }
    }
    
    public Pet creatureToPet(final Creature pet)
    {
        for(ArrayList<Pet> list : pets.values())
        {
            for(Pet p : list)
            {
                if(p.pet == pet) return p;
            }
        }
        
        return null; 
    }

    public boolean isPetOwner(final Player p)
    {
        return pets.containsKey(p);
    }

    public Set<Player> getMasterList()
    {
        return pets.keySet();
    }
}

class PetData
{
    public String player;
    public CreatureType type;
    public int hp;

    //For sheep only
    public boolean sheared;
    public byte color;
    
    //For pigs only
    public boolean saddled;

    public PetData(String player, CreatureType type, int hp)
    {
        this.player = player;
        this.type = type;
        this.hp = hp;
    }

    public PetData(String player, int hp, boolean sheared, byte color)
    {
        this.player = player;
        this.type = CreatureType.SHEEP;
        this.sheared = sheared;
        this.color = color;
    }
    
    public PetData(String player, int hp, boolean saddled)
    {
        this.player = player;
        this.type = CreatureType.PIG;
        this.hp = hp;
        this.saddled = saddled;
    }
}
