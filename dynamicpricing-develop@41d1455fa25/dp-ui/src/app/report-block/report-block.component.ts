import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { SearchService } from "app/search.service";
import { Subscription } from "rxjs/Subscription";
import { GridService } from "app/grid.service";
import { DatePipe } from "@angular/common";
import { BsModalRef, BsModalService } from "ngx-bootstrap/modal";
import { ReportviewService } from 'app/reportview.service';
import { ReportQaFilter } from "app/reportqa-filter";
import * as Excel from 'exceljs';
import * as fs from 'file-saver';
import { LoaderService } from "app/loader.service";

@Component({
  selector: 'app-report-block',
  templateUrl: './report-block.component.html',
  styleUrls: ['./report-block.component.scss']
})

export class ReportBlockComponent implements OnInit {
  clearQaReportTabSubscription: Subscription;
  switchWeek0Subscription: Subscription;
  switchWeekNSubscription: Subscription;
  switchPermanentSubscription: Subscription;

  selectedDateStart: any = '';
  selectedDateEnd: any = '';

  reportType:string ='';
  error: string = '';
  successMsg: string = '';
  showSuccessMsg: boolean = true;
  errorFlag: boolean = false;
  successFlag: boolean = false;

  downloadFlagN : boolean = false;
  downloadFlag0 : boolean = false;
  downloadFlagPerm : boolean = false;



  columnDefs;
  intViewportHeight: any;
  gridReadyFlag: boolean = false;
  excelData:any;

  permData:any;
  week0Data:any;

  gridHeight: any;
  gridApi: any;
  gridMaxHeight: any;
  gridMinHeight: any;

  enableDownload: boolean = false;
  response: Object = null;
  rowData: Array<any> = [];
  temp: Array<any> = [];

  occupDropdownSettings: any;
  possibleOccupancy: String[];
  selectedOccupant: String[];


  clientDropdownSettings: any;
  possibleClients: String[];
  selectedClient: String[];
  clientMap: Map<String, String>;
  reportFilter: ReportQaFilter;
  summaryRow:any;

  week0DataGrid : boolean = false;
  weekNDataGrid : boolean = false;
  permDataGrid: boolean =false;
  showWeekN : boolean =false;
  dateFlag : boolean =false;

  constructor(private reportService: ReportviewService, private gridService: GridService, private datePipe: DatePipe, private modalService: BsModalService, private loaderService: LoaderService) {

    this.possibleOccupancy = ['Vacant', 'SOP'];
    this.selectedOccupant = ['Vacant'];

    this.possibleClients = ['NRZ', 'OCN', 'PHH'];
    this.selectedClient = ['NRZ', 'OCN', 'PHH'];

    this.clientMap = new Map();
    this.clientMap.set("NRZ", "NRZ");
    this.clientMap.set("OCN", "OCN");
    this.clientMap.set("PHH", "PHH");



    this.occupDropdownSettings = {
      singleSelection: true,
      allowSearchFilter: false,
      closeDropDownOnSelection: true
    };

    this.clientDropdownSettings = {
      singleSelection: false,
      selectAllText: 'All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 3,
      allowSearchFilter: false
    }
    this.weekNDataGrid=true;
    this.createWeek0Grid();

    this.switchWeek0Subscription=this.gridService.getShowWeek0DataGrid().subscribe(flag =>{
      if(flag){
        this.weekNDataGrid=false;
        this.week0DataGrid=true;
        this.permDataGrid=false;
        console.log("Week0");
        this.columnDefs = [
          { headerName: "ASSET_NUMBER", field: "assetNumber" },
          { headerName: "PROP_TEMP", field: "propTemp" },
          { headerName: "OLD_ASSET_NUMBER", field: "oldAssetNumber" },
          { headerName: "CLIENT_CODE", field: "clientCode" },
          { headerName: "STATUS", field: "status" },
          { headerName: "ASSET_VALUE", field: "assetValue" },
          { headerName: "AV_SET_DATE", field: "avSetDate" },
          { headerName: "LIST_PRICE", field: "listPrice" },
          { headerName: "CLASSIFICATION", field: "classification" },
          { headerName: "ELIGIBLE", field: "eligible" },
          { headerName: "ASSIGNMENT", field: "assignment" },
          { headerName: "WEEK_0_PRICE", field: "week0Price" },
          { headerName: "STATE", field: "state" },
          { headerName: "RT_SOURCE", field: "rtSource" },
          { headerName: "NOTES", field: "notes" },
          { headerName: "PROPERTY_TYPE",  field: "propertyType" },
          { headerName: "WITHIN BUSINESS RULES",  field: "withinBusinessRules" },
          { headerName: "PctOfAV", field: "pctAV" },
          { headerName: "ASSIGNMENT_DATE", field: "assignmentDate" }

        ];
      }
    });

    this.switchWeekNSubscription=this.gridService.getShowWeekNDataGrid().subscribe(flag =>{
      if(flag){
        this.createWeek0Grid();
      }
    });
   /*  this.switchPermanentSubscription=this.gridService.getShowPermanentReportTab().subscribe(flag =>{
      if(flag){
        console.log("Permanent");
        this.weekNDataGrid=false;
        this.week0DataGrid=false;
        this.permDataGrid=true;
        this.columnDefs = [
          { headerName: "Property Id", field: "" },
          { headerName: "RR Loan Number", field: "" },
          { headerName: "EQ Loan Number", field: "" },
          { headerName: "Classification", field: "" },
          { headerName: "Exclusion Reason", field: "" },
          { headerName: "Updated Date", field: "" }
        ];
      }
    }); */

    this.clearQaReportTabSubscription = this.reportService.getClearQaReportTab().subscribe(flag => {
      if (flag) {
        this.enableDownload = false;
        this.selectedDateStart='';
        this.selectedDateEnd='';
        this.selectedOccupant = ['Vacant'];
        this.selectedClient = ['NRZ', 'OCN', 'PHH'];
        this.error = '';
        this.successMsg = '';
        this.errorFlag = false;
        this.successFlag = false;
        this.showSuccessMsg = true;
        this.dateFlag = false;
        this.response = null;
      }
    });



  }
createWeek0Grid(){
  console.log("WeekN");
  this.weekNDataGrid=true;
  this.week0DataGrid=false;
  this.permDataGrid=false;
  this.showWeekN =true;
  this.columnDefs = [
    { headerName: "SELR_PROP_ID_VC_NN", field: "selrPropIdVcNn" },
    { headerName: "RBID_PROP_ID_VC_PK", field: "rbidPropIdVcPk" },
    { headerName: "REO_PROP_STTS_VC", field: "reoPropSttsVc" },
    { headerName: "PROP_SOLD_DATE_DT", field: "propSoldDateDt" },
    { headerName: "PROP_STTS_ID_VC_FK", field: "propSttsIdVcFk" },
    { headerName: "RBID_PROP_LIST_ID_VC_PK", field: "rbidPropListIdVcPk" },
    { headerName: "LIST_TYPE_ID_VC_FK", field: "listTypeIdVcFk" },
    { headerName: "PREVIOUS_LIST_STRT_DATE", field: "previousListStartDate" },
    { headerName: "PREVIOUS_LIST_END_DATE", field: "previousListEndDate" },
    { headerName: "PREVIOUS_LIST_PRICE", field: "previousListPrice" },
    { headerName: "CURRENT_LIST_STRT_DATE", field: "currentListStartDate" },
    { headerName: "CURRENT_LIST_END_DATE", field: "currentListEndDate" },
    { headerName: "LIST_STTS_DTLS_VC", field: "listSttsDtlsVc" },
    { headerName: "OCCPNCY_STTS_AT_LST_CREATN", field: "occpncySttsAtLstCreatn" },
    { headerName: "ACTUAL_LIST_CYCLE", field: "actualListCycle" },
    { headerName: "WEEKN_RECOMMENDED_LIST_PRICE_REDUCTION",  field: "weeknRecommendedListPriceReduction" },
    { headerName: "WEEKN_RECOMMENDED_DATE",  field: "weeknRecommendedDate" },
    { headerName: "WEEKN_EXCLUSION_REASON", field: "weeknExclusionReason" },
    { headerName: "PCT_PRICE_CHANGE_FRM_LAST_LIST", field: "pctPriceChangeFrmLastList" },
    { headerName: "RULE_VIOLATION",  field: "ruleViolation" },
    { headerName: "WEEKN_MISSINGREPORT", field: "weeknMissingreport" },
    { headerName: "CLASSIFICATION", field: "classification" }

  ];
}
  checkDateDiff() {
      if (this.selectedDateStart != '' && this.selectedDateEnd != '') {

       var stDate = new Date(this.selectedDateStart);
       var enDate = new Date(this.selectedDateEnd);

       if( stDate > enDate){
        this.error ="End Date must be later than Start Date ";
        this.errorFlag = true;
        this.successFlag = false;
        this.dateFlag =false;
      //  return false;
       }
       else{
       var diff = Math.abs(stDate.getTime() - enDate.getTime());
       var diffDays = Math.ceil(diff / (1000 * 3600 * 24));
       if (diffDays > 60) {
         this.error = "Start & End Date selection range should be max 2 months";
         this.errorFlag = true;
         this.successFlag = false;
         this.dateFlag =false;
       //  return false;
       }
       else {
         this.dateFlag = true;
         this.errorFlag = false;
         this.error ='';
         //return true;
       }
      }
     }
  }


  private onSearchInit() {
    this.intViewportHeight = window.innerHeight;
    this.gridMaxHeight = this.intViewportHeight - 280;
    this.gridMinHeight = this.intViewportHeight - 292;
    this.enableDownload = false;
    this.errorFlag = false;
    this.successFlag = false;
  }

  onSearch() {
    this.onSearchInit();

    this.intViewportHeight = window.innerHeight;
    this.gridMaxHeight = this.intViewportHeight - 280;
    this.gridMinHeight = this.intViewportHeight - 292;


    this.reportFilter = new ReportQaFilter();
    this.reportFilter.fromDate = null;
    this.reportFilter.toDate = null;


    this.errorFlag = false;
    this.reportFilter.fromDate = this.selectedDateStart;
    this.reportFilter.toDate = this.selectedDateEnd;


    this.reportFilter.occupancy = this.selectedOccupant;

    this.reportFilter.clients = [];
    for (var status in this.selectedClient) {
      this.reportFilter.clients.push(this.clientMap.get(this.selectedClient[status]));
    }

// add the weekN / Week0 true /false condition check her
if( this.weekNDataGrid && !this.week0DataGrid){
  console.log("=========Block of WeekN Data===============");
  this.reportService.getQaReptDetails(this.reportFilter).subscribe(response => {
    if (this.gridApi) {
      this.gridApi.api.sizeColumnsToFit();
    }
    if (!response.success) {
      this.error = response.message;
      this.errorFlag = true;
      this.successFlag = false;
      this.downloadFlagN = false;

    } else {
      this.successFlag = true;
      this.rowData = [];
      this.reportType="weekN";
      this.excelData=this.iterateData(this.reportType,response.response);
      this.downloadFlagN = true;
      for (var columnEntry in this.excelData) {
        this.rowData.push(this.excelData[columnEntry]);
      }
    }
  }, error => {
    this.error = "Search failed";
    this.errorFlag = true;
  });
}
else if(!this.weekNDataGrid && this.week0DataGrid){
console.log("=========Block of Week0 Data===============");
this.reportService.getQaWeek0ReptDetails(this.reportFilter).subscribe(response => {
  if (this.gridApi) {
    this.gridApi.api.sizeColumnsToFit();
  }
  if (!response.success) {
    this.error = response.message;
    this.errorFlag = true;
    this.successFlag = false;

  } else {
    this.successFlag = true;
    this.rowData = [];
    this.downloadFlag0=true;
    this.reportType="week0";
    this.week0Data=this.iterateData(this.reportType,response.response.week0Reports);
    var summaryJson={};
    this.temp=[];
    summaryJson['propertyCount']=response.response.propertyCount;
    summaryJson['minimumPctAv']=response.response.minimumPctAv;
    summaryJson['medianPctAv']=response.response.medianPctAv;
    summaryJson['maximumPctAv']=response.response.maximumPctAv;
    summaryJson['voilationCount']=response.response.voilationCount;
    summaryJson['missingReportCount']=response.response.missingReportCount;
    this.temp.push(summaryJson);
   this.summaryRow=this.temp;
    console.log(this.summaryRow);
    for (var columnEntry in response.response.week0Reports) {
      this.rowData.push(response.response.week0Reports[columnEntry]);
    }
  }
}, error => {
  this.error = "Search failed";
  this.errorFlag = true;
});
}
/* else if(!this.week0DataGrid && !this.weekNDataGrid && this.permDataGrid){
  console.log("Under construction");
  this.reportService.getPermReptExclusDetails(this.reportFilter).subscribe(response => {
    if (this.gridApi) {
      this.gridApi.api.sizeColumnsToFit();
    }
    if (!response.success) {
      this.error = response.message;
      this.errorFlag = true;
      this.successFlag = false;
      this.downloadFlagPerm=false;

    } else {
      this.successFlag = true;
      this.downloadFlagPerm=true;
      this.rowData = [];
      this.permData=response.response;
      this.downloadFlagN = true;
      for (var columnEntry in  this.permData) {
        this.rowData.push( this.permData[columnEntry]);
      }
      console.log("Rowdata : "+this.rowData);
    }
  }, error => {
    this.error = "Search failed";
    this.errorFlag = true;
  });
} */

  }

  iterateData(reportType,data){
    if(reportType=='weekN'){
    for (var key in data) {
     if (data.hasOwnProperty(key)) {
      if(data[key]['weeknRecommendedDate'] != null){
          let temp=data[key]['weeknRecommendedDate'];
        let myDate =new Date(parseInt(temp,10));
        data[key]['weeknRecommendedDate']= myDate.getFullYear() + '-' +('0' + (myDate.getMonth()+1)).slice(-2)+ '-' +  ('0' + myDate.getDate()).slice(-2);
      }
     }
    }
        return data;
   }
   else if(reportType=='week0'){
    for (var key in data) {
      if (data.hasOwnProperty(key)) {
       if(data[key]['assignmentDate'] != null){
        /*  let temp=data[key]['assignmentDate'];
         let myDate =new Date(parseInt(temp,10));
         data[key]['assignmentDate']= myDate.getFullYear() + '-' +('0' + (myDate.getMonth()+1)).slice(-2)+ '-' +  ('0' + myDate.getDate()).slice(-2);
      */
        let temp=data[key]['assignmentDate'];
        let myDate =new Date(parseInt(temp,10)).toLocaleString("en-US", {timeZone: "America/New_York"});
        var date= myDate.toLocaleString().split(',')[0]; 
        var dateParts = date.split('/'); 
        data[key]['assignmentDate']=dateParts[2] + "-" + ('0'+ dateParts[0]).slice(-2) + "-" + ('0'+ dateParts[1]).slice(-2);
       }
      }
     }
         return data;
   }

};
  ngOnInit() {
  }

  onDownload() {
    if(this.downloadFlagN){
    this.loaderService.display(true);
    const workbook = new Excel.Workbook();

    workbook.creator = 'Me';
    workbook.lastModifiedBy = 'Me';
    workbook.created = new Date();
    workbook.modified = new Date();
    var worksheet = workbook.addWorksheet("Report");


   worksheet.columns=[
      { header: "SELR_PROP_ID_VC_NN", key: "selrPropIdVcNn" },
      { header: "RBID_PROP_ID_VC_PK", key: "rbidPropIdVcPk" },
      { header: "REO_PROP_STTS_VC", key: "reoPropSttsVc" },
      { header: "PROP_SOLD_DATE_DT", key: "propSoldDateDt" },
      { header: "PROP_STTS_ID_VC_FK", key: "propSttsIdVcFk" },
      { header: "RBID_PROP_LIST_ID_VC_PK", key: "rbidPropListIdVcPk" },
      { header: "LIST_TYPE_ID_VC_FK", key: "listTypeIdVcFk" },
      { header: "PREVIOUS_LIST_STRT_DATE", key: "previousListStartDate" },
      { header: "PREVIOUS_LIST_END_DATE", key: "previousListEndDate" },
      { header: "PREVIOUS_LIST_PRICE", key: "previousListPrice" },
      { header: "CURRENT_LIST_STRT_DATE", key: "currentListStartDate" },
      { header: "CURRENT_LIST_END_DATE", key: "currentListEndDate" },
      { header: "LIST_STTS_DTLS_VC", key: "listSttsDtlsVc" },
      { header: "OCCPNCY_STTS_AT_LST_CREATN", key: "occpncySttsAtLstCreatn" },
      { header: "ACTUAL_LIST_CYCLE", key: "actualListCycle" },
      { header: "WEEKN_RECOMMENDED_LIST_PRICE_REDUCTION",  key: "weeknRecommendedListPriceReduction" },
      { header: "WEEKN_RECOMMENDED_DATE",  key: "weeknRecommendedDate" },
      { header: "WEEKN_EXCLUSION_REASON", key: "weeknExclusionReason" },
      { header: "PCT_PRICE_CHANGE_FRM_LAST_LIST", key: "pctPriceChangeFrmLastList" },
      { header: "RULE_VIOLATION",  key: "ruleViolation" },
      { header: "WEEKN_MISSINGREPORT", key: "weeknMissingreport" },
      { header: "CLASSIFICATION", key: "classification" }
    ];
    worksheet.columns.forEach(column => {
      column.width = column.header.length < 12 ? 12 : 25;
    })
    var firstRow = worksheet.getRow(1);

    firstRow.font = { name: 'New Times Roman', family: 4, size: 10, bold: true};
    firstRow.alignment = { vertical: 'middle', horizontal: 'center'};
    firstRow.height = 20;

    worksheet.addRows(this.excelData);
    const filename= this.selectedOccupant[0] +"_WeekN_QAReport-"+this.selectedDateStart+"-to-"+this.selectedDateEnd;
    try{
     workbook.xlsx.writeBuffer().then(function (data) {
       var blob = new Blob([data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
       fs.saveAs(blob, filename);
       });
      }
      catch(e){
       console.log("Some error occurred during the xlxs file download ");
    }

    finally{
     this.loaderService.display(false);
    }
  }
else  if(this.downloadFlag0){
  console.log("downloading week0");
  this.loaderService.display(true);
  const workbook = new Excel.Workbook();

  workbook.creator = 'Admin';
  workbook.lastModifiedBy = 'Admin';
  workbook.created = new Date();
  workbook.modified = new Date();
  var worksheet1 = workbook.addWorksheet("Summary");
  var worksheet2 = workbook.addWorksheet("Week0");

  worksheet1.columns=[
    {header: "Total Number of Properties", key: "propertyCount" },
    {header: "Min Ratio btw Initial Price and AV", key: "minimumPctAv" },
    {header: "Median Ratio btw Initial Price and AV", key: "medianPctAv" },
    {header: "Max Ratio btw Initial Price and AV", key: "maximumPctAv" },
    {header: "# of Violations", key: "voilationCount" },
    {header: "# of Missing Reports", key: "missingReportCount" }

  ];


  worksheet2.columns=[
      { header: "ASSET_NUMBER", key: "assetNumber" },
      { header: "PROP_TEMP", key: "propTemp" },
      { header: "OLD_ASSET_NUMBER", key: "oldAssetNumber" },
      { header: "CLIENT_CODE", key: "clientCode" },
      { header: "STATUS", key: "status" },
      { header: "ASSET_VALUE", key: "assetValue" },
      { header: "AV_SET_DATE", key: "avSetDate" },
      { header: "LIST_PRICE", key: "listPrice" },
      { header: "CLASSIFICATION", key: "classification" },
      { header: "ELIGIBLE", key: "eligible" },
      { header: "ASSIGNMENT", key: "assignment" },
      { header: "WEEK_0_PRICE", key: "week0Price" },
      { header: "STATE", key: "state" },
      { header: "RT_SOURCE", key: "rtSource" },
      { header: "NOTES", key: "notes" },
      { header: "PROPERTY_TYPE",  key: "propertyType" },
      { header: "WITHIN BUSINESS RULES",  key: "withinBusinessRules" },
      { header: "PctOfAV", key: "pctAV" },
      { header: "ASSIGNMENT_DATE", key: "assignmentDate" }
  ];
  worksheet1.columns.forEach(column => {
    column.width = column.header.length < 12 ? 12 : 25;
  })
  var firstRowSummarytab = worksheet1.getRow(1);

  firstRowSummarytab.font = { name: 'New Times Roman', family: 4, size: 10, bold: true};
  firstRowSummarytab.alignment = { vertical: 'middle', horizontal: 'center'};
  firstRowSummarytab.height = 20;

  worksheet2.columns.forEach(column => {
    column.width = column.header.length < 12 ? 12 : 25;
  })
  var firstRow = worksheet2.getRow(1);

  firstRow.font = { name: 'New Times Roman', family: 4, size: 10, bold: true};
  firstRow.alignment = { vertical: 'middle', horizontal: 'center'};
  firstRow.height = 20;

  worksheet2.addRows(this.week0Data);
  worksheet1.addRows(this.summaryRow);
  const filename= this.selectedOccupant[0] +"_Week0_QA-"+this.selectedDateStart+"-to-"+this.selectedDateEnd;
  try{
   workbook.xlsx.writeBuffer().then(function (data) {
     var blob = new Blob([data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
     fs.saveAs(blob, filename);
     });
    }
    catch(e){
     console.log("Some error occurred during the xlxs file download ");
  }

  finally{
   this.loaderService.display(false);
  }

}
/* else  if(this.downloadFlagPerm){
  console.log("donwloading permData");
  this.loaderService.display(true);
  const workbook = new Excel.Workbook();

  workbook.creator = 'Admin';
  workbook.lastModifiedBy = 'Admin';
  workbook.created = new Date();
  workbook.modified = new Date();
  var worksheet = workbook.addWorksheet("PemanReportExclsn");


 worksheet.columns=[
    { header: "Property Id", key: "" },
    { header: "RR Loan Number ", key: "" },
    { header: "EQ Loan Number", key: "" },
    { header: "Classification", key: "" },
    { header: "Exclusion Reason", key: "" },
    { header: "Updated Date", key: "" }
  ];
  worksheet.columns.forEach(column => {
    column.width = column.header.length < 12 ? 12 : 25;
  })
  var firstRow = worksheet.getRow(1);

  firstRow.font = { name: 'New Times Roman', family: 4, size: 10, bold: true};
  firstRow.alignment = { vertical: 'middle', horizontal: 'center'};
  firstRow.height = 20;

  worksheet.addRows(this.excelData);
  const filename= this.selectedOccupant[0] +"_PermanentExclusion-"+this.selectedDateStart+"-to-"+this.selectedDateEnd;
  try{
   workbook.xlsx.writeBuffer().then(function (data) {
     var blob = new Blob([data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
     fs.saveAs(blob, filename);
     });
    }
    catch(e){
     console.log("Some error occurred during the xlxs file download ");
  }

  finally{
   this.loaderService.display(false);
  }
} */
  }


  onKeyInput() {
    return false;
  }


  getToday(): string {
    return new Date().toISOString().split('T')[0];
  }

  isDisabled() {
    if (!this.dateFlag || (this.selectedOccupant.length == 0) || (this.selectedClient.length == 0)) {
      return true;
    } else {
      return false;
    }
  }

  ngOnDestroy() {
    if (this.clearQaReportTabSubscription) {
      this.clearQaReportTabSubscription.unsubscribe();
    }
    if(this.switchWeekNSubscription){
      this.switchWeekNSubscription.unsubscribe();
    }
    if(this.switchWeek0Subscription){
      this.switchWeek0Subscription.unsubscribe();
    }
  }
}

