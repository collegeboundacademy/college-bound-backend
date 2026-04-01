package com.collegebound.demo.college;

public class College {
    private String id;
    private String name;
    private String state;
    private String type;
    private Double acceptanceRate;
    private Integer averageNetPrice;
    private Double retentionRate;
    private Integer satMidpoint;
    private Integer actMidpoint;
    private Integer undergradEnrollment;
    private Double graduationRate4Year;
    private Integer earningsMedian6Yrs;
    private String website;
    private Double studentFacultyRatio;
    private Double firstGenerationShare;
    private Double partTimeShare;
    private Double menShare;
    private Double womenShare;
    private Double raceWhiteShare;
    private Double raceBlackShare;
    private Double raceHispanicShare;
    private Double raceAsianShare;
    private Double raceTwoOrMoreShare;
    private Double raceUnknownShare;

    public College(
            String id,
            String name,
            String state,
            String type,
            Double acceptanceRate,
            Integer averageNetPrice,
            Double retentionRate,
            Integer satMidpoint,
            Integer actMidpoint,
            Integer undergradEnrollment,
            Double graduationRate4Year,
            Integer earningsMedian6Yrs,
            String website,
            Double studentFacultyRatio,
            Double firstGenerationShare,
            Double partTimeShare,
            Double menShare,
            Double womenShare,
            Double raceWhiteShare,
            Double raceBlackShare,
            Double raceHispanicShare,
            Double raceAsianShare,
            Double raceTwoOrMoreShare,
            Double raceUnknownShare) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.type = type;
        this.acceptanceRate = acceptanceRate;
        this.averageNetPrice = averageNetPrice;
        this.retentionRate = retentionRate;
        this.satMidpoint = satMidpoint;
        this.actMidpoint = actMidpoint;
        this.undergradEnrollment = undergradEnrollment;
        this.graduationRate4Year = graduationRate4Year;
        this.earningsMedian6Yrs = earningsMedian6Yrs;
        this.website = website;
        this.studentFacultyRatio = studentFacultyRatio;
        this.firstGenerationShare = firstGenerationShare;
        this.partTimeShare = partTimeShare;
        this.menShare = menShare;
        this.womenShare = womenShare;
        this.raceWhiteShare = raceWhiteShare;
        this.raceBlackShare = raceBlackShare;
        this.raceHispanicShare = raceHispanicShare;
        this.raceAsianShare = raceAsianShare;
        this.raceTwoOrMoreShare = raceTwoOrMoreShare;
        this.raceUnknownShare = raceUnknownShare;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public Double getAcceptanceRate() {
        return acceptanceRate;
    }

    public Integer getAverageNetPrice() {
        return averageNetPrice;
    }

    public Double getRetentionRate() {
        return retentionRate;
    }

    public Integer getSatMidpoint() {
        return satMidpoint;
    }

    public Integer getActMidpoint() {
        return actMidpoint;
    }

    public Integer getUndergradEnrollment() {
        return undergradEnrollment;
    }

    public Double getGraduationRate4Year() {
        return graduationRate4Year;
    }

    public Integer getEarningsMedian6Yrs() {
        return earningsMedian6Yrs;
    }

    public String getWebsite() {
        return website;
    }

    public Double getStudentFacultyRatio() {
        return studentFacultyRatio;
    }

    public Double getFirstGenerationShare() {
        return firstGenerationShare;
    }

    public Double getPartTimeShare() {
        return partTimeShare;
    }

    public Double getMenShare() {
        return menShare;
    }

    public Double getWomenShare() {
        return womenShare;
    }

    public Double getRaceWhiteShare() {
        return raceWhiteShare;
    }

    public Double getRaceBlackShare() {
        return raceBlackShare;
    }

    public Double getRaceHispanicShare() {
        return raceHispanicShare;
    }

    public Double getRaceAsianShare() {
        return raceAsianShare;
    }

    public Double getRaceTwoOrMoreShare() {
        return raceTwoOrMoreShare;
    }

    public Double getRaceUnknownShare() {
        return raceUnknownShare;
    }
}
