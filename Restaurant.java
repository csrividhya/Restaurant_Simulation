import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant 
{
	public static int num_diners;
	public static Integer num_diners_left=0;
	public static long startTime;
	//public static Lock burger_machine, fries_machine, coke_machine;
	public static Object burger_machine, fries_machine, coke_machine;
	public static boolean b_free, f_free,c_free;
	public static ArrayList<Integer>availableTables;
	public static PriorityQueue<Diner> diners;
	public static ArrayList<Diner>hungryDiners;
	
	public static void main(String args[]) throws NumberFormatException, IOException, InterruptedException
	{
		String input_file = args[0];
		FileReader fReader = new FileReader(input_file);
		BufferedReader bReader = new BufferedReader(fReader);
		
		num_diners = Integer.parseInt(bReader.readLine().trim());
		int num_tables =Integer.parseInt(bReader.readLine().trim());
		int num_cooks = Integer.parseInt(bReader.readLine().trim());
		
		startTime = System.currentTimeMillis()/1000;
		
		availableTables = new ArrayList<Integer>(num_tables);
		for(int i=1;i<=num_tables;i++)
		{
			availableTables.add(i);
		}
		
		
		String diner_input;
		int diner_id, arrival_time, burger, fries, coke;
		
		diners = new PriorityQueue<>(num_diners+1);
		hungryDiners = new ArrayList<Diner>(num_diners);
		
		/*burger_machine = new ReentrantLock();
		fries_machine = new ReentrantLock();
		coke_machine = new ReentrantLock();
		*/
		
		burger_machine = new Object();
		fries_machine= new Object();
		coke_machine = new Object();
		f_free = true;
		b_free=true;
		c_free=true;
		
		for(int i=0;i<num_diners;i++)
		{
			diner_input = bReader.readLine().trim();
			String[] details = diner_input.split(",");
			diner_id = i+1;
			arrival_time = Integer.parseInt(details[0]);
			burger = Integer.parseInt(details[1]);
			fries = Integer.parseInt(details[2]);
			coke = Integer.parseInt(details[3]);
			Order temp_o = new Order(burger, fries, coke);
			//create diner obj and push it into priority queue bro!
			Diner diner_obj = new Diner(diner_id, arrival_time,temp_o);
			diners.add(diner_obj);
		}
		
		for(int i=0;i<num_cooks;i++)
		{
			Cook c = new Cook(i+1);
			Thread cook_thread = new Thread(c);
			cook_thread.start();
		}
		
		int prevDinerArrival=0,waitTime;
		
		for(int i=0;i<num_diners;i++)
		{
			Diner temp_d = diners.poll();
			waitTime = (temp_d.arrival_time - prevDinerArrival)*1000;
			Thread diner_thread = new Thread(temp_d);
			Thread.sleep(waitTime);
			diner_thread.start();
			prevDinerArrival = temp_d.arrival_time;
			
		}
		
		bReader.close();
		fReader.close();
		
		
	}
}
