import org.sql2o.*;
import java.util.List;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;


public class Patron {
  private String name;
  private String address;
  private String phone;
  private String email;
  private int id;


  public Patron(String name, String address, String phone, String email) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.email = email;
  }

  public String getName(){
    return this.name;
  }

  public String getAddress(){
    return this.address;
  }

  public String getPhone(){
    return this.phone;
  }

  public String getEmail(){
    return this.email;
  }

  public int getId(){
    return this.id;
  }

  public void setId(int id){
    this.id = id;
  }

  @Override
  public boolean equals(Object otherPatron) {
    if(!(otherPatron instanceof Patron)) {
      return false;
    } else {
      Patron newPatron = (Patron) otherPatron;
      return this.getName().equals(newPatron.getName())
      && this.getAddress().equals(newPatron.getAddress()) &&
      this.getPhone().equals(newPatron.getPhone()) &&
      this.getEmail().equals(newPatron.getEmail()) &&
      this.getId() == newPatron.getId();
    }
  }

  public void save(){
    String sqlCommand = "INSERT INTO patrons (name, address, phone, email) VALUES (:name, :address, :phone, :email)";
    try(Connection con = DB.sql2o.open()){
      this.id = (int) con.createQuery(sqlCommand, true)
      .addParameter("name", this.name)
      .addParameter("address", this.address)
      .addParameter("phone", this.phone)
      .addParameter("email", this.email)
      .executeUpdate()
      .getKey();
    }
  }

  public static List<Patron> all(){
    String sqlCommand = "SELECT * FROM patrons;";
    try(Connection con = DB.sql2o.open()){
      List<Patron> results = con.createQuery(sqlCommand)
      .executeAndFetch(Patron.class);
      return results;
    }
  }

  public static Patron find(int id){
    String sqlCommand = "SELECT * FROM patrons WHERE id=:id;";
    try(Connection con = DB.sql2o.open()){
      Patron result = con.createQuery(sqlCommand)
      .addParameter("id", id)
      .executeAndFetchFirst(Patron.class);
      return result;
    }
  }

  public void update(String name, String address, String phone, String email) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE patrons SET name = :name, address=:address, phone=:phone, email=:email WHERE id = :id";
      con.createQuery(sql)
      .addParameter("name", name)
      .addParameter("address", address)
      .addParameter("phone", phone)
      .addParameter("email", email)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }


  public void delete(){
    String sqlCommand = "DELETE FROM patrons WHERE id=:id;";
    try(Connection con=DB.sql2o.open()){
      con.createQuery(sqlCommand)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }

}
