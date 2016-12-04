package ccl.rt.thread;

import java.util.HashMap;

import ccl.rt.Value;

public class SharedData {

	private Thread reader;
	private HashMap<String, Value> values;
	
	public SharedData(Thread reader) {
		this.reader = reader;
		this.values = new HashMap<String,Value>();
	}
	
	public void write(String receiver, String key, Value value){
		synchronized (this) {
			SharedData to = ThreadData.byName(receiver);
			to.write0(key, value);
		}
	}
	private void write0(String key, Value value) {
		synchronized (this) {
			values.put(key,value);
		}
	}

	public Value read(String key){
		synchronized (this) {
			if(reader == Thread.currentThread()){
				return values.get(key);
			}
			return null;
		}
	}

}
