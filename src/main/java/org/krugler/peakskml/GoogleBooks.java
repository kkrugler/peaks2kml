package org.krugler.peakskml;

public class GoogleBooks {
    public static String makeUrl(int secor2Page, String peakName) {
        if (secor2Page == 0) {
            return "";
        }
        
        // http://books.google.com/books?id=R6mcvANvKasC&pg=PA261&lpg=PA261&dq=%22Rambaud+Pass%22
        StringBuilder result = new StringBuilder();
        result.append("http://books.google.com/books?id=R6mcvANvKasC&pg=PA");
        result.append(secor2Page);
        result.append("&lpg=PA");
        result.append(secor2Page);
        result.append("&dq=%22");
        
        // TODO - figure out more rigorous mapping from full peak name to
        // names used by Secor in his book.
        String secorName = peakName.replaceFirst("Mt\\.", "Mount").replaceAll(" ", "+");
        result.append(secorName);
        result.append("%22");
        
        return result.toString();
    }
}
