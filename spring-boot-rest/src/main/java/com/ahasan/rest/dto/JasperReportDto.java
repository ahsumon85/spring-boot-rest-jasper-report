package com.ahasan.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JasperReportDto {

	private String reportName;
	private List<?> responseEntityList;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public List<?> getResponseEntityList() {
		return responseEntityList;
	}

	public void setResponseEntityList(List<?> responseEntityList) {
		this.responseEntityList = responseEntityList;
	}

	
}
