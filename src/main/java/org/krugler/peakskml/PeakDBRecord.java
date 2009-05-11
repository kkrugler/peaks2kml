package org.krugler.peakskml;
import java.util.ArrayList;


public class PeakDBRecord {
    
    // "uid";"rank";"name_prefix";"name";"name_source";"summit_map_source";"summit_map_ref_type";"saddle_map_source";"saddle_map_ref_type";"summit_7d5_quad";"saddle_7d5_quad";"summit_10m_dem";"saddle_10m_dem";"saddle_elevation_source";"summit_map_meters";"summit_dem_meters";"saddle_map_meters";"saddle_dem_meters";"saddle_depth_altimeter_feet";"saddle_altimeter_margin_feet";"summit_easting";"summit_northing";"saddle_easting";"saddle_northing";"difficulty_class";"secor_3_page";"secor_3_ref_type";"secor_2_page";"secor_2_ref_type";"roper_page";"summit_post_id";"sps_list"
    // "1";"1";"Mt.";"Whitney";"official";"MOUNT WHITNEY, CA";"spot_elevation";;;"MOUNT WHITNEY, CA";;"MOUNT WHITNEY, CA";;"DEM_10m";"4417";"4413";"0";"4116";"0";"0";"384480";"4048720";"384670";"4048760";"1";"70";"full_listing";"70";"full_listing";"307";"150227/mount-whitney.html";"Emblem"

    private static final int UID_FIELD = 0;
    private static final int RANK_FIELD = 1;
    private static final int NAME_PREFIX_FIELD = 2;
    private static final int NAME_FIELD = 3;
    private static final int NAME_SOURCE_FIELD = 4;
    private static final int SUMMIT_MAP_SOURCE_FIELD = 5;
    private static final int SUMMIT_MAP_REF_TYPE_FIELD = 6;
    private static final int SADDLE_MAP_SOURCE_FIELD = 7;
    private static final int SADDLE_MAP_REF_TYPE_FIELD = 8;
    private static final int SUMMIT_7D5_QUAD_FIELD = 9;
    private static final int SADDLE_7D5_QUAD_FIELD = 10;
    private static final int SUMMIT_10M_DEM_FIELD = 11;
    private static final int SADDLE_10M_DEM_FIELD = 12;
    private static final int SADDLE_ELEVATION_SOURCE_FIELD = 13;
    private static final int SUMMIT_MAP_METERS_FIELD = 14;
    private static final int SUMMIT_DEM_METERS_FIELD = 15;
    private static final int SADDLE_MAP_METERS_FIELD = 16;
    private static final int SADDLE_DEM_METERS_FIELD = 17;
    private static final int SADDLE_DEPTH_ALTIMETER_FEET_FIELD = 18;
    private static final int SADDLE_ALTIMETER_MARGIN_FEET_FIELD = 19;
    private static final int SUMMIT_EASTING_FIELD = 20;
    private static final int SUMMIT_NORTHING_FIELD = 21;
    private static final int SADDLE_EASTING_FIELD = 22;
    private static final int SADDLE_NORTHING_FIELD = 23;
    private static final int DIFFICULTY_CLASS_FIELD = 24;
    private static final int SECOR_3_PAGE_FIELD = 25;
    private static final int SECOR_3_REF_TYPE_FIELD = 26;
    private static final int SECOR_2_PAGE_FIELD = 27;
    private static final int SECOR_2_REF_TYPE_FIELD = 28;
    private static final int ROPER_PAGE_FIELD = 29;
    private static final int SUMMIT_POST_ID_FIELD = 30;
    private static final int SPS_LIST_FIELD = 31;
    private static final int NUM_FIELDS = 32;
    

    private int _rank;
    private String _namePrefix;
    private String _name;
    private double _summitMapMeters;
    private double _summitEasting;
    private double _summitNorthing;
    private String _difficultyClass;
    private int _secor2Page;
    private String _summitPostId;
    private String _spsList;
    
    private int getIntFromField(String[] fields, int fieldIndex, String fieldName) {
        try {
            return Integer.parseInt(getStringFromField(fields, fieldIndex, fieldName));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Input line has invalid " + fieldName + ": " + fields[fieldIndex]);
        }
    }
    
    private double getDoubleFromField(String[] fields, int fieldIndex, String fieldName) {
        try {
            return Double.parseDouble(getStringFromField(fields, fieldIndex, fieldName));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Input line has invalid " + fieldName + ": " + fields[fieldIndex]);
        }
    }
    
    private String getStringFromField(String[] fields, int fieldIndex, String fieldName) {
        String text = fields[fieldIndex];
        if (text.length() == 0) {
            return "";
        } else if (text.equals("NULL")) {
            return "";
        } else if (!text.startsWith("\"") || !text.endsWith("\"")) {
            throw new RuntimeException("Input line has invalid " + fieldName + ": " + text);
        }
        
        return text.substring(1, text.length() - 1);
    }

    private static String[] splitOnChar(String str, char c) {
        ArrayList<String> result = new ArrayList<String>();
        
        int lastOffset = 0;
        int curOffset;
        while ((curOffset = str.indexOf(c, lastOffset)) != -1) {
            result.add(str.substring(lastOffset, curOffset));
            lastOffset = curOffset + 1;
        }
        
        result.add(str.substring(lastOffset));
        
        return result.toArray(new String[result.size()]);
    }

    public PeakDBRecord(String inputLine) {
        String[] fields = splitOnChar(inputLine, ';');
        if (fields.length != NUM_FIELDS) {
            throw new RuntimeException("Input line has wrong number of fields: " + inputLine);
        }
        
        _rank = getIntFromField(fields, RANK_FIELD, "rank");
        _namePrefix = getStringFromField(fields, NAME_PREFIX_FIELD, "name prefix");
        _name = getStringFromField(fields, NAME_FIELD, "name");
        _summitMapMeters = getDoubleFromField(fields, SUMMIT_MAP_METERS_FIELD, "summit map meters");
        _summitEasting = getDoubleFromField(fields, SUMMIT_EASTING_FIELD, "summit easting");
        _summitNorthing = getDoubleFromField(fields, SUMMIT_NORTHING_FIELD, "summit northing");
        _difficultyClass = getStringFromField(fields, DIFFICULTY_CLASS_FIELD, "difficulty class");
        _secor2Page = getIntFromField(fields, SECOR_2_PAGE_FIELD, "secore 2 page");
        _summitPostId = getStringFromField(fields, SUMMIT_POST_ID_FIELD, "summit post id");
        _spsList = getStringFromField(fields, SPS_LIST_FIELD, "sps list");
    }

    public int getRank() {
        return _rank;
    }

    public String getNamePrefix() {
        return _namePrefix;
    }

    public String getName() {
        return _name;
    }

    public double getSummitMapMeters() {
        return _summitMapMeters;
    }

    public double getSummitMapFeet() {
        return _summitMapMeters * 3.2808399;
    }
    public double getSummitEasting() {
        return _summitEasting;
    }

    public double getSummitNorthing() {
        return _summitNorthing;
    }

    public String getDifficultyClass() {
        return _difficultyClass;
    }

    public int getSecor2Page() {
        return _secor2Page;
    }
    
    public String getSummitPostId() {
        return _summitPostId;
    }

    public String getSpsList() {
        return _spsList;
    }
    
    public String getFullName() {
        return _namePrefix.length() == 0 ? _name : _namePrefix + " " + _name;
    }
}
