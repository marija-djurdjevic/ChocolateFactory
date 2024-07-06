package beans;

import java.util.Objects;

public class Comment {
	private int id;
	private int customerId;
	private int factoryId;
	private String comment;
	private int rating;
	
	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Comment(int id, int customerId, int factoryId, String comment, int rating) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.factoryId = factoryId;
		this.comment = comment;
		this.rating = rating;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	@Override
	public int hashCode() {
		return Objects.hash(comment, customerId, factoryId, id, rating);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return Objects.equals(comment, other.comment) && customerId == other.customerId && factoryId == other.factoryId
				&& id == other.id && rating == other.rating;
	}
	@Override
	public String toString() {
		return "Comment [id=" + id + ", customerId=" + customerId + ", factoryId=" + factoryId + ", comment=" + comment
				+ ", rating=" + rating + "]";
	}
	
	
}
