package com.spring2go.smile.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Status {
    private String hostname;
    private String serverAddress;
    private String clientAddress;
}
