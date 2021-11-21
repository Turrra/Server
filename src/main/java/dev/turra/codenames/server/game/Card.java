package dev.turra.codenames.server.game;

import dev.turra.codenames.common.CardColor;

import java.awt.*;

public class Card {

	private int x;
	private int y;
	private String word;
	private CardColor color;
	private boolean isRevealed;

    public Card(int x, int y, String word, CardColor color) {
        this.x = x;
        this.y = y;
        this.word = word;
		this.color = color;
        this.isRevealed = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getWord() {
        return word;
    }

	public CardColor getColor() {
        return color;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean isRevealed) {
        this.isRevealed = isRevealed;
    }

}
