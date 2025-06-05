package com.streamer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class StreamerTop3Fixer {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Load input JSON
        JsonNode root = mapper.readTree(new File("Input.json"));
        Set<String> usedTop3Streamers = new HashSet<>();

        // Use LinkedHashMap to maintain order of sections
        Map<String, List<String>> simplifiedOutput = new LinkedHashMap<>();

        for (JsonNode section : root) {
            String sectionID = section.get("sectionID").asText();
            ArrayNode sectionData = (ArrayNode) section.get("sectionData");

            // Extract all streamers
            List<JsonNode> allStreamers = new ArrayList<>();
            for (JsonNode streamer : sectionData) {
                allStreamers.add(streamer);
            }

            // Build new unique top 3
            List<JsonNode> newTop3 = new ArrayList<>();
            Set<String> currentTop3IDs = new HashSet<>();

            for (JsonNode streamer : allStreamers) {
                String id = streamer.get("streamerID").asText();
                if (!usedTop3Streamers.contains(id) && currentTop3IDs.add(id)) {
                    newTop3.add(streamer);
                    if (newTop3.size() == 3)
                        break;
                }
            }

            // Fill remaining if less than 3
            if (newTop3.size() < 3) {
                for (JsonNode streamer : allStreamers) {
                    String id = streamer.get("streamerID").asText();
                    if (currentTop3IDs.add(id)) {
                        newTop3.add(streamer);
                        if (newTop3.size() == 3)
                            break;
                    }
                }
            }

            // Build full ordered list: newTop3 + remaining
            List<String> orderedStreamerIDs = new ArrayList<>();
            Set<JsonNode> topSet = new HashSet<>(newTop3);
            for (JsonNode node : newTop3) {
                orderedStreamerIDs.add(node.get("streamerID").asText());
                usedTop3Streamers.add(node.get("streamerID").asText());
            }
            for (JsonNode node : allStreamers) {
                if (!topSet.contains(node)) {
                    orderedStreamerIDs.add(node.get("streamerID").asText());
                }
            }

            simplifiedOutput.put(sectionID, orderedStreamerIDs);
        }

        // Save simplified output to Output.json
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("Output.json"), simplifiedOutput);
    }
}
