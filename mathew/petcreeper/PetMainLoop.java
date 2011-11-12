package mathew.petcreeper;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

public class PetMainLoop extends Thread
{
    private final PetMain plugin;

    private boolean running;

    public PetMainLoop(PetMain instance)
    {
        plugin = instance;
        running = true;
        this.start();
    }

    public void run()
    {
        System.out.println("PetCreeper main loop running.");
        while(running)
        {            
            for(Player p : plugin.getMasterList())
            {
                for(Pet pet : plugin.getPetsOf(p))
                {
                    if(!pet.following || pet.pet.getPassenger() != null || pet.pet.getLocation().distance(p.getLocation()) < PetConfig.idleDistance)
                        pet.pet.setTarget(null);
                    else
                        pet.pet.setTarget(p);
                }
            }

            try
            {
                this.sleep(500);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public void end()
    {
        running = false;
    }
}
