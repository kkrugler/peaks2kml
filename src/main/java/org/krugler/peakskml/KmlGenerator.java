package org.krugler.peakskml;
import geotransform.coords.Gdc_Coord_3d;
import geotransform.coords.Utm_Coord_3d;
import geotransform.ellipsoids.CC_Ellipsoid;
import geotransform.transforms.Utm_To_Gdc_Converter;

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class KmlGenerator {
    private static final String WHITE_ICON = "http://maps.google.com/mapfiles/kml/pushpin/wht-pushpin.png";
    private static final String GREEN_ICON = "http://maps.google.com/mapfiles/kml/pushpin/grn-pushpin.png";
    private static final String YELLOW_ICON = "http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png";
    private static final String RED_ICON = "http://maps.google.com/mapfiles/kml/pushpin/red-pushpin.png";
    
    private static final RatingStyle[] RATINGS = {
        new RatingStyle("", "classUnknown", WHITE_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("1", "class1", GREEN_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("1s3", "class1s3", GREEN_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("2", "class2", GREEN_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("2-3", "class2-3", YELLOW_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("3", "class3", YELLOW_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("3s4", "class3s4", YELLOW_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("3-4", "class3-4", RED_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("4", "class4", RED_ICON, new Color(255, 255, 255), 1.0),
        new RatingStyle("5", "class5", RED_ICON, new Color(255, 255, 255), 1.0),
    };
    
    private static class RatingStyle {
        private String _ratingName;
        private String _styleName;
        private String _iconUrl;
        private Color _color;
        private double _scale;
        
        public RatingStyle(String ratingName, String styleName, String iconUrl, Color color,
                double scale) {
            _ratingName = ratingName;
            _styleName = styleName;
            _iconUrl = iconUrl;
            _color = color;
            _scale = scale;
        }

        public String getRatingName() {
            return _ratingName;
        }

        public String getStyleName() {
            return _styleName;
        }

        public String getIconUrl() {
            return _iconUrl;
        }
        
        public Color getColor() {
            return _color;
        }

        public double getScale() {
            return _scale;
        }
        
    }
    
    private static RatingStyle mapRatingName(String ratingName) {
        for (RatingStyle rs : RATINGS) {
            if (ratingName.equalsIgnoreCase(rs.getRatingName())) {
                return rs;
            }
        }

        throw new RuntimeException("Invalid or unknown rating: " + ratingName);
    }
    
    private static String mapColorToHex(Color color) {
        // Format is <alpha><blue><green><red>
        return String.format("ff%02x%02x%02x", color.getBlue(), color.getGreen(), color.getRed());
    }
    
    // Set label scale on each entry, where scale = ((h - b)/b * 9) + 1 and
    // h = height, b = base height (13,000).
    private static double mapHeightToScale(double height) {
        final double baseHeight = 13000;
        return 1.0 + (9.0 * ((height - baseHeight)/baseHeight));
    }
    
    private static String makeSnippet(PeakDBRecord peak) {
        StringBuilder result = new StringBuilder();
        
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(0);
        result.append(formatter.format(peak.getSummitMapFeet()));
        result.append("ft");
        result.append(" / ");
        result.append(formatter.format(peak.getSummitMapMeters()));
        result.append("m");
        result.append('\r');
        result.append("Class ");
        if (peak.getDifficultyClass().length() != 0) {
            result.append(peak.getDifficultyClass());
        } else {
            result.append("<unknown>");
        }
        return result.toString();
    }
    
    private static String makeRank(int rank) {
        final String[] rankEndings = {
                "th",   // 20th
                "st",   // 21st
                "nd",   // 22nd
                "rd",   // 23rd
                "th",   // 24th
                "th",
                "th",
                "th",
                "th",
                "th"
        };
        
        if (rank == 1) {
            return "Highest";
        } else if ((rank > 10) && (rank < 14)) {
            return "" + rank + "th highest";
        } else {
            return "" + rank + rankEndings[rank % 10] + " highest";
        }
    }
    
    private static String makeDescription(PeakDBRecord peak) {
        StringBuilder result = new StringBuilder();

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(0);

        result.append("Elevation ");
        result.append(formatter.format(peak.getSummitMapFeet()));
        result.append("ft. ");

        result.append(makeRank(peak.getRank()));
        result.append(" peak in California. ");

        // TODO KKr - make this something like
        // Easiest route to summit is class 2<link> (summit is class 3<link>).
        // Or class 2<link> to class 3 <link>
        result.append("Easiest route to summit is ");
        if (peak.getDifficultyClass().length() != 0) {
            result.append("class ");
            result.append(peak.getDifficultyClass());
        } else {
            result.append("unknown");
        }
        result.append(". ");
        
        String spsRating = peak.getSpsList();
        if (spsRating.length() > 0) {
            // TODO KKr - add link to SPS peak list.
            result.append("This is an SPS ");
            result.append(spsRating);
            result.append( " peak. ");
        }

        result.append("Other sources of information:");
        result.append("<ul>");
        result.append("<li>");
        result.append("<a href=\"http://www.vulgarianramblers.org/peak_detail.php?peak_name=");
        result.append(peak.getName());
        result.append("\">VulgarianRamblers.org</a>");
        result.append("</li>");

        String summitPostId = peak.getSummitPostId();
        if (summitPostId.length() > 0) {
            result.append("<li>");
            result.append("<a href=\"http://www.summitpost.org/mountain/rock/");
            result.append(summitPostId);
            result.append("\">SummitPost.org</a>");
            result.append("</li>");
        }

        String climberDotOrgUrl = ClimberDotOrg.makeUrl(peak.getName());
        if (climberDotOrgUrl.length() > 0) {
            result.append("<li>");
            result.append("<a href=\"");
            result.append(climberDotOrgUrl);
            result.append("\">Climber.org</a>");
            result.append("</li>");
        }
        
        String googleBookUrl = GoogleBooks.makeUrl(peak.getSecor2Page(), peak.getFullName());
        if (googleBookUrl.length() > 0) {
            result.append("<li>");
            result.append("<a href=\"");
            result.append(googleBookUrl);
            result.append("\">Secor 2nd Edition (Google Books)</a>");
            result.append("</li>");
        }
        
        result.append("</ul>");

        return result.toString();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String inputFilename = args[0];
        String outputFilename = args[1];
        
        final byte utmZone = 11;
        final byte utmZoneShasta = 10;
        
        try {
            List<String> lines = FileUtils.readLines(new File(inputFilename), "UTF-8");
            
            File outputFile = new File(outputFilename);
            
            PrintWriter pr = new PrintWriter(outputFile, "UTF-8");
            pr.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pr.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
            pr.println("\t<Document>");
            
            for (RatingStyle rs : RATINGS) {
                pr.println("\t\t<Style id=\"" + rs.getStyleName() + "\">");
                
                pr.println("\t\t\t<LabelStyle>");
                pr.println("\t\t\t\t<color>" + mapColorToHex(rs.getColor()) + "</color>");
                pr.println("\t\t\t\t<colorMode>normal</colorMode>");
                pr.println("\t\t\t\t<scale>" + rs.getScale() + "</scale>");
                pr.println("\t\t\t</LabelStyle>");
                
                pr.println("\t\t\t<IconStyle>");
                pr.println("\t\t\t\t<scale>1.0</scale>");
                pr.println("\t\t\t\t<Icon>");
                pr.println("\t\t\t\t\t<href>" + rs.getIconUrl() + "</href>");
                pr.println("\t\t\t\t</Icon>");
                pr.println("\t\t\t</IconStyle>");
                
                pr.println("\t\t</Style>");
            }

            Utm_To_Gdc_Converter.Init(new CC_Ellipsoid());
            boolean headerLine = true;
            
            for (String line : lines) {
                if (headerLine) {
                    headerLine = false;
                    continue;
                }
                
                PeakDBRecord peak = new PeakDBRecord(line);
                
                boolean isShasta = peak.getName().equalsIgnoreCase("shasta");
                
                double easting = peak.getSummitEasting();
                double northing = peak.getSummitNorthing();
                
                Utm_Coord_3d utm = new Utm_Coord_3d(easting, northing, 0.0, isShasta ? utmZoneShasta : utmZone, true);
                Gdc_Coord_3d location = new Gdc_Coord_3d();
                Utm_To_Gdc_Converter.Convert(utm, location);
                
                // Arghh - I'm using the Clarke 1866 ellipsoid, since that's what NAD27 is based on, but
                // I think there's also a shift that occurs because of NAD27 using a different center (in
                // Kansas). Adjust for average error determined from comparing my results to
                // http://gis.wvdep.org/convert/llutm_conus.php
                double adjustedLongitude = location.longitude - 0.000925046;
                double adjustedLatitude = location.latitude - 0.0000651;
                                
                pr.println("\t\t<Placemark>");
                
                pr.println("\t\t\t<name>" + peak.getFullName() + "</name>");
                
                // Set up style URL
                pr.println("\t\t\t<styleUrl>#" + mapRatingName(peak.getDifficultyClass()).getStyleName() + "</styleUrl>");

                // Set up direct style
//                pr.println("\t\t\t<Style>");
//                pr.println("\t\t\t\t<LabelStyle>");
//                pr.println("\t\t\t\t\t<color>ffffffff</color>");
//                pr.println("\t\t\t\t\t<colorMode>normal</colorMode>");
//                pr.println("\t\t\t\t\t<scale>" + mapHeightToScale(peak.getSummitMapFeet()) + "</scale>");
//                pr.println("\t\t\t\t</LabelStyle>");
//                pr.println("\t\t\t</Style>");
                
                pr.println("\t\t\t<Snippet maxLines=\"2\" >");
                pr.println("\t\t\t\t" + makeSnippet(peak));
                pr.println("\t\t\t</Snippet>");
    
                pr.println("\t\t\t<description>");
                pr.println("\t\t\t\t<![CDATA[");
                pr.println("\t\t\t\t\t" + makeDescription(peak));
                pr.println("\t\t\t\t]]>");
                pr.println("\t\t\t</description>");

                pr.println("\t\t\t<Point>");
                pr.println("\t\t\t\t<coordinates>" + adjustedLongitude + "," + adjustedLatitude + "</coordinates>");
                pr.println("\t\t\t</Point>");
                pr.println("\t\t</Placemark>");

            }
            
            pr.println("\t</Document>");
            pr.println("</kml>");
            pr.flush();
        } catch (Throwable t) {
            System.err.println("Exception processing file: " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }

}
