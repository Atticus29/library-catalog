import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class PatronTest {
  private Patron testPatron;

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp() {
    testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    testPatron.save();
  }

  @Test
  public void Patron_instantiatesCorrectly_true() {
    assertTrue(testPatron instanceof Patron);
  }

  @Test
  public void Patron_instantiatesWithNameAddressPhoneEmail_String() {
    assertEquals("Ryan Murphy", testPatron.getName());
    assertEquals("123 Maple Lane Portland, OR 97203", testPatron.getAddress());
    assertEquals("5046179123", testPatron.getPhone());
    assertEquals("ryan.murphy@gmail.com", testPatron.getEmail());
  }

  @Test
  public void equals_returnsTrueIfNameAddressPhoneEmailAreSame_true() {
    // Patron anotherPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    Patron retrievedPatron = Patron.find(testPatron.getId());
    assertTrue(testPatron.equals(retrievedPatron));
  }

  @Test
  public void save_assignsIdToPatron_true(){
    Patron savedPatron = Patron.all().get(0);
    assertEquals(savedPatron.getId(), testPatron.getId());
  }

  @Test
  public void all_returnsAllInstancesOfPatron_true(){
    Patron secondPatron = new Patron ("Jahan Miller", "133 Maple Lane Portland, OR 97203", "6095779898", "jahan@gmail.com");
    secondPatron.save();
    assertTrue(Patron.all().get(0).equals(testPatron));
    assertTrue(Patron.all().get(1).equals(secondPatron));
  }

  @Test
  public void find_returnsPatronWithSameId_secondPatron(){
    Patron secondPatron = new Patron ("Jahan Miller", "133 Maple Lane Portland, OR 97203", "6095779898", "jahan@gmail.com");
    secondPatron.save();
    assertEquals(Patron.find(secondPatron.getId()), secondPatron);
  }

  @Test
  public void update_updatesPatronName_true() {
    testPatron.update("Jahan Miller", "133 Maple Lane Portland, OR 97203", "6095779898", "jahan@hotmail.com");
    assertEquals("Jahan Miller", Patron.find(testPatron.getId()).getName());
  }

  @Test
  public void delete_deletesPatron_true() {
    int myPatronId = testPatron.getId();
    testPatron.delete();
    assertEquals(null, Patron.find(myPatronId));
  }

}
