/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Comparator;

/**
 *
 * @author Gilles
 */
public class KTLCPlayerResultScoreComparator implements Comparator<KTLCPlayerResult> {

    public int compare(KTLCPlayerResult r1, KTLCPlayerResult r2) {
        int comp = r2.score.compareTo(r1.score);
        if (comp == 0) {
            comp = r1.rankAvg.compareTo(r2.rankAvg);
        }
        return comp;
    }
    
}
