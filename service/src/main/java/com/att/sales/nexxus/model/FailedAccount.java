package com.att.sales.nexxus.model;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FailedAccount   {
  @JsonProperty("accountNumber")
  private String accountNumber = null;

  @JsonProperty("mcn")
  private String mcn = null;

  @JsonProperty("reasonCode")
  private String reasonCode = null;

  @JsonProperty("billMonth")
  private List<String> billMonth = null;


  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getMcn() {
    return mcn;
  }

  public void setMcn(String mcn) {
    this.mcn = mcn;
  }

  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public List<String> getBillMonth() {
    return billMonth;
  }

  public void setBillMonth(List<String> billMonth) {
    this.billMonth = billMonth;
  }

}

