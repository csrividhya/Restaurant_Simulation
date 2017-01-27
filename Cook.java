
public class Cook implements Runnable{
	public int cook_id;
	Diner diner_served;
	
	public Cook(int id)
	{
		this.cook_id= id;		
	}
	
	public String getTime()
	{
		long currentTime = System.currentTimeMillis()/1000;
		long actual = currentTime-Restaurant.startTime;
		String time;
		
		if(actual<60)
		{
			time=new String("00:");
			time=time+actual;
		}
		else
		{
			long hr = actual/60;
			long min = actual%60;
			time=String.format("%02d", hr) + ":" + String.format("%02d", min);
		}
		time+=" - ";
		return time;	
	}
	
	public void serve_diner()
	{
		synchronized (Restaurant.hungryDiners)
		{
			while(Restaurant.hungryDiners.isEmpty())
			{
				try {
					Restaurant.hungryDiners.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			diner_served = Restaurant.hungryDiners.remove(0);
			System.out.println(getTime()+"Cook "+cook_id+" processing Diner "+diner_served.diner_id+"'s order");
		}
	}
	
	public void make_food() throws InterruptedException
	{
		Order o = diner_served.order;
		 while(o.burger+o.fries+o.coke>0)
		{
			if(o.burger>0)
			{
				synchronized(Restaurant.burger_machine){
					if(Restaurant.b_free==false)
					{
						Restaurant.burger_machine.wait();
					}
					else
					{
						System.out.println(getTime()+"Cook "+cook_id+" uses the burger machine.");
						Thread.sleep(5000);
						--o.burger;
						if(o.burger==0)
						{
							Restaurant.b_free=true;
							Restaurant.burger_machine.notify();
						}
							
					}
				}
				/*boolean b_proceed = Restaurant.burger_machine.tryLock();
				if(b_proceed == true)
				{
					try
				{
			
					System.out.println(getTime()+"Cook "+cook_id+" uses the burger machine.");
					Thread.sleep(5000);
					--o.burger;
					
				}
				finally
				{
					if(o.burger==0)
						Restaurant.burger_machine.unlock();
				}
			}*/
		}

			if(o.fries>0)
			{
				synchronized(Restaurant.fries_machine){
					if(Restaurant.f_free==false)
					{
						Restaurant.fries_machine.wait();
					}
					else
					{
						System.out.println(getTime()+"Cook "+cook_id+" uses the fries machine.");
						Thread.sleep(3000);
						--o.fries;
						if(o.fries==0)
							{
								Restaurant.f_free=true;
								Restaurant.fries_machine.notify();
							}
					}
				}
				
				/*boolean f_proceed = Restaurant.fries_machine.tryLock();
				if(f_proceed == true)
				{
					try
				{
					System.out.println(getTime()+"Cook "+cook_id+" uses the fries machine.");
					Thread.sleep(3000);
					--o.fries;
				}
				finally
				{
					if(o.fries==0)
						Restaurant.fries_machine.unlock();
				}
			}*/
		}

			if(o.coke>0)
			{
				synchronized(Restaurant.coke_machine){
					if(Restaurant.c_free==false)
					{
						Restaurant.coke_machine.wait();
					}
					else
					{
						System.out.println(getTime()+"Cook "+cook_id+" uses the coke machine.");
						Thread.sleep(1000);
						--o.coke;
						if(o.coke==0)
							{
								Restaurant.c_free=true;
								Restaurant.coke_machine.notify();
							}
					}
				}
				
				
				/*boolean c_proceed = Restaurant.coke_machine.tryLock();
				if(c_proceed == true)
				{
					try
				{
					System.out.println(getTime()+"Cook "+cook_id+" uses the Coke machine.");
					Thread.sleep(1000);
					--o.coke;
				}
				finally
				{
					if(o.coke==0)
						Restaurant.coke_machine.unlock();
				}
			}*/
		}
		}//while
		 
    if(o.burger==0 && o.fries==0 && o.coke==0) //order ready yo!
    {
    	synchronized (diner_served) 
    	{
			diner_served.notify();
		}
    }
		 
	}
	
	public void run()
	{
	while(true)
	{
		serve_diner();
		try {
			make_food();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
}
