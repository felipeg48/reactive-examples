package io.pivotal.reactive;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Movie {

	private @Id String id;
	private String title;
	private String genre;
	
	public Movie(String title, String genre) {
		this.title = title;
		this.genre = genre;
	}
	
	public String getGenre() {
		return genre;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", genre=" + genre +  ", id=" + id + "]";
	}

}
