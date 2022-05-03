/*
 * MIT License
 * Copyright (c) 2020 Yannis Heim, Stefan Böbel

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

/*
 * Beschreibung
 *
 * Hier wird eine Technologie genutzt namens "Processing",
 * die spezialisiert ist auf die Erstellung von Grafiken, Simulationen und Animationen.
 * Die Technologie "Processing" ist im engeren Sinne eine Programmiersprache, die auf
 * JAVA basiert. Processing nutzt zum Zeichnen der Visualierungselemente drei Klassen:
 * settings(), setup() und draw(), die hier in der "Main" Klasse zu finden sind.
 * Weiter Informationen hierzu findet man auf https://processing.org/.
 *
 * */

package de.hskl;

import de.hskl.contol.AlgorithmInfection;
import de.hskl.contol.MaskDistanceController;
import de.hskl.model.StatusPoint;
import de.hskl.resources.msgCovidInformation;
import de.hskl.util.MathUtil;
import de.hskl.model.Person;
import de.hskl.view.GuiSettings;
import g4p_controls.*;
import processing.core.PApplet;

import javax.swing.*;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.io.*;

import static de.hskl.model.HealthStatus.*;


public class Main extends PApplet {

    private static GCustomSlider basicReproductionRatio;
    private static GCustomSlider numPerson;
    private static GCustomSlider numStartInfections;
    private static GCustomSlider deathratio;
    private static GCustomSlider peopleAtRisk;
    private static GCheckbox Mask;
    private static GCheckbox Distance;
    private static float GuiBasicReproductionRatioValue = 2.0f;
    private static int GuiNumPersonValue = 100;
    private static int GuiNumStartInfectionsValue = 4;
    private static float GuiDeathRatio = 1.0f;
    private static float GuiPeopleAtRisk = 0.0f;
    private static Person[] persons = new Person[100];
    private static MaskDistanceController maskdistanceController = new MaskDistanceController(false, false);
    private static GButton btnstart;
    private static GButton btnstop;
    private static GButton info;
    private static GLabel healthyState;
    private static GLabel infectedState;
    private static GLabel deadState;
    private static GLabel healedState;
    private static GLabel riskState;
    private static int healthyCounter = 0;
    private static int infectedCounter = 0;
    private static int deadCounter = 0;
    private static int healedCounter = 0;
    private static Font font = new Font("TimesRoman", Font.PLAIN, 16);
    private static int strokeWeightValue = 8; // dicke der Punkte definiert
    private static StatusPoint infectedStatePoint;
    private static StatusPoint healthyStatePoint;
    private static StatusPoint deadStatePoint;
    private static StatusPoint healedStatePoint;
    private static StatusPoint riskStatePoint;


    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});
    }

    public void settings() {
        size(1000, 800);
    }

    public void setup() {
        surface.setTitle("PM007 - Corona-Simulation");
        initialize();

        /*
         * Erzeugen der Buttons
         * */

        btnstart = new GButton(this, 32, 20, 140, 20, "NEUSTART");
        btnstart.setLocalColorScheme(GCScheme.GREEN_SCHEME);

        btnstop = new GButton(this, 32, 50, 140, 20, "STOP");
        btnstop.setLocalColorScheme(GCScheme.RED_SCHEME);



        /*
         * Den Labelnamen der Slider mit den Slider gruppieren
         * */

        GGroup groupOfLabelReprRatio = new GGroup(this);
        GLabel labelRepRatio = new GLabel(this, 50, 65, 200, 46, "Reproduktionszahl");
        basicReproductionRatio = GuiSettings.buildSliderForBasicReproductionRatio(this, basicReproductionRatio);
        groupOfLabelReprRatio.addControls(labelRepRatio, basicReproductionRatio);


        GGroup groupOfLabelNumPerson = new GGroup(this);
        GLabel labelNumPerson = new GLabel(this, 25, 135, 200, 45, "Gesamtanzahl der Personen");
        numPerson = GuiSettings.buildSliderForNumberPerson(this, numPerson);
        groupOfLabelReprRatio.addControls(labelNumPerson, numPerson);


        GGroup groupOfLabelNumStartInfects = new GGroup(this);
        GLabel labelNumInfects = new GLabel(this, 20, 205, 200, 45, "Anzahl der Infizierte am Anfang");
        numStartInfections = GuiSettings.buildSliderForNumberStartInfects(this, numStartInfections);
        groupOfLabelReprRatio.addControls(labelNumInfects, numStartInfections);

        GGroup groupOfLabelDeathRatio = new GGroup(this);
        GLabel labelDeathRation = new GLabel(this, 45, 275, 200, 45, "Sterblichkeitsrate in %");
        deathratio = GuiSettings.buildSliderForDeathratio(this, deathratio);
        groupOfLabelDeathRatio.addControls(labelDeathRation, deathratio);

        GGroup groupOfLabelPeopleAtRisk = new GGroup(this);
        GLabel labelPeopleAtRisk = new GLabel(this, 37, 345, 200, 45, "Risikogruppenanteil in %");
        peopleAtRisk = GuiSettings.buildSliderForPeopleAtRisk(this, peopleAtRisk);
        groupOfLabelPeopleAtRisk.addControls(labelPeopleAtRisk, peopleAtRisk);
        strokeWeight(1);

        Mask = GuiSettings.buildCheckboxForMask(this, Mask);

        Distance = GuiSettings.buildCheckboxForDistance(this, Distance);
        strokeWeight(strokeWeightValue);


        /*
         * Erstellen der Legende
         * */

        healthyState = new GLabel(this, 25, 500, 200, 100);
        healthyState.setFont(font);
        healthyStatePoint = new StatusPoint(this, 0, 247, 0).setxPos(10).setyPos(550).setStroke(10);

        infectedState = new GLabel(this, 25, 530, 200, 100);
        infectedState.setFont(font);
        infectedStatePoint = new StatusPoint(this, 255, 255, 0).setxPos(10).setyPos(580).setStroke(10);

        deadState = new GLabel(this, 25, 560, 200, 100);
        deadState.setFont(font);
        deadStatePoint = new StatusPoint(this, 255, 0, 0).setxPos(10).setyPos(610).setStroke(10);

        healedState = new GLabel(this, 25, 590, 200, 100);
        healedState.setFont(font);
        healedStatePoint = new StatusPoint(this, 0, 51, 255).setxPos(10).setyPos(640).setStroke(10);

        riskState = new GLabel(this, 25, 620, 200, 100);
        riskState.setFont(font);
        riskStatePoint = new StatusPoint(this, 0, 0, 0).setxPos(10).setyPos(670).setStroke(10);

        /*
         * Info-Knopf erstellen
         * */

        info = new GButton(this, 32, 720, 140, 20, "INFO");
        info.setLocalColorScheme(GCScheme.CYAN_SCHEME);

    }


    /*
     * Jede Änderung des Sliders führt zu einer Neuinitialisierung der Werte
     * dies muss mit dem Startbutton bestätigt werden
     * */

    private void initialize() {

        if (GuiNumPersonValue >= GuiNumStartInfectionsValue) {
            for (int i = 0; i < GuiNumPersonValue; i++) {
                persons[i] = new Person(this, strokeWeightValue);
                persons[i].generateRandomPosition(strokeWeightValue);
            }

            for (int i = 0; i < GuiNumStartInfectionsValue; i++) {
                persons[i].setCurrentHealthStatus(INFECTED);
            }

        } else {
            for (int i = 0; i < GuiNumPersonValue; i++) {
                persons[i] = new Person(this, strokeWeightValue);
                persons[i].generateRandomPosition(strokeWeightValue);
                persons[i].setCurrentHealthStatus(INFECTED);
            }
        }


        float AbsPeopleAtRisk = (GuiPeopleAtRisk / 100.0f) * (float) GuiNumPersonValue;
        int AbsPeopleAtRiskAsInt = (int) AbsPeopleAtRisk;
        for (int i = 0; i < AbsPeopleAtRiskAsInt; i++) {
            persons[i].setisAtRisk();
        }

        for (int i = 0; i < persons.length; i++) {
            if (persons[i].getCurrentHealthStatus() == INFECTED) {
                if (maskdistanceController.isMasked() && !maskdistanceController.isDistance()) {
                    persons[i].setMasked(true);
                    persons[i].setDistanceOK(false);
                } else if (maskdistanceController.isMasked() && maskdistanceController.isDistance()) {
                    persons[i].setMasked(true);
                    persons[i].setDistanceOK(true);
                } else if (!maskdistanceController.isMasked() && maskdistanceController.isDistance()) {
                    persons[i].setMasked(false);
                    persons[i].setDistanceOK(true);
                }
            }
        }
    }


    public void draw() {

        background(20);
        stroke(255);
        strokeWeight(strokeWeightValue);
        rect(0, 0, 200, height);


        /*
         * Person anzeigen und Bewegung berechnen
         */

        for (int i = 0; i < persons.length; i++) {
            persons[i].move();
            persons[i].show();
        }

        /*
         * Zählt bei jedem Durchlauf die Anzahl toter, gesunder, geheilter und infizierter Menschen
         */

        for (int i = 0; i < persons.length; i++) {
            switch (persons[i].getCurrentHealthStatus()) {
                case HEALTHY:
                    healthyCounter++;
                    break;
                case INFECTED:
                    infectedCounter++;
                    break;
                case DEAD:
                    deadCounter++;
                    break;
                case HEALED:
                    healedCounter++;
                    break;
            }
        }

        /*
         * Ruft den Algorithmus auf der für die Infektion gesunder Menschen verantwortlich ist
         * */

        AlgorithmInfection.infect(persons, GuiBasicReproductionRatioValue);


        /*
         * Gui Werte aktualiseren
         * */

        healthyStatePoint.show();
        healthyState.setText(healthyCounter + " Gesunde");
        healthyCounter = 0;


        infectedStatePoint.show();
        infectedState.setText(infectedCounter + " Infizierte");
        infectedCounter = 0;

        deadStatePoint.show();
        deadState.setText(deadCounter + " Tote");
        deadCounter = 0;

        healedStatePoint.show();
        healedState.setText(healedCounter + " Geheilte");
        healedCounter = 0;

        riskStatePoint.showAtRisk();
        riskState.setText("In Risikogruppe");


        /*
         * DayTime-Simulator: zählt die Frames, um den Reproduktionsfaktor zu visualisieren
         * Setzt die Wahrscheinlichkeit, dass ein Infizierter stirbt
         * */

        for (int i = 0; i < persons.length; i++) {

            if (persons[i].isStoped() == false && persons[i].getCurrentHealthStatus() == INFECTED) {

                //300 Frames entsprechen einem Tag
                if (persons[i].getDaysCounter() >= 300) {
                    persons[i].resetCounters();
                } else {
                    persons[i].riseCounter();
                }


                /*
                Todeswahrscheinlichkeit = GuiDeathRatio, es wird ein Wert zwischen 1 und 1000 gewürfelt,
                wenn dieser kleiner oder gleich DeathRatio ist stirbt die Person.
                Falls eine Person teil der Risikogruppe ist erhöht sich die Sterbewahrscheinlichkeit auf das 3-fache
                 */


                if (((int) (Math.random() * 1000) + 1 <= GuiDeathRatio * 10.0f) && (persons[i].getIsSafe() == false) && persons[i].getisAtRisk() == false) {
                    persons[i].setCurrentHealthStatus(DEAD);
                } else if (persons[i].getisAtRisk() == true && persons[i].getIsSafe() == false && (int) (Math.random() * 1000) + 1 <= GuiDeathRatio * 10.0f * 3.0f) {
                    persons[i].setCurrentHealthStatus(DEAD);
                } else {
                    persons[i].setIsSafe(true);
                }

                //nach 700 Frames sind die Infizierten sie wieder geheilt, falls sie nicht bereits tot sind
                if (persons[i].getDaysOfInfection() >= 700) {
                    persons[i].setCurrentHealthStatus(HEALED);
                }
            }
        }
    }


    /*
     * Wird nur bei Änderung der Slider ausgeführt,
     * dies übernimmt Processing von selbst
     * */

    public void handleSliderEvents(GValueControl slider, GEvent event) {
        if (slider == basicReproductionRatio) {
            GuiBasicReproductionRatioValue = MathUtil.roundOneDigit(slider.getValueF());
        }
        if (slider == numPerson) {
            GuiNumPersonValue = slider.getValueI();
        }
        if (slider == numStartInfections) {
            GuiNumStartInfectionsValue = slider.getValueI();
        }
        if (slider == deathratio) {
            GuiDeathRatio = MathUtil.roundOneDigit(slider.getValueF());
        }
        if (slider == peopleAtRisk) {
            GuiPeopleAtRisk = slider.getValueI();
        }
    }


    /*
     * Handhabt die Buttons für die Events für STOP, START und INFO
     * */

    public void handleButtonEvents(GButton button, GEvent event) {
        if (button == btnstart && event == GEvent.CLICKED) {
            persons = new Person[GuiNumPersonValue];
            initialize();
        }
        if (button == btnstop && event == GEvent.CLICKED) {
            for (int i = 0; i < persons.length; i++) {
                persons[i].stopMotion();
            }
        }
        if (button == info && event == GEvent.CLICKED) {


            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    /*JTextArea textArea=new JTextArea(msgCovidInformation.getText());*/
                    JTextArea textArea = new JTextArea(msgCovidInformation.getText());
                    //textArea.append(getText());
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    scrollPane.setPreferredSize(new Dimension(800, 800));
                    JOptionPane.showMessageDialog(null, scrollPane, "Infos zur Simulation und Covid-19", OK_OPTION);

                }
            });


        }
    }


    /*
     * Handhabt die Checkboxen für Maskenpflicht und Abstandsregelung
     * */

    public void handleToggleControlEvents(GToggleControl box, GEvent event) {
        if (Mask.isSelected() == true && Distance.isSelected() == false) {
            maskdistanceController.setMasked(true);
            maskdistanceController.setDistance(false);
        }
        if (Mask.isSelected() == true && Distance.isSelected() == true) {
            maskdistanceController.setMasked(true);
            maskdistanceController.setDistance(true);
        }
        if (Mask.isSelected() == false && Distance.isSelected() == true) {
            maskdistanceController.setMasked(false);
            maskdistanceController.setDistance(true);
        }
    }
}