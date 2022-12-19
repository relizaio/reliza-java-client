package reliza.java.client.responses;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * Superclass of all data objects
 */
@Data
public class RelizaDataParent {
	private ProgrammaticType createdType;
	private UUID lastUpdatedBy;
	private ZonedDateTime createdDate;
	
	public enum ProgrammaticType {
		MANUAL,
		AUTO,
		MANUAL_AND_AUTO,
		API,
		TEST
	}
}