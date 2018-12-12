package io.pivotal.reactive;

import java.util.Date;

public class MovieEvent {
	
	private Movie movie;
	private Date date;
	
	public MovieEvent() {
		super();
	}

	public MovieEvent(Movie movie, Date date) {
		this.movie = movie;
		this.date = date;
	}

	@Override
	public String toString() {
		return "MovieEvent [movie=" + movie.getTitle() + ", date=" + date + "]";
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}