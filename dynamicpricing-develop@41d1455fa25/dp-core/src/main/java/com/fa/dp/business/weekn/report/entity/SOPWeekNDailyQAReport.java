package com.fa.dp.business.weekn.report.entity;

import com.fa.dp.business.weekn.run.status.entity.SOPWeekNDailyRunStatus;
import com.fa.dp.business.weekn.run.status.entity.WeekNDailyRunStatus;
import com.fa.dp.core.entityaudit.domain.AbstractAuditable;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Setter
@Getter
@Table(name = "SOP_WEEKN_DAILY_QA_REPORT")
public class SOPWeekNDailyQAReport extends AbstractAuditable{
    private static final long serialVersionUID = 2636557093837729053L;
    @Column(name = "SELR_PROP_ID_VC_NN")
    private String selrPropIdVcNn;
    @Column(name = "RBID_PROP_ID_VC_PK")
    private String rbidPropIdVcPk;
    @Column(name = "OLD_PROP_ID")
    private String oldPropId;
    @Column(name = "OLD_LOAN_NUMBER")
    private String oldLoanNumber;
    @Column(name = "REO_PROP_STTS_VC")
    private String reoPropSttsVc;
    @Column(name = "PROP_SOLD_DATE_DT")
    private String propSoldDateDt;
    @Column(name = "PROP_STTS_ID_VC_FK")
    private String propSttsIdVcFk;
    @Column(name = "RBID_PROP_LIST_ID_VC_PK")
    private String rbidPropListIdVcPk;
    @Column(name = "LIST_TYPE_ID_VC_FK")
    private String listTypeIdVcFk;
    @Column(name = "PREVIOUS_LIST_START_DATE")
    private LocalDate previousListStartDate;
    @Column(name = "PREVIOUS_LIST_END_DATE")
    private LocalDate previousListEndDate;
    @Column(name = "PREVIOUS_LIST_PRICE")
    private String previousListPrice;
    @Column(name = "CURRENT_LIST_START_DATE")
    private LocalDate currentListStartDate;
    @Column(name = "CURRENT_LIST_END_DATE")
    private LocalDate currentListEndDate;
    @Column(name = "LIST_PRCE_NT")
    private String listPriceNt;
    @Column(name = "LIST_STTS_DTLS_VC")
    private String listSttsDtlsVc;
    @Column(name = "OCCPNCY_STTS_AT_LST_CREATN")
    private String occpncySttsAtLstCreatn;
    @Column(name = "ACTUAL_LIST_CYCLE")
    private String actualListCycle;
    @Column(name = "WEEKN_RECOMMENDED_LIST_PRICE_REDUCTION")
    private String weeknRecommendedListPriceReduction;
    @Column(name = "WEEKN_RECOMMENDED_DATE")
    private String weeknRecommendedDate;
    @Column(name = "WEEKN_EXCLUSION_REASON")
    private String weeknExclusionReason;
    @Column(name = "PCT_PRICE_CHANGE_FRM_LAST_LIST")
    private String pctPriceChangeFrmLastList;
    @Column(name = "RULE_VIOLATION")
    private String ruleViolation;
    @Column(name = "WEEKN_MISSINGREPORT")
    private String weeknMissingreport;
    @Column(name = "CLASSIFICATION")
    private String classification;
    @Column(name = "STATUS")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "RUN_STATUS_ID")
    private SOPWeekNDailyRunStatus sopWeekNDailyRunStatus;
}




