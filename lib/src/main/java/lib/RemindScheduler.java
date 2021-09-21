package lib;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.requests.restaction.MessageAction;

/*
 * For the current implementation of the class, I am going to make it rather simple.
 * Every 30 seconds or so, the thread will wake up and check if any messages need to be sent.
 * 
 * Of course, we will be careful regarding thread safety, especially related to the list of
 * scheduled tasks. 
 * 
 * We will store MessageActions and simply queue them when their time comes. We will ticket them
 * such that they can be modified as necessary.
 */
public class RemindScheduler implements Runnable {
	private long ticketSet;
	private RemindScheduler instance = null;
	private RemindScheduler() {
		this.ticketSet = System.currentTimeMillis();
	}
	
	
	
	public RemindScheduler getInstance() {
		if (instance == null)
			instance = new RemindScheduler();
		return instance;
	}
	
	private long ticketer = 0;
	private List<SimpleEvent> events = new ArrayList<SimpleEvent>();
	
	//Schedule an event:
	public long scheduleEvent(MessageAction action, long timeToSend) {
		SimpleEvent e = new SimpleEvent(action, timeToSend, ticketer);
		synchronized (events) {
			events.add(e);
		}
		return ticketer++;
	}
	
	public void setMessageAction(MessageAction newAction, long ticket) {
		synchronized (events) {
			for (SimpleEvent event : events) {
				if (event.getTicket() == ticket) {
					event.replaceMessageAction(newAction);
					break;
				}
			}
		}
	}
	
	public long getTicketSet() {
		return ticketSet;
	}
	
	public long getTime(long ticket) {
		long time = -1;
		synchronized (events) {
			for (SimpleEvent event : events) {
				if (event.getTicket() == ticket) {
					time = event.getTime();
					break;
				}
			}
		}
		return time;
	}
	
	public void setTime(long ticket, long time) {
		synchronized (events) {
			for (SimpleEvent event : events) {
				if (event.getTicket() == ticket) {
					event.setTime(time);
					break;
				}
			}
		}
	}
	
	//Check all events once per thirty seconds.
	public void run() {
		while (true) {
			try {
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e) {
				//Do nothing: if interrupted, simply check sooner than expected.
				//Just don't crash the thread, please!
			}
			synchronized (events) {
				long curTime = System.currentTimeMillis();
				for (SimpleEvent event : events)
					event.sendIfBefore(curTime);
			}
		}
	}
	
	private class SimpleEvent {
		MessageAction a;
		long time;
		public SimpleEvent(MessageAction a, long tts, long ticket) {
			this.a = a;
			this.time = tts;
		}
		
		public boolean sendIfBefore(long curTime) {
			if (curTime > time) {
				a.queue();
				return true;
			}
			return false;
		}
		
		public long getTime() {
			return time;
		}
		
		public void setTime(long time) {
			this.time = time;
		}
		
		public void replaceMessageAction(MessageAction newAction) {
			this.a = newAction;
		}
		
		public long getTicket() {
			return 0;
		}
	}
}
