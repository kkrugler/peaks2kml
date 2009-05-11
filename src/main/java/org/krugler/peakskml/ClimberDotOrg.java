package org.krugler.peakskml;

public class ClimberDotOrg {
    // These need to be in order from common to uncommon, so that
    // we get rid of "mountain" before getting rid of "north". That
    // way "North Mountain" => "North" instead of "Mountain".

    // They also have to be long to short (currently) since I'm just searching for
    // substrings, so "mountain" has to come before "mount"
    final static String[] MODIFIERS = {
            "north",
            "south",
            "east",
            "west",

            "peak",
            "peaks",
            "mountain",
            "mount",
            "mt",
            "mtn",
            "hill",

            "middle",

            "black",
            "white",
            "red",
            "blue",

            "big",
            "little",
            "the",
            "el",

            "crest",
            "dome",
            "ridge",
            "needle",
            "col",
            "minaret",
            "spire",
            "spires",
            "point",
            "mesa",
            "butte",
            "plateau",
            "wings",

            "creek",
            "spring"
    };

    public static String makeUrl(String name) {
        String minimizedName = name.toLowerCase();

        // Climber.org doesn't have UTM
        if (minimizedName.startsWith("utm")) {
            return "";
        }

        // Get rid of punctuation
        minimizedName = minimizedName.replaceAll("'", "");
        minimizedName = minimizedName.replaceAll("\\.", "");
        minimizedName = minimizedName.replaceAll("\\(", "");
        minimizedName = minimizedName.replaceAll("\\)", "");
        minimizedName = minimizedName.trim();

        // Get rid of modifiers (mount, north, south, east, west, middle, red, etc) as long
        // as that leaves something else behind.
        boolean modified;
        do {
            modified = false;
            for (String modifier : MODIFIERS) {
                if (minimizedName.equals(modifier)) {
                    // Don't get rid of "north" if the name was "North Peak", which then got
                    // shortened to "North".
                    continue;
                } else if (minimizedName.startsWith(modifier + " ")) {
                    minimizedName = minimizedName.substring(modifier.length() + 1);
                    modified = true;
                    break;
                } else if (minimizedName.endsWith(" " + modifier)) {
                    minimizedName = minimizedName.substring(0, minimizedName.length() - (modifier.length() + 1));
                    modified = true;
                    break;
                }
            }
        } while (modified);

        // Get rid of residual gaps between remaining words.
        minimizedName = minimizedName.replaceAll(" ", "");

        if (minimizedName.length() == 0) {
            throw new RuntimeException("Invalid name: " + name);
        }

        // http://climber.org/TripReports/ByPeakR.html#ritter
        return "http://climber.org/TripReports/ByPeak" + minimizedName.substring(0, 1).toUpperCase() + ".html#" + minimizedName;

    }
}
