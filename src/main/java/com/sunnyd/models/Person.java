package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.ModelInterface;
import com.sunnyd.annotations.*;

import java.util.HashMap;

public class Person extends Base implements ModelInterface {
	@tableAttr
	private String firstName;
	
	@tableAttr
	private String lastName;
	
	@tableAttr
	private Integer id;	
	
	@tableAttr
	private String status;
	
	public static final String tableName = "persons";

	public Person(HashMap<Object, Object> HM) {
		super(HM);
	}

	public static void main(String[] args) {
		
		Person a = Person.find(1);
		
		System.out.println(a.getId());
		System.out.println(a.getFirstName());
		System.out.println(a.getLastName());
		
//		System.out.println(a.Destroy());
		
	}

	
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public String getTableName() {
		return tableName;
	}
	
	
	@Method(attribute = false)
	public String getFirstName() {
		return firstName;
	}

	@Method(attribute = false)
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Method(attribute = false)
	public String getLastName() {
		return lastName;
	}

	@Method(attribute = false)
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
