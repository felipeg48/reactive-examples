package io.pivotal.reactive;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Movie {
	
	private String title;
	
	@Id
	private String id;
	
	public Movie() {
		super();
	}

	public Movie(String title, String id) {
		super();
		this.title = title;
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", id=" + id + "]";
	}
}