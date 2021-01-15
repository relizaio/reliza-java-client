package reliza.java.client.responses;

import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class SourceCodeEntryDetails {
    private UUID uuid;
    private String createdType;
    private String lastUpdatedBy;
    private String createdDate;
    private UUID branch;
    private UUID vcsRepositoryUuid;
    private String vcsBranch;
    private String commit;
    private String vcsTag;
    private String notes;
    private String org;
    private String dateActual;
}