package com.aether.gatekeeper.smart_gateway_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HeaderRule {
    private Long id;
    private String direction;
    private String action;
    private String headerName;
    private String headerValue;
}
