
import models.Login;
import models.Player;
import org.junit.*;
import play.db.jpa.JPA;
import play.test.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void createAndRetreivePlayer() {
        Player p = new Player("gehef").save();
        Login l = new Login("g", p).save();

        JPA.em().clear();

        Player gehef = Player.findByLogin("g");
        assertNotNull(gehef);
        assertEquals("gehef", gehef.name);
        assertEquals(1, gehef.logins.size());
    }

    @Test
    public void addSeveralLogins() {
        Player p = new Player("gehef").save();
        p.addLogin("g");
        p.addLogin("f");

        JPA.em().clear();

        Player gehef = Player.findByLogin("f");
        assertNotNull(gehef);
        assertEquals("gehef", gehef.name);
        assertEquals(2, gehef.logins.size());
    }

    @Test
    public void playerLoginCascadeConstraint() {
        Player p = new Player("gehef").save();
        p.addLogin("g");
        p.addLogin("f");

        JPA.em().clear();

        assertEquals(2, Login.findAll().size());

        Player gehef = Player.findByLogin("f");
        gehef.delete();

        assertEquals(0, Login.findAll().size());
    }
    
}
