package com.sunnyd.models;

import java.util.HashMap;

public class DBManager {
	public HashMap<Object, Object> find(int id){
		HashMap<Object, Object> Bean = new HashMap<Object, Object>();
		switch (id) {
			case 1:
				Bean.put("firstName", "bitch");
				Bean.put("lastName", "please");
				break;
			case 2:
				Bean.put("firstName", "Mike");
				Bean.put("lastName", "Pham");
				break;
			case 3:
				return null;
			default:
				break;
		}

		return Bean;
	}

}
