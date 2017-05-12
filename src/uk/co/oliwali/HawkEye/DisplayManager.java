package uk.co.oliwali.HawkEye;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages displaying of search results. Includes utilities for handling pages of results
 *
 * @author oliverw92
 */
public class DisplayManager {

    private static String toJson(String text, String color, String additionalParameter) {
        text.replace("\"", ""); // TODO: escape properly
        String ret = "{\"text\":\"" + text + "\",\"color\":\"" + color + "\"";
        if(additionalParameter != null) ret += "," + additionalParameter;
        ret += "}";
        return ret;
    }
    
    private static String toJson(String text, String color) {
        return toJson(text, color, null);
    }

    /**
     * Displays a page of data from the specified {@link PlayerSession} search results.
     * Contains appropriate methods for detecing errors e.g. no results
     *
     * @param session {@link PlayerSession}
     * @param page    page number to display
     */
    public static void displayPage(PlayerSession session, int page) {

        //Check if any results are found
        List<DataEntry> results = session.getSearchResults();
        if (results == null || results.size() == 0) {
            Util.sendMessage(session.getSender(), "&cNo results found");
            return;
        }

        //Work out max pages. Return if page is higher than max pages
        int maxLines = 6;
        int maxPages = (int) Math.ceil((double) results.size() / 6);

        if (page > maxPages || page < 1)
            return;

        //Calculates how many pixels we should fill in with '-'. 255 = max, 42 = other characters
        int fillPixels = (255 - (42 + (Integer.toString(page).length() * 5) + (Integer.toString(maxPages).length() * 5))) / 2;

        StringBuilder lineBuilder = new StringBuilder();

        for (; fillPixels >= 0; fillPixels -= 5) {
            lineBuilder.append("-");
        }

        String line = lineBuilder.toString();

        //Begin displaying page
        List<String> jsonBuilder = new ArrayList<>();

        jsonBuilder.add(toJson(line + " ", "dark_gray"));
        jsonBuilder.add(toJson("Page (", "gray"));
        jsonBuilder.add(toJson(String.valueOf(page), "red"));
        jsonBuilder.add(toJson("/", "gray"));
        jsonBuilder.add(toJson(String.valueOf(maxPages), "red"));
        jsonBuilder.add(toJson(") ", "gray"));
        jsonBuilder.add(toJson(line + "\n", "dark_gray"));
        //Util.sendMessage(session.getSender(), "&8" + line + " &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8" + line);

        for (int i = (page - 1) * maxLines; i < ((page - 1) * maxLines) + maxLines; i++) {
            if (i == results.size())
                break;
            DataEntry entry = results.get(i);

            String time = Util.getTime(entry.getTimestamp());

            jsonBuilder.add(toJson(" ID:", "red"));
            jsonBuilder.add(toJson(String.valueOf(entry.getDataId()), "red", "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/he tpto " + String.valueOf(entry.getDataId()) + "\"}"));
            jsonBuilder.add(toJson(" " + time, "gray"));
            jsonBuilder.add(toJson(" " + entry.getPlayer(), "red"));
            jsonBuilder.add(toJson(" " + entry.getType().getConfigName() + "\n", "gray"));

            jsonBuilder.add(toJson(" Loc: ", "red"));
            jsonBuilder.add(toJson(entry.getWorld() + " " + entry.getX() + "," + entry.getY() + "," + entry.getZ() + " ", "gray"));
            jsonBuilder.add(toJson("Data: ", "red"));
            jsonBuilder.add(toJson(entry.getStringData() + "\n", "gray"));
        }
        jsonBuilder.add(toJson("-----------------------------------------------------", "dark_gray"));
        StringBuilder jsonStr = new StringBuilder();
        for(int i = 0; i < jsonBuilder.size(); i++) {
            if(i > 0) jsonStr.append(",");
            jsonStr.append(jsonBuilder.get(i));
        }
        String jsonString = "[\"\"," + jsonStr.toString() + "]";
        Util.sendJsonMessage(session.getSender(), jsonString);
    }

}
