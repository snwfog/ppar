package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.annotations.Method;

import java.util.HashMap;

public class Person extends Base {
	private String firstName, lastName, status;

	public Person(HashMap<Object, Object> HM) {
		super(HM);
	}

	public static void main(String[] args) {
		Person a = Person.find(1);
		System.out.println(a.getFirstName());
		System.out.println(a.getLastName());
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
