package com.vasquez;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventToPublish {
    private String type;
    private byte[] body;
}
