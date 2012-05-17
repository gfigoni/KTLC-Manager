package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class KTLCEdition extends Model {

    @Required
    public Integer number;
    @Required
    public Date date;
    @OneToMany(mappedBy = "ktlc", cascade = CascadeType.ALL)
    public List<KTLCRace> races = new ArrayList<KTLCRace>();
    @OneToMany(mappedBy = "ktlc", cascade = CascadeType.ALL)
    public List<KTLCResult> results;

    public KTLCEdition(Integer num, Date date) {
        super();
        this.number = num;
        this.date = date;
    }

    @Override
    public String toString() {
        return number.toString();
    }

    public KTLCRace addRace(TMMap map) {
        KTLCRace race = new KTLCRace(this, map).save();
        this.races.add(race);
        this.save();
        return race;
    }

    public static KTLCEdition createKTLCEdition(Integer num, Date date, Reader reader) throws UnknownLoginException, IOException {
        KTLCEdition ktlc = new KTLCEdition(num, date).save();
        Set<Login> logins = new HashSet<Login>(); // liste des joueurs de l'édition
        Set<KTLCRace> races = new HashSet<KTLCRace>(); // liste des courses de l'édition

        BufferedReader br = new BufferedReader(reader);
        String line;
        KTLCRace race = null; // current race

        // On ne peut pas se fier à la première donnée de la log KTLC pour avoir le rang,
        // il faut le déduire de la position du joueur dans la liste
        int index = 0;
        while ((line = br.readLine()) != null) {
            Matcher m = KTLCUtils.FAST_LOG_PATTERN_RACE.matcher(line);
            if (m.matches()) {
                // début de log pour une course

                // map
                String mapId = m.group(3);

                // si la map n'existe pas, elle est créée
                TMMap map = TMMap.findById(mapId);
                if (map == null) {
                    String loginName = m.group(4);
                    String mapName = m.group(1);
                    String environment = m.group(2);
                    Login login = Login.findByName(loginName);
                    if (login == null) {
                        throw new UnknownLoginException(loginName);
                    }
                    map = new TMMap(mapId, mapName, login, TMEnvironment.getEnvironment(environment)).save();
                }

                // création de la course
                race = ktlc.addRace(map);
                races.add(race);

                // réinitialisation de l'index pour le début de log d'une nouvelle course
                index = 0;
            } else {
                m = KTLCUtils.FAST_LOG_PATTERN_PLACE.matcher(line);
                if (m.matches()) {
                    // détail de log pour une course
                    index++;

                    int rank = index;
                    int score = KTLCUtils.getRankPoints(rank);
                    int round = Integer.parseInt(m.group(2));
                    String loginName = m.group(3);

                    Login login = Login.findByName(loginName);
                    if (login == null) {
                        throw new UnknownLoginException(loginName);
                    }
                    logins.add(login);

                    new KTLCRaceResult(race, login, rank, score, round).save();
                }
            }
        }

        // Consolidation des résultats de la KTLC
        List<KTLCResult> results = new ArrayList<KTLCResult>();
        for (Login login : logins) {
            KTLCResult result = new KTLCResult(ktlc, login);

            int score = 0;
            double rank = 0d;
            int nbRace = 0;

            for (KTLCRace r : races) {
                KTLCRaceResult pr = r.findResult(login);
                if (pr != null) {
                    score += pr.score;
                    rank += pr.rank;
                    nbRace++;
                }
            }
            result.score = score;
            result.rankAvg = rank / nbRace;
            result.nbRaces = nbRace;
            results.add(result);
        }

        // tri des resultats
        Collections.sort(results);

        // affectation des rangs
        int nbAtTheSameRank = 1;
        int currentRank = 1;
        KTLCResult previous = null;
        for (KTLCResult current : results) {
            if (previous == null) {
                // 1er joueur
                current.rank = currentRank;
                currentRank++;
            } else {
                // autres joueurs
                if (previous.compareTo(current) == 0) {
                    // cas égalité entre le joueur courant et le précédent
                    current.rank = currentRank - 1;
                    nbAtTheSameRank++;
                } else {
                    // autres cas... la liste étant déjà triée
                    current.rank = currentRank + nbAtTheSameRank - 1;
                    currentRank += nbAtTheSameRank;
                    nbAtTheSameRank = 1;
                }
            }
            previous = current;
            current.save();
        }

        return ktlc;
    }

    /**
     * Méthode permettant de vérifier que tous les logins déclarés dans la log FAST
     * sont identifiés dans l'application.
     * Retourne la liste des logins non déclarés, avec le nom par défaut du joueur à proposer.
     */
    public static Map<String, String> checkLogins(Reader r) throws IOException {
        Map<String, String> logins = new HashMap<String, String>();

        BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            Matcher m = KTLCUtils.FAST_LOG_PATTERN_RACE.matcher(line);
            if (m.matches()) {
                // début de log pour une course
                // auteur de la map
                String loginName = m.group(4);
                Login login = Login.findByName(loginName);
                if (login == null) {
                    if (logins.get(loginName) == null) {
                        // Dans le cas d'un auteur d'une map, aucun nom de joeuur par défaut n'est disponible.
                        // On propose donc le login
                        logins.put(loginName, loginName);
                    }
                }
            } else {
                m = KTLCUtils.FAST_LOG_PATTERN_PLACE.matcher(line);
                if (m.matches()) {
                    // détail de log pour une course
                    // joueur
                    String loginName = m.group(3);
                    String playerName = m.group(4);

                    Login login = Login.findByName(loginName);
                    if (login == null) {
                        logins.put(loginName, playerName);
                    }
                }
            }
        }

        return logins;
    }
    
    /**
     * Find the first KTLC that occured BEFORE in time of the current KTLC
     * @return the previous KTLCEdition or null if none
     */
    public KTLCEdition findPreviousKTLC() { 
    	return find("date < ? ORDER BY date DESC", this.date).first();
    }
    
    /**
     * Find the first KTLC that occured AFTER in time of the current KTLC
     * @return the next KTLCEdition or null if none
     */
    public KTLCEdition findNextKTLC() { 
    	return find("date > ? ORDER BY date ASC", this.date).first();
    }

    public static KTLCEdition findByNumber(Integer number) {
        return find("byNumber", number).first();
    }

    public List<KTLCRace> findRaces() {
        return KTLCRace.find("byKtlc", this).fetch();
    }
    
    public KTLCResult getResult(Player player) {
        return KTLCResult.findByKTLCAndPlayer(this, player);
    }
    
    /**
     * Ratio de participation d'un joueur à une édition
     * zéro si le joueur n'a pas participé, 100% s'il est premier.
     */
    public Integer getRatio(Player player) {
        KTLCResult result = getResult(player);
        Integer ratio = 0;
        if (result != null) {
            ratio = 100 - ((result.rank - 1) * 100 / this.results.size());
        }
        return ratio;
    }
}