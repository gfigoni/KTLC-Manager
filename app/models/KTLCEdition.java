package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
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
    public List<KTLCRace> races;
    @OneToMany(mappedBy = "ktlc", cascade = CascadeType.ALL)
    public List<KTLCPlayerResult> results;

    public KTLCEdition(Integer num, Date date) {
        super();
        this.number = num;
        this.date = date;
    }

    @Override
    public String toString() {
        return number.toString();
    }

    public KTLCEdition addRace(TMMap map) {
        KTLCRace race = new KTLCRace(this, map);
        this.races.add(race);
        this.save();
        return this;
    }

    public static KTLCEdition createKTLCEdition(Integer num, Date date, Reader reader) throws UnknownPlayerException, IOException {
        KTLCEdition ktlc = new KTLCEdition(num, date).save();
        Set<Player> players = new HashSet<Player>(); // liste des joueurs de l'édition
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
                    String pid = m.group(4);
                    String mapName = m.group(1);
                    String environment = m.group(2);
                    Player author = Player.findByLogin(pid);
                    if (author == null) {
                        throw new UnknownPlayerException(pid);
                    }
                    map = new TMMap(mapId, mapName, author, TMEnvironment.getEnvironment(environment)).save();
                }

                race = new KTLCRace(ktlc, map).save();
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
                    String pid = m.group(3);

                    Player p = Player.findByLogin(pid);
                    if (p == null) {
                        throw new UnknownPlayerException(pid);
                    }
                    players.add(p);

                    new KTLCRacePlayerResult(race, p, rank, score, round).save();
                }
            }
        }

        // Consolidation des résultats de la KTLC
        List<KTLCPlayerResult> results = new ArrayList<KTLCPlayerResult>();
        for (Player p : players) {
            KTLCPlayerResult result = new KTLCPlayerResult(ktlc, p);

            int score = 0;
            double rank = 0d;
            int nbRace = 0;

            for (KTLCRace r : races) {
                KTLCRacePlayerResult pr = r.findResult(p);
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
        KTLCPlayerResultScoreComparator comp = new KTLCPlayerResultScoreComparator();
        Collections.sort(results, new KTLCPlayerResultScoreComparator());

        // affectation des rangs
        int nbAtTheSameRank = 1;
        int currentRank = 1;
        KTLCPlayerResult previous = null;
        for (KTLCPlayerResult current : results) {
            if (previous == null) {
                // 1er joueur
                current.rank = currentRank;
                currentRank++;
            } else {
                // autres joueurs
                if (comp.compare(previous, current) == 0) {
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

    public static Map<String, String> checkPlayers(Reader r) throws IOException {
        Map<String, String> players = new HashMap<String, String>();

        BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            Matcher m = KTLCUtils.FAST_LOG_PATTERN_RACE.matcher(line);
            if (m.matches()) {
                // début de log pour une course
                // auteur de la map
                String pid = m.group(4);
                Player p = Player.findByLogin(pid);
                if (p == null) {
                    if (players.get(pid) == null) {
                        players.put(pid, null);
                    }
                }
            } else {
                m = KTLCUtils.FAST_LOG_PATTERN_PLACE.matcher(line);
                if (m.matches()) {
                    // détail de log pour une course
                    // joueur
                    String pid = m.group(3);
                    String pname = m.group(4);

                    Player p = Player.findByLogin(pid);
                    if (p == null) {
                        players.put(pid, pname);
                    }
                }
            }
        }

        return players;
    }

    public static KTLCEdition findByNumber(Integer number) {
        return find("byNumber", number).first();
    }

    public List<KTLCRace> findRaces() {
        return KTLCRace.find("byKtlc", this).fetch();
    }
}