import { Component, OnInit } from '@angular/core';
import { GridOptions } from "ag-grid";
import { Subscription } from "rxjs/Subscription";
import { FileSelected } from "app/file-selected";
import { GridService } from "app/grid.service";
import { DownloadService } from "app/download.service";

@Component({
  selector: 'app-output-grid',
  templateUrl: './output-grid.component.html',
  styleUrls: ['./output-grid.component.scss']
})
export class OutputGridComponent implements OnInit {

  fileDetailInfoSubscription: Subscription;
  fileDetailInfo: any;
  errorFlag: boolean = false;
  error: String = '';
  weekType: String;
  //grid variables
  columnDefs;
  gridOptions: GridOptions;
  rowData: Array<any> = [];
  gridReadyFlag: boolean = false;
  gridApi: any;
  intViewportHeight: any;
  gridHeight: any;
  //map
  commandFilterMap: Map<String, String>;

  constructor(private gridService: GridService, private downloader: DownloadService) {

    this.commandFilterMap = new Map();
    this.commandFilterMap.set("week0RRClassification", "Client Mismatch");
    this.commandFilterMap.set("week0DuplicateFilter", "Dup Asset");
    this.commandFilterMap.set("week0InvestorCodeFilter", "SS");
    this.commandFilterMap.set("week0AssetValueFilter", "Unsupp AV");
    this.commandFilterMap.set("week0RRRtngAggregator", "RR/RTNG Failure");
    this.commandFilterMap.set("week0PropertyTypeFilter", "Unsupp PropType");
    this.commandFilterMap.set("week0RAInputPayload", "Model Fail");
    this.commandFilterMap.set("weekNFetchData", "Hubzu Failure");
    this.commandFilterMap.set("weekNOddListingsFilter", "Odd Listing");
    this.commandFilterMap.set("weekNAssignmentFilter", "Assignment");
    this.commandFilterMap.set("weekNZipStateFilter", "Unsupp State");
    this.commandFilterMap.set("weekNSSPmiFilter", "SS & PMI");
    this.commandFilterMap.set("weekNSOPFilter", "SOP");
    this.commandFilterMap.set("weekNRAIntegrarion", "Model Fail");
    this.commandFilterMap.set("SopWeek0DuplicateFilter", "Dup Asset");
    this.commandFilterMap.set("SopWeek0AssetValueFilter", "Unsupp AV");
    this.commandFilterMap.set("SOPWeekNRAIntegrarion", "Model Fail");
    this.commandFilterMap.set("SOPWeekNActiveListingsFilter", "Active Listings");
    this.commandFilterMap.set("SOPWeekNSuccessfulUnderreviewFilter", "Successful / Underreview");
    this.commandFilterMap.set("SOPWeekNAssignmentFilter", "Assignment");
    this.commandFilterMap.set("SOPWeekNOddListingsFilter", "Odd Listing");
    this.commandFilterMap.set("SOPWeekNSSPmiFilter", "SS & PMI");
    this.commandFilterMap.set("SOPWeekNStateFilter", "Unsupp State");
    this.commandFilterMap.set("sopWeekNFetchData", "Hubzu Failure");
    this.commandFilterMap.set("SOPWeekNPast12CyclesFilter", "Past 12 Cycles");
    this.commandFilterMap.set("SOPWeekNSOPFilter", "Vacant");
    this.commandFilterMap.set("weekNPast12CyclesFilter", "Past 12 Cycles");

    if (window.location.pathname.toString().includes("/week0")) {
      this.weekType = "week0";
      this.columnDefs = [
        { headerName: "Asset #", headerTooltip: "Asset #", field: "assetNumber" },
        { headerName: "Client Code", headerTooltip: "Client Code", field: "clientCode" },
        { headerName: "Status", headerTooltip: "Status", field: "status" },
        { headerName: "Asset Value", headerTooltip: "Asset Value", field: "assetValue" },
        { headerName: "AV set date", headerTooltip: "AV set date", field: "avSetDate" },
        { headerName: "List Price (106% of AV)", headerTooltip: "List Price (106% of AV)", field: "listPrice" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        { headerName: "Property Type", headerTooltip: "Property Type", field: "propertyType" },
        { headerName: "Loan Status", headerTooltip: "Loan Status", valueGetter: this.deriveStatus.bind(this) }
      ];
    } else if (window.location.pathname.toString().includes("/weekN")) {
      this.weekType = "weekN";
      this.columnDefs = [
        { headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        { headerName: "State", headerTooltip: "State", field: "state" },
        { headerName: "Zip Code", headerTooltip: "Zip Code", field: "zipCode" },
        { headerName: "SS", headerTooltip: "SS", field: "clientCode" },
        { headerName: "PMI", headerTooltip: "PMI", field: "privateMortgageInsurance" },
        { headerName: "SOP", headerTooltip: "SOP", field: "sellerOccupiedProperty" },
        { headerName: "Status", headerTooltip: "Status", valueGetter: this.deriveStatus.bind(this) }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweek0")) {
      this.weekType = "sopWeek0";
      this.columnDefs = [
        { headerName: "Asset #", headerTooltip: "Asset #", field: "assetNumber" },
        { headerName: "Status", headerTooltip: "Status", field: "status" },
        { headerName: "Asset Value", headerTooltip: "Asset Value", field: "assetValue" },
        { headerName: "AV set date", headerTooltip: "AV set date", field: "avSetDate" },
        { headerName: "List Price (106% of AV)", headerTooltip: "List Price (106% of AV)", field: "listPrice" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        { headerName: "Property Type", headerTooltip: "Property Type", field: "propertyType" },
        { headerName: "Reason", headerTooltip: "Reason", valueGetter: this.deriveSOPStatus.bind(this) }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweekN")) {
      this.weekType = "sopWeekN";
      this.columnDefs = [
        { headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        { headerName: "State", headerTooltip: "State", field: "state" },
        { headerName: "Zip Code", headerTooltip: "Zip Code", field: "zipCode" },
        { headerName: "SS", headerTooltip: "SS", field: "clientCode" },
        { headerName: "PMI", headerTooltip: "PMI", field: "privateMortgageInsurance" },
        { headerName: "SOP", headerTooltip: "SOP", field: "sellerOccupiedProperty" },
        { headerName: "Status", headerTooltip: "Status", valueGetter: this.deriveSOPStatus.bind(this) }
      ];
    }

    this.gridOptions = {

      // PROPERTIES - object properties, myRowData and myColDefs are created somewhere in your application
      columnDefs: this.columnDefs,

      // PROPERTIES - simple boolean / string / number properties
      enableFilter: true,
      enableColResize: true,
      enableSorting: true,
      suppressMenuHide: true,
      headerHeight: 40,

      // EVENTS - add event callback handlers
      onGridSizeChanged: this.resizeColumns.bind(this)
      // CALLBACKS
    }

    this.fileDetailInfoSubscription = this.gridService.getFileDetailInfo().subscribe(fileDetailInfo => {
      if (this.gridApi) {
        this.gridApi.api.sizeColumnsToFit();
      }
      this.intViewportHeight = window.innerHeight;
      this.gridHeight = this.intViewportHeight - 192;
      this.fileDetailInfo = fileDetailInfo;
      this.rowData = [];
      for (var columnEntry in this.fileDetailInfo) {
        this.rowData.push(this.fileDetailInfo[columnEntry]);
      }
    });
  }

  deriveStatus(params) {
    if (params.data.command == null) {
      return "Processed"
    } else {
      return this.commandFilterMap.get(params.data.command.name);
    }
  }

  deriveSOPStatus(params) {
    if (params.data.failedStepCommandName == null) {
      return "Processed"
    } else {
      return this.commandFilterMap.get(params.data.failedStepCommandName.substring(3));
    }
  }

  onDownload() {
    this.errorFlag = false;
    if (this.weekType == "week0") {
      this.downloader.downloadFile(this.fileDetailInfo[0].dynamicPricingFilePrcsStatus.id, this.weekType);
    } else if (this.weekType == "weekN") {
      this.downloader.downloadFile(this.fileDetailInfo[0].dpWeekNProcessStatus.id, this.weekType);
    } else if (this.weekType == "sopWeek0") {
      this.downloader.downloadFile(this.fileDetailInfo[0].sopWeek0ProcessStatus.id, this.weekType);
    } else if (this.weekType == "sopWeekN") {
      this.downloader.downloadFile(this.fileDetailInfo[0].sopWeekNProcessStatus.id, this.weekType);
    }
  }

  resizeColumns(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params;
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if (this.fileDetailInfoSubscription) {
      this.fileDetailInfoSubscription.unsubscribe();
    }
  }

}
