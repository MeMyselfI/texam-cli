package de.agentsinaction.texam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultWrapper {
    public boolean success;
    public int total;
    public JsonNode root;
}
