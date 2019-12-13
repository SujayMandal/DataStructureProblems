import { Component, OnInit } from '@angular/core';
import { Subscription } from "rxjs/Subscription";
import { GridOptions } from "ag-grid";
import { ProcessorService } from "app/processor.service";
import { GridService } from "app/grid.service";
import { DatePipe } from "@angular/common";
import { FetchService } from "app/fetch.service";
import { DownloadService } from "app/download.service";

@Component({
  selector: 'app-fetch-block',
  templateUrl: './fetch-block.component.html',
  styleUrls: ['./fetch-block.component.scss']
})
export class FetchBlockComponent implements OnInit {

  clearFetchTabSubscription: Subscription;
  enableDownload: boolean = false;
  errorMsg: string = '';
  successMsg: string = '';
  errorFlag: boolean = false;
  successFlag: boolean = false;
  showSuccessMsg: boolean = true;
  response: Object = null;
  selectedDate: any = '';
  //grid variables
  columnDefs;
  gridOptions: GridOptions;
  rowData: Array<any> = [];
  gridReadyFlag: boolean = false;
  intViewportHeight: any;
  gridHeight: any;
  gridApi: any;
  viewWeek: String;

  constructor(private downloadService: DownloadService, private fetchService: FetchService, private fileProcessor: ProcessorService, private datePipe: DatePipe, private gridService: GridService) {

    if (window.location.pathname.toString().includes("/SOP")) {
      this.viewWeek = "SOP WeekN";
      this.columnDefs = [
        { headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        {
          headerName: "Latest List End Date", headerTooltip: "Latest List End Date", valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.listEndDateDtNn, 'MM/dd/yyyy');
          }
        },
        { headerName: "Latest Status", headerTooltip: "Latest Status", field: "mostRecentListStatus" },
        {
          headerName: "Date Of Last Reduction", headerTooltip: "Date Of Last Reduction", valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.listStrtDateDtNn, 'MM/dd/yyyy');
          }
        },
        {
          headerName: "Delivery Date", headerTooltip: "Delivery Date", valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.deliveryDate, 'MM/dd/yyyy');
          }
        },
        { headerName: "Vacant Status", headerTooltip: "Vacant Status", valueGetter: function transformDate(params) {
            return params.data.sellerOccupiedProperty.toUpperCase() === 'Y' ? 'N': params.data.sellerOccupiedProperty;
          }  
        },
        { headerName: "Reason", headerTooltip: "Reason", field: "exclusionReason", tooltipField: "exclusionReason" }
      ];
    } else {
      this.viewWeek = "WeekN"
      this.columnDefs = [
        { headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        {
          headerName: "Latest List End Date", headerTooltip: "Latest List End Date", valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.listEndDateDtNn, 'MM/dd/yyyy');
          }
        },
        { headerName: "Latest Status", headerTooltip: "Latest Status", field: "listSttsDtlsVc" },
        {
          headerName: "Date Of Last Reduction", headerTooltip: "Date Of Last Reduction", valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.listStrtDateDtNn, 'MM/dd/yyyy');
          }
        },
        {
          headerName: "Delivery Date", headerTooltip: "Delivery Date", valueGetter: function transformDate(params) {
            if (params.data.dpWeekNProcessStatus != null) {
              return datePipe.transform(params.data.dpWeekNProcessStatus.fetchedDateStr, 'MM/dd/yyyy');
            } else {
              return null;
            }
          }
        },
        { headerName: "SOP Status", headerTooltip: "SOP Status", field: "sellerOccupiedProperty" },
        { headerName: "Reason", headerTooltip: "Reason", field: "exclusionReason", tooltipField: "exclusionReason" }
      ];
    }

    this.clearFetchTabSubscription = this.gridService.getClearFetchTab().subscribe(flag => {
      if (flag) {
        this.enableDownload = false;
        this.errorMsg = '';
        this.successMsg = '';
        this.errorFlag = false;
        this.successFlag = false;
        this.showSuccessMsg = true;
        this.response = null;
      }
    });

  }

  onFetch() {

    this.onFetchInit();

    this.fetchService.fetchData(new Date(this.selectedDate).getTime() + 86400 * 1000 - 1, this.viewWeek).subscribe(response => {
      if (this.gridApi) {
        this.gridApi.api.sizeColumnsToFit();
      }
      if (!response.success) {
        this.errorFlag = true;
        this.errorMsg = response.message;
      } else {
        this.response = response.response;
        this.rowData = [];
        for (var columnEntry in response.response.columnEntries) {
          if (!this.viewWeek.includes("SOP")) {
            response.response.columnEntries[columnEntry].dpWeekNProcessStatus.fetchedDateStr = this.selectedDate;
          }
          this.rowData.push(response.response.columnEntries[columnEntry]);
        }
        this.successFlag = true;
        this.showSuccessMsg = false;
        this.successMsg = response.message;
        this.enableDownload = true;
      }
    }, error => {
      this.errorFlag = true;
      this.errorMsg = "Data Fetch failed";
    });

  }

  private onFetchInit() {
    this.intViewportHeight = window.innerHeight;
    this.gridHeight = this.intViewportHeight - 260;
    this.enableDownload = false;
    this.errorFlag = false;
    this.successFlag = false;
  }

  onDownload() {
    this.downloadService.downloadWeekNStep1(this.response, new Date(this.selectedDate).getTime() + 86400 * 1000 - 1, this.viewWeek);
  }

  ngOnInit() {
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params;
  }

  isDisabled() {
    if (this.selectedDate == "") {
      return true;
    } else {
      return false;
    }
  }

  onKeyInput() {
    return false;
  }

  ngOnDestroy() {
    if (this.clearFetchTabSubscription) {
      this.clearFetchTabSubscription.unsubscribe();
    }
  }

}
