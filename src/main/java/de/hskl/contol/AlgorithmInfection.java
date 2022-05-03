/*
 * MIT License
 * Copyright (c) 2020 Wissam Aalamareen, Zouhir El Kharboubi

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * */


package de.hskl.contol;

import de.hskl.model.HealthStatus;
import de.hskl.model.Person;
import static processing.core.PApplet.dist;


public class AlgorithmInfection {

    private static float personDistance(Person per1, Person per2) {
        return dist(per1.getPosition().x, per1.getPosition().y, per2.getPosition().x, per2.getPosition().y);
    }


    /*
     *  Vergleicht 2 Personen die innerhalb des Infektionsradiuses sind. Eine infizierte Person kann eine gesunde
     *  Person anstecken. Es können zusätzliche noch Attribute angewählt werden, wie Personen tragen alle Masken und
     *  Personen halten sich die Abstandsregel
     * */

    public static void infect(Person[] persons, float basicReproductionRatioValue) {

        /*
         * Verhältnis von 1:5 (20%), um mit einer Maske angesteckt zu werden
         * */

        int ratioToInfectWithMask = 5;


        /*
         * Verhältnis von 1:7 (14.7 Prozent), um von einer Person, die sich an die Abstandsreglung hält infizert zu werden.
         * */

        int ratioToInfectPeopleWithDistance = 7;



        /*
         * Es wird jede Person mit jeder anderen Person verglichen und kontrolliert, ob sie andere Personen
         * anstecken kann.
         * */

        for (int i = 0; i < persons.length; i++) {

            /*
             * Sperzialfall: Personen tragen keine Masken und halten sich auch nicht an die Abstandreglung
             * */

            if (persons[i].getCurrentHealthStatus() == HealthStatus.INFECTED && persons[i].isMasked() == false && persons[i].isDistanceOK() == false) {



                for (int j = 0; j < persons.length; j++) {

                    if ((persons[i] != persons[j]) && (persons[j].getCurrentHealthStatus() == HealthStatus.HEALTHY)) {

                        if (personDistance(persons[i], persons[j]) < persons[j].getRadiusPerson()) {

                            /*
                            * Der Basisreproduktionsfaktor gibt an wie viele Personen von einem infizierten angesteckt werden können
                            * */

                            if (persons[i].isAbleToInfect(basicReproductionRatioValue)) {
                                persons[j].setCurrentHealthStatus(HealthStatus.INFECTED);
                            }
                        }
                    }
                }
            }


            /*
            * Sperzialfall: Personen tragen Masken und halten sich nicht an die Abstandreglung
            * */

            if (persons[i].getCurrentHealthStatus() == HealthStatus.INFECTED && persons[i].isMasked() == true && persons[i].isDistanceOK() == false) {


                for (int j = 0; j < persons.length; j++) {

                    if ((persons[i] != persons[j]) && (persons[j].getCurrentHealthStatus() == HealthStatus.HEALTHY)) {

                        if (personDistance(persons[i], persons[j]) < persons[j].getRadiusPerson()) {

                            /*
                            * Es wird mit einer Wahrscheinlichkeit geprüft, ob sich die Person anstecken kann
                            * Es wird eine Zahl erzeugt die kleiner ist als ratioToInfectWithMask und danach
                            * auf eine ganze Zahl gerundet, falls diese 1 ist kann sich die Person infizieren.
                            * */

                            if (persons[i].isAbleToInfect(basicReproductionRatioValue) && (int) (Math.random() * ratioToInfectWithMask) == 1) {

                                persons[j].setCurrentHealthStatus(HealthStatus.INFECTED);
                            }
                        }
                    }
                }

            }


            /*
            * Spezialfall: Personen tragen keine Masken und halten sich denoch an die Abstandreglung
            * */

            if (persons[i].getCurrentHealthStatus() == HealthStatus.INFECTED && persons[i].isMasked() == false && persons[i].isDistanceOK() == true) {

                for (int j = 0; j < persons.length; j++) {

                    if ((persons[i] != persons[j]) && (persons[j].getCurrentHealthStatus() == HealthStatus.HEALTHY)) {

                        if (personDistance(persons[i], persons[j]) < persons[j].getRadiusPerson()) {

                            /*
                             * Es wird mit einer Wahrscheinlichkeit geprüft, ob sich die Person anstecken kann.
                             * Es wird eine Zahl erzeugt die kleiner ist als ratioToInfectPeopleWithDistance und danach
                             * auf eine ganze Zahl gerundet, falls diese 1 ist kann sich die Person infizieren.
                             * */

                            if (persons[i].isAbleToInfect(basicReproductionRatioValue) && (int) (Math.random() * ratioToInfectPeopleWithDistance) == 1) {

                                persons[j].setCurrentHealthStatus(HealthStatus.INFECTED);
                            }
                        }
                    }
                }
            }


            /*
             * Spezialfall: Personen tragen Masken und halten sich auch an die Abstandreglung
             * */

            if (persons[i].getCurrentHealthStatus() == HealthStatus.INFECTED && persons[i].isMasked() == true && persons[i].isDistanceOK() == true) {


                for (int j = 0; j < persons.length; j++) {

                    if ((persons[i] != persons[j]) && (persons[j].getCurrentHealthStatus() == HealthStatus.HEALTHY)) {

                        if (personDistance(persons[i], persons[j]) < persons[j].getRadiusPerson()) {

                            /*
                             * Es wird mit einer Wahrscheinlichkeit geprüft, ob sich die Person anstecken kann.
                             * Es wird eine Zahl erzeugt die kleiner ist als ratioToInfectPeopleWithDistance und danach
                             * auf eine ganze Zahl gerundet, falls diese 1 ist kann sich die Person infizieren.
                             * Zusätzlich wird eine Zahl erzeugt die kleiner ist als ratioToInfectWithMask und danach
                             * auf eine ganze Zahl gerundet, falls diese 1 ist kann sich die Person infizieren.
                             * */

                            if ((persons[i].isAbleToInfect(basicReproductionRatioValue))
                                && ((int) (Math.random() * ratioToInfectPeopleWithDistance) == 1)
                                && ((int) (Math.random() * ratioToInfectWithMask) == 1)) {

                                persons[j].setCurrentHealthStatus(HealthStatus.INFECTED);
                            }
                        }
                    }
                }
            }
        }
    }
}
