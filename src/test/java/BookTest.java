import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;

public class BookTest {

  private Book testBook;

  @Before
  public void setUp() {
    testBook = new Book("Booky McBookface", "Mark Twain", 1);
    testBook.save();
  }

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void book_instantiatesCorrectly_true() {
    assertTrue(testBook instanceof Book);
  }

  @Test
  public void getters_returnBookVariableValues() {
    assertEquals("Booky McBookface", testBook.getTitle());
    assertEquals("Mark Twain", testBook.getAuthor());
    assertEquals(1, testBook.getPatronId());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsSame() {
    // Book secondBook = new Book("Booky McBookface", "Mark Twain", 1);
    Book retrievedBook = Book.find(testBook.getId());
    assertTrue(testBook.equals(retrievedBook));
  }

  @Test
  public void save_savesIntoDatabase() {
    assertTrue(Book.all().get(0).equals(testBook));
  }

  @Test
  public void save_assignsIdToBook() {
    Book savedBook = Book.all().get(0);
    assertEquals(testBook.getId(), savedBook.getId());
  }

  @Test
  public void all_returnsAllInstancesOfBook_true() {
    Book secondBook = new Book ("Book2", "Jane Austin", 1);
    secondBook.save();
    assertTrue(Book.all().get(0).equals(testBook));
    assertTrue(Book.all().get(1).equals(secondBook));
  }

}
