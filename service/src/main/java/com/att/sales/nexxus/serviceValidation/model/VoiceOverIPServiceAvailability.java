package com.att.sales.nexxus.serviceValidation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class VoiceOverIPServiceAvailability {
 private String rateCenter;
 private String rateCenterState;
 private Boolean numberAvailabilityIndicator;
 private String lnsSwitchCLLI;
 private Boolean numberPortabilityIndicator;
 private String voipAvailabilityFlag;
 private String voipE911AvailabilityFlag;
 private String voipLocalAvailabilityFlag;
 private String e911AvailabilityFlag;
 private String ipldAvailabilityIndicator;
 private String taxGeoCode;
 private Boolean ipTollFreeIndicator;
}
