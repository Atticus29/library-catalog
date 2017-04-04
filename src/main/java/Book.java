import org.sql2o.*;
import java.util.ArrayList;
import java.util.List;

public class Book {
  private int id;
  private String title;
  private String author;
  private int patronId;

  public Book(String title, String author, int patronId) {
    this.title = title;
    this.author = author;
    this.patronId = patronId;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public int getPatronId() {
    return patronId;
  }

  public int getId(){
    return id;
  }

  @Override
  public boolean equals(Object otherBook) {
    if(!(otherBook instanceof Book)){
      return false;
    } else {
      Book newBook = (Book) otherBook;
      return this.getId() == newBook.getId()
        && this.getTitle().equals(newBook.getTitle())
        && this.getAuthor().equals(newBook.getAuthor())
        && this.getPatronId() == newBook.getPatronId();
    }
  }

  public void save() {
    String sql = "INSERT INTO books (title, author, patronId) VALUES (:title, :author, :patronId)";
    try(Connection con = DB.sql2o.open()) {
      this.id = (int) con.createQuery(sql, true)
        .addParameter("title", this.title)
        .addParameter("author", this.author)
        .addParameter("patronId", this.patronId)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Book> all() {
    String sql = "SELECT * FROM books;";
    try(Connection con = DB.sql2o.open()) {
      List<Book> books = con.createQuery(sql)
        .executeAndFetch(Book.class);
      return books;
    }
  }

  public static Book find(int id){
    String sqlCommand = "SELECT * FROM books WHERE id=:id;";
    try(Connection con=DB.sql2o.open()){
      Book result = con.createQuery(sqlCommand)
        .addParameter("id", id)
        .executeAndFetchFirst(Book.class);
      return result;
    }
  }

}
