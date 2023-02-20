/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

/**
 *
 * @author Alessia
 */
public class Probability {

    public static double fraction(int positive_case, int total) {
        if (total == 0) {
            System.out.println("Impossible to divide by 0.");
            return 0;
        } else {
            return ((double) positive_case) / total;
        }
    }

    public static double probCond(double num, double denom) {
        if (denom == 0) {
            System.out.println("Impossible to divide by 0.");
            return 0;
        } else {
            return num / denom;
        }
    }

    public static double finalProduct(double prob1, double prob2) {
        return prob1 * prob2;
    }

}
