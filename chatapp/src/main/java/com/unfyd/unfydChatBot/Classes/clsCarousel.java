package com.unfyd.unfydChatBot.Classes;

public class clsCarousel {

    private String image;

    private String tooltip;

    private String value;

    private String key;

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String getTooltip ()
    {
        return tooltip;
    }

    public void setTooltip (String tooltip)
    {
        this.tooltip = tooltip;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public String getKey ()
    {
        return key;
    }

    public void setKey (String key)
    {
        this.key = key;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [image = "+image+", tooltip = "+tooltip+", value = "+value+", key = "+key+"]";
    }
}
