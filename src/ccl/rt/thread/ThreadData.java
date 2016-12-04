package ccl.rt.thread;

import java.util.HashMap;

public class ThreadData {

	static{
		sharedData = new HashMap<String, SharedData>();
	}
	
	private static HashMap<String, SharedData> sharedData;
	
	public static SharedData byName(String receiver) {
		synchronized (sharedData) {
			return sharedData.get(receiver);
		}
	}
	
	private Thread thread;
	private SharedData shared;

	public ThreadData(Thread t) {
		this.thread = t;
		register();
	}

	private void register() {
		SharedData d = sharedData.get(thread.getName());
		if(d == null){
			d = new SharedData(thread);
			sharedData.put(thread.getName(),d);
		}
		shared = d;
	}
	
	SharedData getShared(){
		synchronized (this) {
			return shared;
		}
	}
	
	public String getName(){
		return thread.getName();
	}
	
	public void waitFor(String name){
		while(byName(name) == null);
	}
	
	public boolean find(String name){
		synchronized (this) {
			return getShared().read(name) != null;
		}
	}
	
	public void sleep(long millis) throws InterruptedException{
		Thread.sleep(millis);
	}

}
