package Modules;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class Duration {
    public String text;
    public int value;

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }
    public String getText(){return this.text;}
    public int getValue(){return this.value;}
}
