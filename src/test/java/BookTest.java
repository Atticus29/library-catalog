import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.Date;

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

  @Test
  public void find_returnsBookWithSameId_secondBook() {
    Book secondBook = new Book ("Book2", "Jane Austin", 1);
    secondBook.save();
    assertEquals(Book.find(secondBook.getId()), secondBook);
  }

  @Test
  public void update_updatesBookName_true() {
    testBook.update("Bookbook", "Bark Twain");
    assertEquals("Bookbook", Book.find(testBook.getId()).getTitle());
  }

  @Test
  public void delete_deletesBook_true() {
    int testBookId = testBook.getId();
    testBook.delete();
    assertEquals(null, Book.find(testBookId));
  }

  @Test
  public void checkout_addsEntryTocheckoutsJoinTable_true(){
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    testBook.checkout(testPatron);
    assertTrue(testBook.isCheckedOut());
    List<Patron> patronsWhoHaveCheckedThisBookOut = testBook.getPatronRecords();
    assertEquals(1, patronsWhoHaveCheckedThisBookOut.size());
  }

  @Test
  public void getPatronRecords_returnsListOfPatrons_true(){
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    testBook.checkout(testPatron);
    List<Patron> patronsWhoHaveCheckedThisBookOut = testBook.getPatronRecords();
    assertTrue(patronsWhoHaveCheckedThisBookOut instanceof List);
    assertTrue(patronsWhoHaveCheckedThisBookOut.get(0) instanceof Patron);
  }

  @Test
  public void isCheckedOut_instantiatesAsFalseAndReturnsTrueAfterCheckout_true(){
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    assertFalse(testBook.isCheckedOut());
    testBook.checkout(testPatron);
    assertTrue(testBook.isCheckedOut());
  }

  @Test
  public void getDueDate_getsATimeStamp_true(){
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    testBook.checkout(testPatron);
    Timestamp due = testBook.getDueDate(testPatron);
    Timestamp rightNow = new Timestamp(new Date().getTime());
    assertEquals(rightNow.getDay(), due.getDay());
  }

  @Test
  public void renew_extendsDueDate() {
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    testBook.checkout(testPatron);
    Timestamp rightNow = new Timestamp(new Date().getTime());
    Calendar cal = Calendar.getInstance();
    cal.setTime(rightNow);
    cal.add(Calendar.DATE, 14);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(testBook.getDueDate(testPatron));
    assertEquals(cal.getTime().getDate(), cal2.getTime().getDate());
  }

  @Test
  public void isOverdue_checksIfBookOverdue_true() {
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    testBook.checkout(testPatron);
    // http://stackoverflow.com/questions/22856931/converting-java-date-to-sql-timestamp
    Timestamp rightNow = new Timestamp(new Date().getTime());
    Calendar cal = Calendar.getInstance();
    cal.setTime(rightNow);
    cal.set(Calendar.MILLISECOND, 0);
    Timestamp currentTime = new Timestamp(cal.getTimeInMillis());
    assertFalse(testBook.isOverdue(currentTime, testPatron));
    cal.add(Calendar.DATE, 15);
    cal.set(Calendar.MILLISECOND, 0);
    currentTime = new Timestamp(cal.getTimeInMillis());
    assertTrue(testBook.isOverdue(currentTime, testPatron));
  }

  @Test
  public void canBeRenewedAgain_checksWhetherABookCanBeRenewedSeveralTimes(){
    Patron testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
    testBook.checkout(testPatron);
    assertTrue(testBook.canBeRenewedAgain(testPatron));
    testBook.renew(testPatron);
    assertTrue(testBook.canBeRenewedAgain(testPatron));
    testBook.renew(testPatron);
    assertFalse(testBook.canBeRenewedAgain(testPatron));
  }

}
