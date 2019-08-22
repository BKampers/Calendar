/*
** © Bart Kampers
*/

package bka.calendar.swing;


public class MacPlatformAdapter {


    public void setIcon(java.awt.Image image) {
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
    }


}
