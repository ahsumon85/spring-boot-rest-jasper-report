package com.ahasan.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JasperReportDto {

	private String bgbLogoBase64String;
	private String hospitalName;
	private String hospitalAddress;
	private String othersInformation;
	private List<?> ResponeEntityList;

	public String getBgbLogoBase64String() {
		return bgbLogoBase64String;
	}

	public void setBgbLogoBase64String(String bgbLogoBase64String) {
		this.bgbLogoBase64String = bgbLogoBase64String;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getHospitalAddress() {
		return hospitalAddress;
	}

	public void setHospitalAddress(String hospitalAddress) {
		this.hospitalAddress = hospitalAddress;
	}

	public String getOthersInformation() {
		return othersInformation;
	}

	public void setOthersInformation(String othersInformation) {
		this.othersInformation = othersInformation;
	}

	public List<?> getResponeEntityList() {
		return ResponeEntityList;
	}

	public void setResponeEntityList(List<?> responeEntityList) {
		ResponeEntityList = responeEntityList;
	}

}
