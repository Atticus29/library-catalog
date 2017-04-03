import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class PatronTest {
  private Patron testPatron;

  @Before
  public void setUp() {
    testPatron = new Patron("Ryan Murphy", "123 Maple Lane Portland, OR 97203", "5046179123", "ryan.murphy@gmail.com");
    // testPatron.save();
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
  public void equals_returnsTrueIfNameAndPersonIdAreSame_true() {
    Patron anotherPatron = new Patron("Bubbles", 1);
    assertTrue(testPatron.equals(anotherPatron));
  }

  @Test
  public void save_assignsIdToPatron_true(){
    Patron savedPatron = Patron.all().get(0);
    assertEquals(savedPatron.getId(), testPatron.getId());
  }

  @Test
  public void all_returnsAllInstancesOfPatron_true(){
    Patron secondPatron = new Patron ("Spud", 1);
    secondPatron.save();
    assertTrue(Patron.all().get(0).equals(testPatron));
    assertTrue(Patron.all().get(1).equals(secondPatron));
  }

  @Test
  public void find_returnsPatronWithSameId_secondPatron(){
    Patron secondPatron = new Patron("Spud", 3);
    secondPatron.save();
    assertEquals(Patron.find(secondPatron.getId()), secondPatron);
  }



}
