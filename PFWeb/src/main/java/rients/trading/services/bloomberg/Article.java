package rients.trading.services.bloomberg;

public class Article {

private String title;
private int length;
private String lastUpdated;
private String author;
/**
* @return the title
*/
public String getTitle() {
return title;
}
/**
* @param title the title to set
*/
public void setTitle(String title) {
this.title = title;
}
/**
* @return the length
*/
public int getLength() {
return length;
}
/**
* @param length the length to set
*/
public void setLength(int length) {
this.length = length;
}
/**
* @return the lastUpdated
*/
public String getLastUpdated() {
return lastUpdated;
}
/**
* @param lastUpdated the lastUpdated to set
*/
public void setLastUpdated(String lastUpdated) {
this.lastUpdated = lastUpdated;
}
/**
* @return the author
*/
public String getAuthor() {
return author;
}
/**
* @param author the author to set
*/
public void setAuthor(String author) {
this.author = author;
}
}