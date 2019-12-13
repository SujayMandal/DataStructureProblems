import { Component, OnInit } from '@angular/core';
import { GridOptions } from "ag-grid";
import { DashboardService } from "app/dashboard.service";
import { DatePipe } from "@angular/common";
import { Subscription } from "rxjs/Subscription";
import { GridService } from "app/grid.service";
import { FilterCountComponent } from "app/filter-count/filter-count.component";
import { Props } from "app/props";
import { FormControl } from "@angular/forms";
import { Observable } from "rxjs/Observable";
import { map } from "rxjs/operator/map";
import { DownloadService } from "app/download.service";
import { SearchFilter } from "app/search-filter";

@Component({
  selector: 'app-dashboard-block',
  templateUrl: './dashboard-block.component.html',
  styleUrls: ['./dashboard-block.component.scss']
})
export class DashboardBlockComponent implements OnInit {

  loadDashboardGridFlagSubscription: Subscription;
  errorMsg: string[] = [];
  successMsg: string = '';
  errorFlag: boolean = false;
  error: String;
  successFlag: boolean = false;
  myDateRange: any;
  fromDate: any = '';
  toDate: any = '';
  //grid variables
  columnDefs;
  gridOptions: GridOptions;
  rowData: Array<any> = [];
  gridReadyFlag: boolean = false;
  gridApi: any;
  intViewportHeight: any;
  gridMaxHeight: any;
  gridMinHeight: any;
  //dropdown variables
  fileName: String[];
  fileNameSet: Set<String>;
  dropdownFilesList: String[];
  selectedStatus: String[];
  statusDropdownSettings: any;
  fileNameDropdownSettings: any;
  statusMap: Map<String, String>;
  possibleStatus: String[];
  weekType: String;
  searchFilter: SearchFilter;

  constructor(private dashboardService: DashboardService, private datePipe: DatePipe, private gridService: GridService,
    private downloader: DownloadService) {

    this.fileNameDropdownSettings = {
      singleSelection: true,
      allowSearchFilter: true,
      searchPlaceholderText: 'Search file',
      closeDropDownOnSelection: true
    };

    this.statusMap = new Map();
    this.statusMap.set("Successful", "SUCCESSFUL");
    this.statusMap.set("In Progress", "IN_PROGRESS");
    this.statusMap.set("Uploaded", "UPLOADED");
    this.statusMap.set("Partial", "PARTIAL");
    this.statusMap.set("Failed", "FAILED");

    this.statusDropdownSettings = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 2,
      allowSearchFilter: false,
      limitSelection: 4
    };

    if (window.location.pathname.toString().includes("/week0")) {
      this.weekType = "week0";
      this.possibleStatus = ['Successful', 'In Progress', 'Uploaded', 'Partial', 'Failed'];
      this.columnDefs = [
        { headerName: "File Name", headerTooltip: "File Name", field: "inputFileName", checkboxSelection: true, width: 85 },
        { headerName: "Status", headerTooltip: "Status", field: "status", minWidth: 95, maxWidth: 95 },
        { headerName: "Total", headerTooltip: "Total", field: "totalAssets", suppressFilter: true, suppressSorting: true, minWidth: 55, maxWidth: 55 },
        { headerName: "Client Mismatch", headerTooltip: "Client Mismatch", field: "classificationMismatchCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 70, maxWidth: 70 },
        { headerName: "Dup Asset", headerTooltip: "Dup Asset", field: "duplicateCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "SS", headerTooltip: "SS", field: "ssInvestorCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "Unsupp AV", headerTooltip: "Unsupp AV", field: "unsupportedAssetCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 60, maxWidth: 60 },
        { headerName: "RR/RTNG Failure", headerTooltip: "RR/RTNG Failure", field: "failedRealResolOrRealCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 70, maxWidth: 70 },
        { headerName: "Unsupp PropType", headerTooltip: "Unsupp PropType", field: "unsupportedPropertyCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 70, maxWidth: 70 },
        { headerName: "Model Fail", headerTooltip: "Model Fail", field: "raFailedCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 55, maxWidth: 55 },
        { headerName: "Processed", headerTooltip: "Processed", field: "processedListCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 75, maxWidth: 75 },
        {
          headerName: "Last Updated", headerTooltip: "Last Updated", minWidth: 130, maxWidth: 130, valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.uploadTimestamp, 'MM/dd/yyyy HH:mm:ss');
          }
        }
      ];
    } else if (window.location.pathname.toString().includes("/weekN")) {
      this.weekType = "weekN";
      this.possibleStatus = ['Successful', 'In Progress', 'Partial', 'Failed'];
      this.columnDefs = [
        { headerName: "File Name", headerTooltip: "File Name", field: "inputFileName", checkboxSelection: true, width: 85 },
        { headerName: "Status", headerTooltip: "Status", field: "status", minWidth: 85, maxWidth: 85 },
        { headerName: "Total", headerTooltip: "Total", field: "totalAssets", suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Successful / Underreview", headerTooltip: "Successful / Underreview", field: "successUnderreviewCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 75, maxWidth: 75 },
        { headerName: "Hubzu Failure", headerTooltip: "Hubzu Failure", field: "dataFetchFailCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Odd Listing", headerTooltip: "Odd Listing", field: "oddListingsCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Assignment", headerTooltip: "Assignment", field: "weekNAssignmentCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 70, maxWidth: 70 },
        { headerName: "Unsupp State", headerTooltip: "Unsupp State", field: "unsupportedStateOrZipCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "SS & PMI", headerTooltip: "SS & PMI", field: "ssAndPmiCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 40, maxWidth: 40 },
        { headerName: "SOP", headerTooltip: "SOP", field: "sopCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 40, maxWidth: 40 },
        { headerName: "Active Listings", headerTooltip: "Active Listings", field: "activeListingsCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "Model Fail", headerTooltip: "Model Fail", field: "weekNRAFailedCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Past 12 Cycles", headerTooltip: "Past 12 Cycles", field: "past12CyclesCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 65, maxWidth: 65 },
        { headerName: "Processed", headerTooltip: "Processed", field: "processedListCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 65, maxWidth: 65 },
        {
          headerName: "Last Updated", headerTooltip: "Last Updated", minWidth: 125, maxWidth: 125, valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.uploadTimestamp, 'MM/dd/yyyy HH:mm:ss');
          }
        }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweek0")) {
      this.weekType = "sopWeek0";
      this.possibleStatus = ['Successful', 'In Progress', 'Partial', 'Failed', 'Uploaded'];
      this.columnDefs = [
        { headerName: "File Name", headerTooltip: "File Name", field: "inputFileName", checkboxSelection: true, width: 85 },
        { headerName: "Status", headerTooltip: "Status", field: "status", minWidth: 95, maxWidth: 95 },
        { headerName: "Total", headerTooltip: "Total", field: "totalAssets", suppressFilter: true, suppressSorting: true, minWidth: 55, maxWidth: 55 },
        { headerName: "Dup Asset", headerTooltip: "Dup Asset", field: "sopWeek0DuplicateAssetCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "Unsupp AV", headerTooltip: "Unsupp AV", field: "sopWeek0UnsupportedAssetValueCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 60, maxWidth: 60 },
        { headerName: "Processed", headerTooltip: "Processed", field: "processedListCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 75, maxWidth: 75 },
        {
          headerName: "Last Updated", headerTooltip: "Last Updated", minWidth: 130, maxWidth: 130, valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.uploadTimestamp, 'MM/dd/yyyy HH:mm:ss');
          }
        }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweekN")) {
      this.weekType = "sopWeekN";
      this.possibleStatus = ['Successful', 'In Progress', 'Partial', 'Failed'];
      this.columnDefs = [
        { headerName: "File Name", headerTooltip: "File Name", field: "inputFileName", checkboxSelection: true, width: 85 },
        { headerName: "Status", headerTooltip: "Status", field: "status", minWidth: 85, maxWidth: 85 },
        { headerName: "Total", headerTooltip: "Total", field: "totalAssets", suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Successful / Underreview", headerTooltip: "Successful / Underreview", field: "successUnderreviewCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 75, maxWidth: 75 },
        { headerName: "Hubzu Failure", headerTooltip: "Hubzu Failure", field: "dataFetchFailCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Odd Listing", headerTooltip: "Odd Listing", field: "oddListingsCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Assignment", headerTooltip: "Assignment", field: "weekNAssignmentCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 70, maxWidth: 70 },
        { headerName: "Unsupp State", headerTooltip: "Unsupp State", field: "unsupportedStateOrZipCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "SS & PMI", headerTooltip: "SS & PMI", field: "ssAndPmiCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 40, maxWidth: 40 },
        { headerName: "Vacant", headerTooltip: "Vacant", field: "sopCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Active Listings", headerTooltip: "Active Listings", field: "activeListingsCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 50, maxWidth: 50 },
        { headerName: "Model Fail", headerTooltip: "Model Fail", field: "raFailedCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 45, maxWidth: 45 },
        { headerName: "Past 12 Cycles", headerTooltip: "Past 12 Cycles", field: "past12CyclesCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 65, maxWidth: 65 },
        { headerName: "Processed", headerTooltip: "Processed", field: "processedListCount", cellRendererFramework: FilterCountComponent, suppressFilter: true, suppressSorting: true, minWidth: 65, maxWidth: 65 },
        {
          headerName: "Last Updated", headerTooltip: "Last Updated", minWidth: 125, maxWidth: 125, valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.uploadTimestamp, 'MM/dd/yyyy HH:mm:ss');
          }
        }
      ];
    }

    this.gridOptions = {

      // PROPERTIES - object properties, myRowData and myColDefs are created somewhere in your application
      columnDefs: this.columnDefs,

      // PROPERTIES - simple boolean / string / number properties
      enableFilter: true,
      enableColResize: true,
      enableSorting: true,
      rowSelection: "single",
      suppressRowClickSelection: true,
      suppressMenuHide: true,
      headerHeight: 40,

      // EVENTS - add event callback handlers
      onGridReady: this.resizeColumns.bind(this)
      // CALLBACKS
    }

    this.loadDashboardGridFlagSubscription = this.gridService.getLoadDashboardGridFlag().subscribe(flag => {
      if (flag) {
        if(this.gridApi) {
          this.gridApi.api.setSortModel(null);
          this.gridApi.api.sizeColumnsToFit();
        }
        this.intViewportHeight = window.innerHeight;
        this.gridMaxHeight = this.intViewportHeight - 260;
        this.gridMinHeight = this.intViewportHeight - 292;
        this.error = '';
        this.errorFlag = false;
        this.dashboardService.getAllFile(this.weekType).subscribe(response => {
          if (response.error) {
            this.errorFlag = true;
            this.errorMsg = response.message;
          } else {
            this.fileNameSet = new Set();
            this.fileName = [];
            this.dropdownFilesList = [];
            this.selectedStatus = [];
            this.rowData = [];
            for (var columnEntry in response.response) {
              if (response.response[columnEntry].status != "DATA_LOAD") {
                this.rowData.push(response.response[columnEntry]);
                if (!this.fileNameSet.has(response.response[columnEntry].inputFileName)) {
                  this.fileNameSet.add(response.response[columnEntry].inputFileName);
                  this.dropdownFilesList.push(response.response[columnEntry].inputFileName);
                }
              }
            }
            this.successFlag = true;
            this.successMsg = response.message;
          }
        }, error => {
          this.errorFlag = true;
          this.errorMsg = error.json;
        });
      }
    });

  }

  isDisabled() {
    if (this.fromDate == "" && this.toDate == "") {
      return false;
    } else if (this.fromDate != "" && this.toDate != "") {
      return false;
    } else {
      return true;
    }
  }

  onKeyInput() {
    return false;
  }

  onSearch() {
    this.searchFilter = new SearchFilter();
    this.searchFilter.fromDate = null;
    this.searchFilter.toDate = null;

    var startDate = new Date(this.fromDate).getTime();
    var endDate = new Date(this.toDate).getTime();
    endDate = endDate + 86400 * 1000 - 1;
    if (startDate > endDate) {
      this.error = "From date should not be later than To date";
      this.errorFlag = true;
      return;
    } else if (startDate <= endDate) {
      this.errorFlag = false;
      this.searchFilter.fromDate = startDate;
      this.searchFilter.toDate = endDate;
    }


    this.searchFilter.status = [];
    for (var status in this.selectedStatus) {
      this.searchFilter.status.push(this.statusMap.get(this.selectedStatus[status]));
    }

    if (this.fileName.length != 0) {
      this.searchFilter.fileName = this.fileName[0];
    } else {
      this.searchFilter.fileName = null;
    }

    this.searchFilter.weekType = this.weekType;

    this.dashboardService.getFilteredDetails(this.searchFilter).subscribe(response => {
      if (response.error) {
        this.error = response.message;
        this.errorFlag = true;
      } else {
        this.rowData = [];
        for (var columnEntry in response.response) {
          this.rowData.push(response.response[columnEntry]);
        }
      }
    }, error => {
      this.error = "Search failed";
      this.errorFlag = true;
    });

  }

  onDownload() {
    this.errorFlag = false;
    const selectedNodes = this.gridApi.api.getSelectedNodes();
    if (selectedNodes.length > 0) {
      if(selectedNodes[0].data.status == "IN-PROGRESS") {
        this.error = "Selected file's processing is in-progress";
        this.errorFlag = true;
      } else {
        this.downloader.downloadFile(selectedNodes[0].data.id, this.weekType);
      }
    } else {
      this.error = "Select file to download using checkbox";
      this.errorFlag = true;
    }
  }

  ngOnInit() {
  }

  resizeColumns(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params;
  }

  ngOnDestroy() {
    if (this.loadDashboardGridFlagSubscription) {
      this.loadDashboardGridFlagSubscription.unsubscribe();
    }
  }

}
