package view.commons;

import java.io.Serializable;

public class PageDescriptor implements Serializable{
    private String name;
    private String action;
    private String backTitle;

    public PageDescriptor(String name) {
        this.name = name;
    }

    /**
     * Returns the displayable name of the page.
     *
     * @return The name of the page.
     */
    public String getName() {
        return name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    /**
     * Sets the back link title.
     *
     * @param title
     */
    public void setBackTitle(String title) {
        this.backTitle = title;
    }

    /**
     * Returns the back link title.
     * Use this string when rendering a back link or button.
     * If not set, a default title will be provided.
     *
     * @param title The title to set on the back link.
     */
    public String getBackTitle() {
        if (backTitle == null) {
            return "Torna a " + getName();
        }
        return backTitle;
    }
}
