package mathew.petcreeper;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PetPlayerListener extends PlayerListener
{
    private final PetMain plugin;
    
    private void disconnect(Player p)
    {
        if(plugin.isPetOwner(p))
        {
            for(Pet pet : plugin.getPetsOf(p))
            {
                /*if(pet.type == CreatureType.SHEEP)
                {
                    Sheep s = (Sheep)pet.pet;
                    plugin.petDataList.add(new PetData(p.getName(), s.getHealth(), s.isSheared(), s.getColor().getData()));
                }
                else if(pet.type == CreatureType.PIG)
                {
                    Pig pig = (Pig)pet.pet;
                    plugin.petDataList.add(new PetData(p.getName(), pig.getHealth(), pig.hasSaddle()));
                }
                else
                    plugin.petDataList.add(new PetData(p.getName(), pet.type, pet.pet.getHealth()));*/
                plugin.untamePet(pet);
                pet.pet.remove();
            }
        }
    }

    private void teleport(Player p)
    {
        if(!plugin.isPetOwner(p)) return;
        
        for(Pet pet : plugin.getPetsOf(p))
        {
            if(pet.following)
                pet.teleport();
        }
    }

    public PetPlayerListener(PetMain instance)
    {
        plugin = instance;
    }

    public void onPlayerJoin(PlayerJoinEvent event)
    {
        /*Player p = event.getPlayer();
        for(int i = 0; i < plugin.petDataList.size(); i++)
        {
            PetData pet = plugin.petDataList.get(i);
            if(pet.player.equals(p.getName()))
            {
                plugin.petDataList.remove(i);
                Creature c = plugin.spawnPetOf(p, pet.type);
                c.setHealth(pet.hp);
                if(pet.type == CreatureType.SHEEP)
                {
                    Sheep s = (Sheep)c;
                    if(pet.sheared) s.setSheared(true);
                    s.setColor(DyeColor.getByData(pet.color));
                }
                else if(pet.type == CreatureType.PIG)
                {
                    Pig pig = (Pig)c;
                    if(pet.saddled) pig.setSaddle(true);
                }
                p.sendMessage(ChatColor.GREEN + "Your pet " + plugin.getPetNameOf(p) + " greets you.");
                break;
            }
        }*/
    }

    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        disconnect(p);
    }
    
    public void onPlayerKick(PlayerKickEvent event)
    {
        Player p = event.getPlayer();
        disconnect(p);
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        Entity e = event.getRightClicked();
        Player p = event.getPlayer();

        if(e instanceof Creature)
        {
            Pet pet = plugin.creatureToPet((Creature)e);
            if(pet != null)
            {
                if(pet.master == p)
                {
                    Entity passenger = pet.pet.getPassenger();
                    if(!(pet.type == CreatureType.PIG) && passenger == p)
                        pet.pet.eject();
                    else if(PetConfig.ridable && p.getItemInHand().getType() == Material.SADDLE && passenger == null)
                    {
                        //Pigs get saddled, so dont mount them
                        if(pet.type == CreatureType.PIG)
                            return;
                        pet.pet.setPassenger(p);
                    }
                    //Does the player have permission to ride this pet?
                    else if(!plugin.isPermitted(p, "ride. " + pet.type.getName()))
                    {
                        p.sendMessage(ChatColor.RED + "You don't have permission to ride that creature.");
                        return;
                    }
                    else if(pet.following)
                    {
                        p.sendMessage(ChatColor.GOLD + pet.getName() + " is now not following you.");
                        pet.following = false;
                    }
                    else
                    {
                        p.sendMessage(ChatColor.GOLD + pet.getName() + " is now following you.");
                        pet.following = true;
                    }
                }
                else
                {
                    p.sendMessage(ChatColor.GOLD + "That " + pet.getSpecies() + " belongs to " + pet.master.getDisplayName() + ".");
                }

            }
            else
            {
                Creature c = (Creature)e;
                //Don't tame the "untamable"
                if(c instanceof Wolf || c instanceof Skeleton || c instanceof Ghast || c instanceof Slime)
                    return;

                ItemStack bait = p.getItemInHand();
                int amt = bait.getAmount();
                if(bait.getType() == PetConfig.getBait(c) && amt > 0)
                {
                    //Try to tame a wild mob
                    
                    //Does the player have permission to tame this pet?
                    if(!plugin.isPermitted(p, "tame. " + Pet.getTypeOf(c).getName()))
                    {
                        p.sendMessage(ChatColor.RED + "You don't have permission to tame that creature.");
                        return;
                    }

                    if(amt == 1)
                        p.getInventory().removeItem(bait);
                    else
                        bait.setAmount(amt - 1);
                    plugin.tamePet(p, c);

                    p.sendMessage(ChatColor.GREEN + "You tamed the " + Pet.getTypeOf(c).getName() + "!");
                }
            }
        }
    }

    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        teleport(event.getPlayer());
    }

    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        teleport(event.getPlayer());
    }
    
    public void onPlayerPortal(PlayerPortalEvent event)
    {
        teleport(event.getPlayer());
    }
}
