package org.krugler.peakskml;

public class GoogleEarthIcons {

    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int pal = 2; pal < 6; pal++) {
            System.out.println("\t<h2>pal" + pal + "</h2>");
            System.out.println("\t<ul>");
            for (int i = 0; i < 100; i++) {
                System.out.println("\t\t<li><img src=\"http://maps.google.com/mapfiles/kml/pal" + pal + "/icon" + i + ".png\" />icon" + i + ".png</li>");
            }
            System.out.println("\t</ul>");
        }
    }
}
