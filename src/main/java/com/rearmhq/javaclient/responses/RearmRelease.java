package com.rearmhq.javaclient.responses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Minimal projection of a ReARM {@code Release}. Holds the fields most CI
 * callers need; extend if you need richer detail (artifacts, parent releases,
 * timing, etc.).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RearmRelease {
	private UUID uuid;
	private String version;
	private String lifecycle;
	private UUID org;
	private UUID component;
	private UUID branch;
	private UUID sourceCodeEntry;
	private RearmSourceCodeEntry sourceCodeEntryDetails;
	private String endpoint;
}
