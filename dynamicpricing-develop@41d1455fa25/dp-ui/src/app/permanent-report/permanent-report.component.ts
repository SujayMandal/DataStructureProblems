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
  selector: 'app-permanent-report',
  templateUrl: './permanent-report.component.html',
  styleUrls: ['./permanent-report.component.scss']
})
export class PermanentReportComponent implements OnInit {

  clientDropdownSettings: any;
  possibleClients: String[];
  selectedClient: String[];
  clientMap: Map<String, String>;
  error: string = '';

  switchPermanentSubscription: Subscription;
  clearQaReportTabSubscription:Subscription;

  columnDefs;
  gridHeight: any;
  gridApi: any;
  gridMaxHeight: any;
  gridMinHeight: any;

  intViewportHeight: any;
  gridReadyFlag: boolean = false;
  errorFlag: boolean = false;
  successFlag: boolean = false;
  enableDownload : boolean = false;
  reportFilter: ReportQaFilter;
  permanentData:any;
  rowData: Array<any> = [];
  
  constructor(private reportService: ReportviewService, private gridService: GridService, private loaderService: LoaderService) {
    this.possibleClients = ['NRZ', 'OCN', 'PHH'];
    this.selectedClient = ['NRZ', 'OCN', 'PHH'];

    this.clientMap = new Map();
    this.clientMap.set("NRZ", "NRZ");
    this.clientMap.set("OCN", "OCN");
    this.clientMap.set("PHH", "PHH");

    this.clientDropdownSettings = {
      singleSelection: false,
      selectAllText: 'All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 3,
      allowSearchFilter: false
    }

    this.switchPermanentSubscription=this.gridService.getShowPermanentReportTab().subscribe(flag =>{
      if(flag){
        this.columnDefs = [
          { headerName: "Property Id", field: "propertyId" },
          { headerName: "RR Loan Number", field: "rrLoanNumber" },
          { headerName: "EQ Loan Number", field: "eqLoanNumber" },
          { headerName: "Classification", field: "classiication" },
          { headerName: "Exclusion Reason", field: "exclusionReason" },
          { headerName: "Updated Date", field: "updateDate" }
        ];
      }
    });

    this.clearQaReportTabSubscription = this.reportService.getClearQaReportTab().subscribe(flag => {
      if (flag) {
        this.enableDownload = false;
        this.selectedClient = ['NRZ', 'OCN', 'PHH'];
        this.error = '';
        this.errorFlag = false;
        this.successFlag = false;
      }
    });

 }

  ngOnInit() {
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

    console.log(this.selectedClient);
     this.reportService.getPermReptExclusDetails(this.selectedClient).subscribe(response => {
      if (this.gridApi) {
        this.gridApi.api.sizeColumnsToFit();
      }
      if (!response.success) {
        this.error = response.message;
        this.errorFlag = true;
        this.successFlag = false;
        this.enableDownload = false;
  
      } else {
        this.successFlag = true;
        this.rowData = [];
        this.enableDownload = true;
        this.permanentData= response.response;
        if(this.permanentData ==null || this.permanentData=='')
        {
          console.log("size 0");
          this.enableDownload = false;
        }
        else{
        for (var columnEntry in  this.permanentData) {
          this.rowData.push( this.permanentData[columnEntry]);
        }
      }
      }
    }, error => {
      this.error = "Search failed";
      this.errorFlag = true;
    }); 
  }
  isDisabled() {
    if (this.selectedClient.length == 0) {
      return true;
    } else {
      return false;
    }
  }
  
  onDownload(){
    console.log("donwloading permData");
    this.loaderService.display(true);
    const workbook = new Excel.Workbook();
  
    workbook.creator = 'Admin';
    workbook.lastModifiedBy = 'Admin';
    workbook.created = new Date();
    workbook.modified = new Date();
    var worksheet = workbook.addWorksheet("PemanReportExclsn");
  
  
   worksheet.columns=[
      { header: "Property Id", key: "propertyId" },
      { header: "RR Loan Number ", key: "rrLoanNumber" },
      { header: "EQ Loan Number", key: "eqLoanNumber" },
      { header: "Classification", key: "classiication" },
      { header: "Exclusion Reason", key: "exclusionReason" },
      { header: "Updated Date", key: "updateDate" }
    ];
    worksheet.columns.forEach(column => {
      column.width = column.header.length < 12 ? 12 : 25;
    })
    var firstRow = worksheet.getRow(1);
  
    firstRow.font = { name: 'New Times Roman', family: 4, size: 10, bold: true};
    firstRow.alignment = { vertical: 'middle', horizontal: 'center'};
    firstRow.height = 20;
  
    worksheet.addRows(this.permanentData);
    const filename= "PermanentExclusion";
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
  ngOnDestroy() {
    if (this.switchPermanentSubscription) {
      this.switchPermanentSubscription.unsubscribe();
    }
    if (this.clearQaReportTabSubscription) {
      this.clearQaReportTabSubscription.unsubscribe();
    }
  }
}
