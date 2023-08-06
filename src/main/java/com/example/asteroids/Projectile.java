package com.example.asteroids;

import javafx.scene.shape.Polygon;

/**
 *
 * @author Jed
 */
public class Projectile extends Character {

    public Projectile(int x, int y) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
    }

    //Override our parent class to stop projectiles
    //from screen jumping like the ship and asteroids
    @Override
    public void move() {
        super.getCharacter().setTranslateX(super.getCharacter().getTranslateX()
                + super.getMovement().getX());

        super.getCharacter().setTranslateY(super.getCharacter().getTranslateY()
                + super.getMovement().getY());

    }

}