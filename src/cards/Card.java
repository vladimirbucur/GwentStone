package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;
import game.GameBoard;

import java.util.ArrayList;

public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean isFrozen;
    private boolean isTank;
    private boolean isUsed;

    public Card(final CardInput card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = new ArrayList<>();
        for (String color : card.getColors()) {
            this.colors.add(color);
        }
        this.name = new String(card.getName());
        this.isFrozen = false;
        this.isTank = false;
        this.isUsed = false;
    }

    public Card(final Card card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = new ArrayList<String>();
        for (String color : card.getColors()) {
            this.colors.add(color);
        }
        this.name = card.getName();
        this.isFrozen = card.getIsFrozen();
        this.isTank = card.getIsTank();
        this.isUsed = card.getIsUsed();
    }

    /**
     * The method that marks if a card is a tank card
     */
    @JsonIgnore
    public void cardTank() {
        if (this.getName().equals("Goliath") || this.getName().equals("Warden")) {
            this.isTank = true;
        }
    }

    /**
     * The method that marks if a card is an environment card
     * @return
     */
    @JsonIgnore
    public boolean isEnvironment() {
        if (this.getName().equals("Firestorm") || this.getName().equals("Winterfell")
                || this.getName().equals("Heart Hound")) {
            return true;
        }

        return false;
    }

    /**
     * The method that marks if a card is a hero card
     * @return
     */
    @JsonIgnore
    public boolean isHero() {
        if (this.getName().equals("Lord Royce") || this.getName().equals("Empress Thorina")
                || this.getName().equals("King Mudface")
                || this.getName().equals("General Kocioraw")) {
            return true;
        }

        return false;
    }

    /**
     * The method that returns in which row a book should be located according to its type
     * @return
     */
    public int positionOnTable() {
        if (this.isEnvironment() || this.isHero()) {
            return -1; // is not on the table
        }

        if (this.getName().equals("The Ripper") || this.getName().equals("Miraj")
                || this.getName().equals("Goliath") || this.getName().equals("Warden")) {
            return 1; // front row
        }

        if (this.getName().equals("Sentinel") || this.getName().equals("Berserker")
                || this.getName().equals("The Cursed One") || this.getName().equals("Disciple")) {
            return 2; // back row
        }

        return -1; // there is a problem
    }

    /**
     * Getter
     * @return
     */
    public int getMana() {
        return mana;
    }

    /**
     * Setter
     * @param mana
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * Getter
     * @return
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * Setter
     * @param attackDamage
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * Getter
     * @return
     */
    public int getHealth() {
        return health;
    }

    /**
     * Setter
     * @param health
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * Setter
     * @param damage
     */
    public void takeDamage(final int damage) {
        this.health = this.health - damage;
    }

    /**
     * Getter
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter
     * @param description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Getter
     * @return
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * Setter
     * @param colors
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    /**
     * Getter
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Setter
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter
     * @return
     */
    @JsonIgnore
    public boolean getIsFrozen() {
        return isFrozen;
    }

    /**
     * Setter
     * @param frozen
     */
    public void setIsFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    /**
     * Getter
     * @return
     */
    @JsonIgnore
    public boolean getIsTank() {
        return isTank;
    }

    /**
     * Getter
     * @return
     */
    @JsonIgnore
    public boolean getIsUsed() {
        return isUsed;
    }

    /**
     * Setter
     * @param used
     */
    public void setIsUsed(final boolean used) {
        isUsed = used;
    }
}
