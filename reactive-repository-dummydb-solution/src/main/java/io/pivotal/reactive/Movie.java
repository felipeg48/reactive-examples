package io.pivotal.reactive;

public class Movie {

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
		return "Movie [title=" + title + ", genre=" + genre;
	}

}
