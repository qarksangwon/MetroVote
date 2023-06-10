package com.inhatc.metrovote.api;

import java.util.Date;

public class SubwayArriveDTO {
    private String lineNum;
    private String frCode;
    private String stationCd;
    private String stationNm;
    private String trainNo;
    private Date arriveTime;
    private Date leftTime;
    private String originStation;
    private String destStation;
    private String subwaysName;
    private String subwayEName;
    private String weekTag;
    private String inoutTag;
    private String flFlag;
    private String expressYN;
    private String branchLine;

    public SubwayArriveDTO() {
        this.lineNum = "에러가 발생했습니다.";
        this.frCode = "";
        this.stationCd = "";
        this.stationNm = "";
        this.trainNo = "";
        this.arriveTime = new Date();
        this.leftTime = new Date();
        this.originStation = "";
        this.destStation = "";
        this.subwaysName = "";
        this.subwayEName = "";
        this.weekTag = "";
        this.inoutTag = "";
        this.flFlag = "";
        this.expressYN = "";
        this.branchLine = "";
    }

    public SubwayArriveDTO(String lineNum, String frCode, String stationCd, String stationNm, String trainNo, Date arriveTime, Date leftTime, String originStation, String destStation, String subwaysName, String subwayEName, String weekTag, String inoutTag, String flFlag, String expressYN, String branchLine) {
        this.lineNum = lineNum;
        this.frCode = frCode;
        this.stationCd = stationCd;
        this.stationNm = stationNm;
        this.trainNo = trainNo;
        this.arriveTime = arriveTime;
        this.leftTime = leftTime;
        this.originStation = originStation;
        this.destStation = destStation;
        this.subwaysName = subwaysName;
        this.subwayEName = subwayEName;
        this.weekTag = weekTag;
        this.inoutTag = inoutTag;
        this.flFlag = flFlag;
        this.expressYN = expressYN;
        this.branchLine = branchLine;
    }

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public String getFrCode() {
        return frCode;
    }

    public void setFrCode(String frCode) {
        this.frCode = frCode;
    }

    public String getStationCd() {
        return stationCd;
    }

    public void setStationCd(String stationCd) {
        this.stationCd = stationCd;
    }

    public String getStationNm() {
        return stationNm;
    }

    public void setStationNm(String stationNm) {
        this.stationNm = stationNm;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public Date getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Date arriveTime) {
        this.arriveTime = arriveTime;
    }

    public Date getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(Date leftTime) {
        this.leftTime = leftTime;
    }

    public String getOriginStation() {
        return originStation;
    }

    public void setOriginStation(String originStation) {
        this.originStation = originStation;
    }

    public String getDestStation() {
        return destStation;
    }

    public void setDestStation(String destStation) {
        this.destStation = destStation;
    }

    public String getSubwaysName() {
        return subwaysName;
    }

    public void setSubwaysName(String subwaysName) {
        this.subwaysName = subwaysName;
    }

    public String getSubwayEName() {
        return subwayEName;
    }

    public void setSubwayEName(String subwayEName) {
        this.subwayEName = subwayEName;
    }

    public String getWeekTag() {
        return weekTag;
    }

    public void setWeekTag(String weekTag) {
        this.weekTag = weekTag;
    }

    public String getInoutTag() {
        return inoutTag;
    }

    public void setInoutTag(String inoutTag) {
        this.inoutTag = inoutTag;
    }

    public String getFlFlag() {
        return flFlag;
    }

    public void setFlFlag(String flFlag) {
        this.flFlag = flFlag;
    }

    public String getExpressYN() {
        return expressYN;
    }

    public void setExpressYN(String expressYN) {
        this.expressYN = expressYN;
    }

    public String getBranchLine() {
        return branchLine;
    }

    public void setBranchLine(String branchLine) {
        this.branchLine = branchLine;
    }
}
