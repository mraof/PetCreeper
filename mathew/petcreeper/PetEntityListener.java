package mathew.petcreeper;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

//TODO figure out a way to replace the deprecated events

public class PetEntityListener extends EntityListener
{
    private final PetMain plugin;

    public PetEntityListener(PetMain instance)
    {
        plugin = instance;
    }

    public void onEntityTarget(EntityTargetEvent event)
    {
        Entity e = event.getEntity();
        if(e instanceof Creature)
        {
            Pet pet = plugin.creatureToPet((Creature)e);
            if(pet != null)
            {
                if(!pet.following || pet.pet.getPassenger() != null || pet.pet.getLocation().distance(pet.pet.getLocation()) < PetConfig.idleDistance)
                    event.setTarget(null);
                else
                    event.setTarget(pet.master);
            }
        }
    }

    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        Entity e = event.getEntity();
        if(e instanceof Creature)
        {
            if(plugin.creatureToPet((Creature)e) != null)
                event.setCancelled(true);
        }
    }

    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity e = event.getEntity();
        if(e instanceof Creature)
        {
            Pet pet = plugin.creatureToPet((Creature)e);
            
            if(pet != null)
            {
                if(!PetConfig.provokable)
                {
                    event.setCancelled(true);
                    return;
                }
                Entity attacker = null;
                if(event instanceof EntityDamageByProjectileEvent)
                {
                    attacker = ((EntityDamageByProjectileEvent)event).getDamager();
                }
                else if(event instanceof EntityDamageByEntityEvent)
                {
                    attacker = ((EntityDamageByEntityEvent)event).getDamager();
                }
                else return;
                if(attacker != null && attacker instanceof Player)
                {
                    Player p = (Player)attacker;
                    if(pet.master == p)
                    {
                        p.sendMessage(ChatColor.RED + "You made " + pet.getName() + " angry!");

                        if(pet.pet instanceof Monster)
                            pet.pet.setTarget(p);
                        else
                            pet.pet.setTarget(null);
                        
                        plugin.untamePet(pet);
                    }
                }
            }
            else if(PetConfig.attackTame)
            {
                Creature c = (Creature)e;
                
                //Tame the darn thing
                Entity attacker = null;
                if(event instanceof EntityDamageByProjectileEvent)
                {
                    attacker = ((EntityDamageByProjectileEvent)event).getDamager();
                }
                else if(event instanceof EntityDamageByEntityEvent)
                {
                    attacker = ((EntityDamageByEntityEvent)event).getDamager();
                }
                else return;
                if(attacker != null && attacker instanceof Player)
                {
                    Player p = (Player)attacker;
                    //Don't tame the "untamable"
                    if(c instanceof Wolf || c instanceof Skeleton || c instanceof Ghast || c instanceof Slime)
                        return;
                    

                    ItemStack bait = p.getItemInHand();
                    int amt = bait.getAmount();
                    if(bait.getType() == PetConfig.getBait(c) && amt > 0)
                    {
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
                        event.setCancelled(true);
                    }
                }
            }

        }
    }

    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity e = event.getEntity();
        if(e instanceof Creature)
        {
            Pet pet = plugin.creatureToPet((Creature)e);
            if(pet != null)
            {
                pet.master.sendMessage(ChatColor.RED + pet.getName() + " has died!");
                plugin.untamePet(pet);
            }
        }
    }
}
