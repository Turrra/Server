package dev.turra.codenames.server.game;

import dev.turra.codenames.common.CardColor;

import java.awt.*;

/**
 * Represents a single card in the game.
 */
public class Card {

	private int x;
	private int y;
	private String word;
	private CardColor color;
	private boolean isRevealed;

	/**
	 * Creates a new card.
	 * @param x The x coordinate of the card.
	 * @param y The y coordinate of the card.
	 * @param word The word on the card.
	 * @param color The color of the card.
	 */
    public Card(int x, int y, String word, CardColor color) {
        this.x = x;
        this.y = y;
        this.word = word;
		this.color = color;
        this.isRevealed = false;
    }

	/**
	 *
	 * @return The x coordinate of the card.
	 */
    public int getX() {
        return x;
    }

	/**
	 *
	 * @return The y coordinate of the card.
	 */
    public int getY() {
        return y;
    }

	/**
	 *
	 * @return The word on the card.
	 */
    public String getWord() {
        return word;
    }

	/**
	 *
	 * @return The color of the card.
	 */
	public CardColor getColor() {
        return color;
    }

	/**
	 *
	 * @return Whether the card is revealed or not.
	 */
    public boolean isRevealed() {
        return isRevealed;
    }

	/**
	 *
	 * @param isRevealed Sets whether the card is revealed or not.
	 */
    public void setRevealed(boolean isRevealed) {
        this.isRevealed = isRevealed;
    }

}
