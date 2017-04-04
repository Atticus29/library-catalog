import org.sql2o.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class Book {
  private int id;
  private String title;
  private String author;
  private int patronId;
  private boolean checkedOut;
  public static final int MAX_RENEW_COUNT = 2;

  public Book(String title, String author, int patronId) {
    this.title = title;
    this.author = author;
    this.patronId = patronId;
    checkedOut = false;
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


  public Timestamp getDueDate(Patron patron) {
    String sqlCommand = "SELECT due_date FROM checkouts WHERE patronid = :patronid AND bookid = :bookid;";
    try(Connection con=DB.sql2o.open()){
      Timestamp dueDate = con.createQuery(sqlCommand)
      .addParameter("patronid", patron.getId())
      .addParameter("bookid", this.id)
      .executeAndFetchFirst(Timestamp.class);
      return dueDate;
    }
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
    String sqlCommand = "INSERT INTO checkouts (bookid, patronid, checkout_date, due_date, renew_count, checked_out) VALUES (:bookid, :patronid, now(), now() + INTERVAL '14 days', 0, :checked_out);";
    try(Connection con = DB.sql2o.open()){
      con.createQuery(sqlCommand)
      .addParameter("bookid", this.id)
      .addParameter("patronid", patron.getId())
      .addParameter("checked_out", this.isCheckedOut())
      .executeUpdate();
    }
    this.checkedOut = true;
  }

  public List<Patron> getPatronRecords() {
    String sql = "SELECT patrons.* FROM books JOIN checkouts ON (books.id = checkouts.bookid) JOIN patrons ON (checkouts.patronid = patrons.id) WHERE books.id = :id;" ;
    try(Connection con = DB.sql2o.open()) {
      List<Patron> results = con.createQuery(sql)
      .addParameter("id", this.id)
      .executeAndFetch(Patron.class);
      return results;
    }
  }

  public boolean isCheckedOut(){
    return this.checkedOut;
    // if(this.getPatronRecords().size()==0){
    //   return false;
    // } else{
    //   String sqlCommand = "SELECT return_date FROM checkouts WHERE bookid=:bookid ORDER BY checkout_date;";
    //   // test that this return most recent book entry
    //   try(Connection con=DB.sql2o.open()){
    //     Timestamp returnDate = con.createQuery(sqlCommand)
    //     .addParameter("bookid", this.id)
    //     .executeAndFetchFirst(Timestamp.class);
    //     return false;
    //   }catch(RuntimeException e){
    //     System.out.println(e.getClass().getName());
    //     return true;
    //   }
    // }
  }

  public boolean canBeRenewedAgain(Patron patron){
    String sqlCommand = "SELECT renew_count FROM checkouts WHERE bookid=:bookid AND patronid=:patronid;";
    try(Connection con = DB.sql2o.open()){
      int renewsSoFar = con.createQuery(sqlCommand)
        .addParameter("bookid", this.id)
        .addParameter("patronid", patron.getId())
        .executeAndFetchFirst(Integer.class);
      return renewsSoFar < this.MAX_RENEW_COUNT;
    }
  }

  public void renew(Patron patron) {
    if(this.canBeRenewedAgain(patron)){
      String sql = "UPDATE checkouts  SET renew_count = renew_count + 1, due_date = due_date + INTERVAL '14 days' WHERE bookid = :bookid AND patronid=:patronid";
      try(Connection con = DB.sql2o.open()) {
        con.createQuery(sql)
        .addParameter("bookid", this.id)
        .addParameter("patronid", patron.getId())
        .executeUpdate();
      }
    }
  }

  public boolean isOverdue(Timestamp currentTime, Patron patron){
    String sqlCommand = "SELECT due_date FROM checkouts WHERE bookid=:bookid AND patronid=:patronid;";
    try(Connection con=DB.sql2o.open()){
      Timestamp due = con.createQuery(sqlCommand)
      .addParameter("bookid", this.id)
      .addParameter("patronid", patron.getId())
      .executeAndFetchFirst(Timestamp.class);
      return currentTime.after(due);
    }
  }

}

// rightNow = new Timestamp(new Date().getTime());
// return returnDate.after(rightNow);
