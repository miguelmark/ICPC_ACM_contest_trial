/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icpc.acm.contest.trial;
import black.*;
/**
 *
 * @author Miguel
 */
public class ICPCACMContestTrial {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Black.gridOn();
        // for debugging purposes
        // Black.start(10, Demo.class,UVI_Player.class, 100, 500000000, 500);
        Black.start(10, UVI_Player.class, Demo.class);
        
    }
}
