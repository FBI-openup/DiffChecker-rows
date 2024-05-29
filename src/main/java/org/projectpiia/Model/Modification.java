package org.projectpiia.Model;

/**
 * Class representing a modification
 */
public class Modification {

    public enum Type {
        INSERT, DELETE, REPLACE
    }

    public enum State {
        WAITING, ACCEPTED, REJECTED, COUNTER_PROPOSAL
    }

    private State state = State.WAITING;
    private final Type type;
    private final String originalText;
    private String modifiedText;
    private final int position;
    private String comment = "";

    public Modification(String originalText, String modifiedText, int position, Type type) {
        this.originalText = originalText;
        this.modifiedText = modifiedText;
        this.position = position;
        this.type = type;
    }

    // Getters
    public String getOriginalText() {
        return originalText;
    }

    public String getModifiedText() {
        return modifiedText;
    }

    public int getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setModifiedText(String modifiedText) {
        this.modifiedText = modifiedText.substring(2, modifiedText.length());
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
