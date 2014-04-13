package org.mauro.automation.microserver.sessions;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class SessionData {
	private boolean isNew = true;
	Map<String, String> values = new HashMap<>();
	
	public SessionData(boolean isNew) {
		this.isNew = isNew;
		values.put("CreationDate", new Date().getTime()+"");
	}

	public SessionData(boolean isNew, Map<String, String> map) {
		this.isNew = isNew;
		if (map!=null){
			values = map;
		}
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public String setValue(String id, String value){
		isNew = true;
		return values.put(id, value);
	}

	public String getValue(String id){
		return values.get(id);
	}
	
	public Map<String, String> getValues(){
		return values;
	}
	
}
