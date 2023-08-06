/*
 *  Jed Newcomb - A character class which denotes objects
 *                seen on the screen during gameplay.
 */
package com.example.asteroids;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 *
 * @author Jed
 */
public abstract class Character {

    private Polygon character;
    private Point2D movement;
    private boolean aliveStatus;

    public Character(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);
        this.aliveStatus = true;

        movement = new Point2D(0, 0);
    }

    public Polygon getCharacter() {
        return this.character;
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 5);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 5);
    }

    public void setMovement(Point2D movement) {
        this.movement = movement;
    }

    public Point2D getMovement() {
        return this.movement;
    }

    //this method can improve, as at times certain asteroids will
    //"jump" across the screen. try to figure out getBoundsInParent()?
    public void move() {
        this.character.setTranslateX(this.character.getTranslateX()
                + this.movement.getX());

        this.character.setTranslateY(this.character.getTranslateY()
                + this.movement.getY());

        if (this.character.getTranslateX() < 0) {
            this.character.setTranslateX(this.character.getTranslateX()
                    + AsteroidsApplication.WIDTH);
        }

        if (this.character.getTranslateX() > AsteroidsApplication.WIDTH) {
            this.character.setTranslateX(this.character.getTranslateX()
                    % AsteroidsApplication.WIDTH);
        }

        if (this.character.getTranslateY() < 0) {
            this.character.setTranslateY(this.character.getTranslateY()
                    + AsteroidsApplication.HEIGHT);
        }

        if (this.character.getTranslateY() > AsteroidsApplication.HEIGHT) {
            this.character.setTranslateY(this.character.getTranslateY()
                    % AsteroidsApplication.HEIGHT);
        }

    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= .05;
        changeY *= .05;

        this.movement = this.movement.add(changeX, changeY);
    }

    public void setAliveStatus(Boolean aliveStatus) {
        this.aliveStatus = aliveStatus;
    }

    public boolean getAliveStatus() {
        return this.aliveStatus;
    }

    public boolean collide(Character other) {
        Shape collisionArea
                = Shape.intersect(this.character, other.getCharacter());

        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

}
