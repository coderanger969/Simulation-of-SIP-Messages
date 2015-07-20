import java.util.ArrayList;
import java.util.*;


public class RunSimulation {

	double rateOfArrivals = 0;
	double smtP = 0;
	double smtS = 0;
	double smtA = 0;
	int departures = 0;
	int departedCustomers = 0;
	ArrayList<Event> events = new ArrayList<Event>();
	Random rand;
	double nextEventTime = 0;
	int customerTracker = 0;
	double E1Next = 65000;
	double E2Next = 65000;
	double E3Next = 65000;
	double E4Next = 65000;
	
	Queue<Event> pQueue = new LinkedList<Event>();
	Queue<Event> sQueue = new LinkedList<Event>();
	Queue<Event> aQueue = new LinkedList<Event>();
	
	boolean pIdle = true;
	boolean sIdle = true;
	boolean aIdle = true;
	
	ArrayList<Double> CustomerTimes = new ArrayList<Double>();
	
	public RunSimulation(double rateOfArrivals,double smtP, double smtS, double smtA, int departures, Random rand)
	{
		this.rateOfArrivals = rateOfArrivals;
		this.smtP = smtP;
		this.smtS = smtS;
		this.smtA = smtA;
		this.departures = departures;
		this.rand = rand;
	}
	
	public ArrayList<Double> Run(double masterClock) {
		while(departedCustomers < departures)
		{
			if(customerTracker == 0)
			{
				ProcessFirstEvent(masterClock);
				masterClock = Math.min(E1Next,E2Next);
			} else
			{
				ArrayList<Event> eventsToProcess = GetNextEvent(masterClock);
				for (Event e : eventsToProcess) {
					//TODO convert to Switch later
				    if(e.EventType == "E1")
				    {
				    	if(E1Next == masterClock) E1Next = 65000;
				    	HandleEventE1(masterClock,e);	
				    }
					if(e.EventType == "E2")
				    {
				    	if(E2Next == masterClock) E2Next = 65000;
				    	HandleEventE2(masterClock,e);
				    }
					if(e.EventType == "E3")
				    {
				    	if(E3Next == masterClock) E3Next = 65000;
				    	HandleEventE3(masterClock,e);
				    }
					
					if(e.EventType == "E4")
				    {
				    	if(E4Next == masterClock) E4Next = 65000;
				    	HandleEventE4(masterClock,e);
				    }
				}
			}
			
			masterClock = Math.min(Math.min(E1Next,E2Next),Math.min(E3Next,E4Next));
			//System.out.println("masterClock" + masterClock);
		}
		return CustomerTimes;
	}

	private ArrayList<Event> GetNextEvent(double masterClock) {
		ArrayList<Event> eventsToOccur = new ArrayList<Event>();
		for (Event e : events) {
		    if(e != null && e.TimeToOccur == masterClock){
		    	eventsToOccur.add(e);
		    }
		}
		
		return eventsToOccur;
	}

	private void ProcessFirstEvent(double masterClock) {
		Customer firstCustomer = new Customer();
		firstCustomer.newArrival = true;
		firstCustomer.ArrivalTime = masterClock;
		customerTracker++;
		E1Next = GenerateNextArrivalEvent(masterClock);
		E2Next = GenerateServiceDoneEvent(masterClock,"P",firstCustomer,smtP,"E2");
	}

	private double GenerateNextArrivalEvent(double masterClock) {
		double nextArrivalTime = masterClock + GenerateArrivalTime(rateOfArrivals);
		
		Event nextEvent = new Event();
		nextEvent.TimeToOccur = nextArrivalTime;
		nextEvent.server = "P";
		nextEvent.EventType = "E1";
		nextEvent.customer = new Customer();
		customerTracker++;
		nextEvent.customer.ArrivalTime = nextArrivalTime;
		nextEvent.customer.newArrival = true;
		events.add(nextEvent);
		
		return nextArrivalTime;
	}

	private double GenerateServiceDoneEvent(double masterClock,String serverType,Customer customer,double ServiceRate,String eventType) {
		double serviceDoneTime = masterClock + GenerateTime(ServiceRate);
		
		Event nextEvent = new Event();
		nextEvent.TimeToOccur = serviceDoneTime;
		nextEvent.server = serverType;
		nextEvent.EventType = eventType;
		nextEvent.customer = customer;
		events.add(nextEvent);
		
		return serviceDoneTime;
	}
	
	
	private void HandleEventE1(double masterClock,Event E1)
	{
		if(pIdle)
		{
				double nextArrivalTime = GenerateNextArrivalEvent(masterClock);
				double serviceDoneTime = GenerateServiceDoneEvent(masterClock,"P",E1.customer,smtP,"E2");
				
				E1Next = Math.min(nextArrivalTime,E1Next);
				E2Next = Math.min(serviceDoneTime,E2Next);
				pIdle = false;
		}
		else{
			pQueue.add(E1);
			double nextArrivalTime = GenerateNextArrivalEvent(masterClock);
			E1Next = Math.min(nextArrivalTime,E1Next);
		}
	}
	
	private void HandleEventE2(double masterClock,Event E2)
	{
		if(!E2.customer.newArrival)
		{
			//TODO Handle Departures
			departedCustomers++;
			E2.customer.DepartureTime = masterClock;
			CustomerTimes.add((E2.customer.DepartureTime - E2.customer.ArrivalTime)) ;
			
		} else if(sIdle)
		{
				E3Next = GenerateServiceDoneEvent(masterClock,"S",E2.customer,smtS,"E3");
				sIdle = false;
			
		} else
		{
			sQueue.add(E2);
		}
		
		if(pQueue.size() > 0)
		{
			Event nextEvent = pQueue.remove();
			E2Next = GenerateServiceDoneEvent(masterClock,"P",nextEvent.customer,smtP,"E2");
			//if(nextEvent.customer.newArrival) E1Next = Math.min(E1Next, GenerateNextArrivalEvent(masterClock));
			pIdle = false;
		}
		else
		{
			pIdle = true;
		}
	}
	
	private void HandleEventE3(double masterClock,Event E3)
	{		
		if(!E3.customer.newArrival)
		{
			
			if(pIdle)
			{
					E2Next = GenerateServiceDoneEvent(masterClock,"P",E3.customer,smtP,"E2");
				
			} 
			else
			{
				pQueue.add(E3);
			}
			
			pIdle = false;
			
		} else if(aIdle)
		{
			E4Next = GenerateServiceDoneEvent(masterClock,"AS",E3.customer,smtA,"E4");
			aIdle = false;
			
		} else
		{
			aQueue.add(E3);
		}
		
		if(sQueue.size() > 0)
		{
			E3Next = GenerateServiceDoneEvent(masterClock,"S",sQueue.remove().customer,smtS,"E3");
			sIdle = false;
		} 
		else
		{
			sIdle = true;
		}
		
	}
	
	private void HandleEventE4(double masterClock,Event E4)
	{
		E4.customer.newArrival = false;
		if(sIdle)
		{
			E3Next = GenerateServiceDoneEvent(masterClock,"S",E4.customer,smtS,"E3");
			sIdle = false;
			
		} else
		{
			sQueue.add(E4);
		}
		if(aQueue.size() > 0)
		{
			E4Next = GenerateServiceDoneEvent(masterClock,"AS",aQueue.remove().customer,smtA,"E4");
			aIdle = false;
		}
		else
		{
			aIdle = true;
		}
	}

	private double GenerateTime(double serviceMean) {
		double randomValue = rand.nextDouble();
		//System.out.println("Random Value"+ randomValue);
		return -(serviceMean)*Math.log(randomValue);
	}
	
	private double GenerateArrivalTime(double ArrivalRate) {
		double randomValue = rand.nextDouble();
		//System.out.println("Random Value"+ randomValue);
		return -(1/ArrivalRate)*Math.log(randomValue);
	}

}
