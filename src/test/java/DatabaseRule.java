import org.junit.rules.ExternalResource;
import org.sql2o.*;

public class DatabaseRule extends ExternalResource {
  @Override
  protected void before() {
    DB.sql2o = new Sql2o("jdbc:postresql://localhost:5432/library-catalog-test", null, null);
  }

  @Override
  protected void after() {
    try(Connection con = DB.sql2o.open()) {
      String deleteBooksQuery = "DELETE FROM books *;";
      // String deletePatronsQuery = "DELETE FROM patrons *;";
      con.createQuery(deleteBooksQuery).executeUpdate();
      // con.createQuery(deletePatronsQuery).executeUpdate();
    }
  }
}
