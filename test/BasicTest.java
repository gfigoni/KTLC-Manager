
import models.Player;
import org.junit.*;
import play.Logger;
import play.libs.I18N;
import play.test.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void createAndRetreivePlayer() {
        new Player("gehef", "gehef").save();

        Player gehef = Player.find("byLogin", "gehef").first();
        assertNotNull(gehef);
        assertEquals("gehef", gehef.login);
        assertEquals("gehef", gehef.name);
    }

    @Test
    public void checkPlayers() {
        Fixtures.loadModels("players.yml");
        Player evolm = Player.find("byLogin", "evolm").first();
        assertNotNull(evolm);
        assertEquals("evolm", evolm.login);
        assertEquals("evolm", evolm.name);
    }

    @Test
    public void testI18N() {
        System.out.println("date format:" + I18N.getDateFormat());
        Logger.info("date format: %s", I18N.getDateFormat());

    }
}
