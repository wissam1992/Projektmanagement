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

package de.hskl.model;

import processing.core.PApplet;
import processing.core.PVector;


/*
 * Bei der Personen Klasse wird unter anderem die Position und Gesundheitszustand gespeichert
 * Die Position der Person wird zuföllig gewählt
 * Movement als fixer Parameter beim Erzeugen der Person wird definiert
 * durch OutOfBounds wird verhindert das sie den Canvas verlassen
 */

public class Person {

    private PApplet p;
    private PVector position;
    private PVector move;
    private HealthStatus currentHealthStatus;
    private float radiusPerson = 7f;
    private boolean isAtRisk = false;
    private boolean masked = false;
    private boolean distanceOK = false;
    private int daysOfInfection = 0;
    private int daysCounter = 0;
    private boolean canInfect = false;
    private int infectCounter = 0;
    private boolean isSafe = false;
    private int strokeWeightValue;
    private boolean stoped = false;

    public Person(PApplet p, int strokeWeightValue) {
        this.p = p;
        this.strokeWeightValue = strokeWeightValue;
        currentHealthStatus = HealthStatus.HEALTHY;
        move = new PVector(p.random(-0.5f, 0.5f), p.random(-0.5f, 0.5f));
    }

    public PVector getPosition() {
        return position;
    }

    public float getRadiusPerson() {
        return radiusPerson;
    }

    public HealthStatus getCurrentHealthStatus() {
        return currentHealthStatus;
    }

    public void setCurrentHealthStatus(HealthStatus currentHealthStatus) {
        this.currentHealthStatus = currentHealthStatus;
    }

    public boolean getisAtRisk() {
        return isAtRisk;
    }

    public void setisAtRisk() {
        isAtRisk = true;
    }

    public int getDaysOfInfection() {
        return daysOfInfection;
    }

    public int getDaysCounter() {
        return daysCounter;
    }

    public boolean getIsSafe() {
        return isSafe;
    }

    public void setIsSafe(boolean value) {
        isSafe = value;
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    public boolean isDistanceOK() {
        return distanceOK;
    }

    public void setDistanceOK(boolean distanceOK) {
        this.distanceOK = distanceOK;
    }

    public boolean isStoped() {
        return stoped;
    }

    public void generateRandomPosition(int strokeWeightValue) {
        position = new PVector(p.random(200 + strokeWeightValue, p.width), p.random(0, p.height));
    }

    public void stopMotion() {
        move.x = 0;
        move.y = 0;
        stoped = true;
    }


    /*
     * Bewegt die Person gleichmäßig in eine Richtung
     * */

    public void move() {
        position.x += move.x;
        position.y += move.y;

        /*
         * Wenn die Person tot ist, soll sie sich nicht mehr bewegen
         * */

        if (currentHealthStatus == HealthStatus.DEAD) {
            move.x = 0;
            move.y = 0;
        }

        /*
         * Falls die Person aus der Bildgrenze hinaus gelangt,
         * gilt folgende Regel: Einfallswinkel = Ausfallswinkel
         * */

        outOfBounce();
    }


    /*
     * Erkennung der Grenzen des Canvas. Invertierung der x bzw y Achse, so dass alle Personen im Feld bleiben
     * Regel: Einfallswinkel = Ausfallswinkel
     */

    public void outOfBounce() {
        if (position.x > p.width || position.x <= 0) {
            move.x = -1 * move.x;
        } else if (position.y > p.height || position.y <= 0) {
            move.y = -1 * move.y;
        } else if (position.x <= 200 + strokeWeightValue) {
            move.x = -1 * move.x;
        }
    }


    /*
     * Farbcodierung der Person in RGB Farben auf dem Feld
     * */

    public void show() {
        if (currentHealthStatus == HealthStatus.HEALTHY) {
            p.stroke(0, 247, 0);
            p.point(position.x, position.y);

        } else if (currentHealthStatus == HealthStatus.INFECTED) {
            p.stroke(255, 255, 0);
            p.point(position.x, position.y);

        } else if (currentHealthStatus == HealthStatus.DEAD) {
            p.stroke(255, 0, 0);
            p.point(position.x, position.y);

        } else {
            p.stroke(0, 51, 255);
            p.point(position.x, position.y);
        }

        //zeichnet Kreuz in den Punkt wenn Person teil der Risikogruppe ist
        if (isAtRisk == true) {
            p.stroke(0);
            p.strokeWeight(2);
            p.line(position.x - strokeWeightValue / 2.0f, position.y, position.x + strokeWeightValue / 2.0f, position.y);
            p.line(position.x, position.y - strokeWeightValue / 2.0f, position.x, position.y + strokeWeightValue / 2.0f);
            p.strokeWeight(strokeWeightValue);

        }
    }


    public void riseCounter() {
        daysOfInfection++;
        daysCounter++;
    }


    public void resetCounters() {
        daysCounter = 0;
        canInfect = true;
        infectCounter = 0;
    }


    public boolean isAbleToInfect(float reproduction) {
        if (infectCounter < reproduction * 10.0f) {
            canInfect = true;
            infectCounter += 10;
        } else {
            canInfect = false;
        }
        return canInfect;
    }
}