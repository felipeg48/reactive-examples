package io.pivotal.reactive;

import java.util.Date;

public class NameEvent {
	
	private String name;
	private Date date;
	
	public NameEvent() {
		super();
	}

	public NameEvent(String name, Date date) {
		this.name = name;
		this.date = date;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "NameEvent [name=" + name + ", date=" + date + "]";
	}


}