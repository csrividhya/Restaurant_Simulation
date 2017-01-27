
public class Diner implements Runnable, Comparable<Diner>
{
public int diner_id, arrival_time, table_assigned;
public Order order;

	public Diner(int id, int time, Order o)
	{
		this.diner_id= id;
		this.arrival_time=time;
		this.order =o;
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
	public void enter_restaurant()
	{
		System.out.println(getTime()+"Diner "+diner_id+" arrives.");
	}
	
	public void acquire_table() throws InterruptedException
	{
		synchronized (Restaurant.availableTables) 
		{
			if(Restaurant.availableTables.isEmpty()==true)
			{
				Restaurant.availableTables.wait();
			}
			else
			{
				table_assigned=Restaurant.availableTables.remove(0);
				System.out.print(getTime()+"Diner "+diner_id+" is seated at Table "+table_assigned+" .");
			}
		}
	}
	
	public void place_order()
	{
		synchronized (Restaurant.hungryDiners)
		{
			Restaurant.hungryDiners.add(this);
			Restaurant.hungryDiners.notify();
		}
	}
	
	public void wait_for_food() throws InterruptedException
	{
		synchronized (this) 
		{
			this.wait();
		}
	}
	
	public void eat() throws InterruptedException
	{
		System.out.println(getTime()+"Diner "+diner_id+" 's order is ready. Diner "+diner_id+" starts eating.");
		Thread.sleep(30000);
	}
	
	public void leave_restaurant()
	{
		System.out.println(getTime()+"Diner "+diner_id+" finished. Diner "+diner_id+" leaves the restaurant");
		synchronized(Restaurant.availableTables)
		{
			Restaurant.availableTables.add(table_assigned);
			Restaurant.availableTables.notify();
		}
	}
	
	public void check_if_lastDiner()
	{
		synchronized (Restaurant.num_diners_left) 
		{
			Restaurant.num_diners_left++;
			
			if(Restaurant.num_diners_left==Restaurant.num_diners)
			{
				System.out.println(getTime()+" The last diner leaves the restaurant");
				System.exit(0);
			}
		}
	}
	
	public void run()
	{
		enter_restaurant();
		
		try 
		{
			acquire_table();
		}
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		place_order();
		
		try 
		{
			wait_for_food();
		}
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			eat();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		leave_restaurant();
		check_if_lastDiner();
		
	}

	@Override
	public int compareTo(Diner o) {
		// TODO Auto-generated method stub
		if (arrival_time < o.arrival_time)
            return -1;
        else if (arrival_time> o.arrival_time)
            return 1;
		return 0;
	}

}


