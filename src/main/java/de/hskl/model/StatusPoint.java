package de.hskl.model;

import processing.core.PApplet;

/*
 * Die StatusPoint Klasse generiert die Punkte die sich in der Simulation umherbewegen und die verschiedenen Personen darstellen.
 * Durch verschiedene Farben werden die unterschiedlichen Zustände dargestellt.
 * Durch Kreuze wird die Risikogruppe dargestellt.
 */

public class StatusPoint {
    private PApplet p;
    private float colorR;
    private float colorG;
    private float colorB;
    private float xPos;
    private float yPos;
    private float height;
    private float width;

    public StatusPoint(PApplet p, float colorR, float colorG, float colorB) {
        this.p = p;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
    }

    public PApplet getP() {
        return p;
    }

    public StatusPoint setP(PApplet p) {
        this.p = p;
        return this;
    }

    public float getColorR() {
        return colorR;
    }

    public StatusPoint setColorR(float colorR) {
        this.colorR = colorR;
        return this;
    }

    public float getColorG() {
        return colorG;
    }

    public StatusPoint setColorG(float colorG) {
        this.colorG = colorG;
        return this;
    }

    public float getColorB() {
        return colorB;
    }

    public StatusPoint setColorB(float colorB) {
        this.colorB = colorB;
        return this;
    }

    public float getxPos() {
        return xPos;
    }

    public StatusPoint setxPos(float xPos) {
        this.xPos = xPos;
        return this;
    }

    public float getyPos() {
        return yPos;
    }

    public StatusPoint setyPos(float yPos) {
        this.yPos = yPos;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public StatusPoint setHeight(float height) {
        this.height = height;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public StatusPoint setWidth(float width) {
        this.width = width;
        return this;
    }

    public StatusPoint setStroke(float stroke) {
        this.height = stroke;
        this.width = stroke;
        return this;
    }
/*
 * Stellt die unterschiedlichen Punkte dar.
 * Legt Größe und Farbe der Punkte fest.
 */
    public void show() {

        p.strokeWeight(1);
        p.stroke(colorR, colorG, colorB);
        p.fill(colorR, colorG, colorB);
        p.ellipse(xPos, yPos, width, height);
        p.fill(255);
        p.strokeWeight(8);
    }
/*
 * Generiert Kreuze in den Punkten für Personen der Risikogruppe
 */
    public void showAtRisk() {
        p.stroke(colorR, colorG, colorB);
        p.strokeWeight(2);
        p.line(xPos - width / 2.0f, yPos, xPos + width / 2.0f, yPos);
        p.line(xPos, yPos - height / 2.0f, xPos, yPos + height / 2.0f);
        p.strokeWeight(8);
        p.fill(255);
    }


}
