package reliza.java.client.responses;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class TimingDetails {
    private String lifecycle;
    private String dateFrom;
    private String dateTo;
    private String event;
    private String duration;
}