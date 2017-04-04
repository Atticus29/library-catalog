import org.sql2o.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

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

  public void update(String title, String author) {
    try (Connection con = DB.sql2o.open()) {
      String sql = "UPDATE books SET title = :title, author = :author WHERE id = :id";
      con.createQuery(sql)
      .addParameter("title", title)
      .addParameter("author", author)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }

  public void delete() {
    String sql = "DELETE FROM books WHERE id = :id;";
    try (Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }

  public void checkout(Patron patron){
    String sqlCommand = "INSERT INTO checkouts (bookid, patronid, checkout_date, due_date, renew_count) VALUES (:bookid, :patronid, now(), now() + INTERVAL '14 days', 0);";
    try(Connection con = DB.sql2o.open()){
      con.createQuery(sqlCommand)
        .addParameter("bookid", this.id)
        .addParameter("patronid", patron.getId())
        .executeUpdate();
    }
  }

  public List<Patron> getPatronRecords() {
    String sql = "SELECT patrons.* FROM books
      JOIN checkouts ON (books.id = checkouts.bookid)
      JOIN patrons ON (checkouts.patronid = patrons.id)
      WHERE books.id = :id;";
    try(Connection con = DB.sql2o.open()) {
      List<Patron> results = con.createQuery(sql)
      .addParameter("id", this.id)
      .executeAndFetch(Patron.class);
    return results;
    }
  }

public boolean isCheckedOut(){
  String sqlCommand = "SELECT return_date FROM checkouts WHERE bookid=:bookid ORDER BY checkout_date;";
  // test that this return most recent book entry
  try(Connection con=DB.sql2o.open()){
    Timestamp returnDate = con.createQuery(sqlCommand)
      .addParameter("bookid", this.id)
      .executeAndFetchFirst(Timestamp.class);
    return false;
    // rightNow = new Timestamp(new Date().getTime());
    // return returnDate.after(rightNow);
  }catch(){
    return true;
  }
}

}
