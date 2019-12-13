import { Component, OnInit, ViewChild } from '@angular/core';
import { FilterService } from "app/filter.service";
import { GridService } from "app/grid.service";
import { Subscription } from "rxjs/Subscription";
import { GridOptions } from "ag-grid/main";
import { FileSelected } from "app/file-selected";
import { AgGridNg2 } from "ag-grid-angular";
import { NumericEditorComponent } from "app/numeric-editor/numeric-editor.component";
import { Props } from "app/props";
import { ProcessorService } from "app/processor.service";
import { weekNProps } from "app/weekn-props";
import { SopWeek0Props } from "app/sopWeek0Props";
import { SopWeekNProps } from "app/sop-weekn-props";

@Component({
  selector: 'app-filter-grid',
  templateUrl: './filter-grid.component.html',
  styleUrls: ['./filter-grid.component.scss']
})
export class FilterGridComponent implements OnInit {

  filterBucketSelectedSubscription: Subscription;
  fileSelected: FileSelected;
  errorMsg: string[] = [];
  successMsg: string = '';
  errorFlag: boolean = false;
  successFlag: boolean = false;
  fileDetailInfo: any;
  error: String;
  weekType: String;
  //grid variables
  columnDefs;
  gridOptions: GridOptions;
  rowData: Array<any> = [];
  gridReadyFlag: boolean = false;
  gridApi: any;
  intViewportHeight: any;
  gridMaxHeight: any;
  gridMidHeight: any;
  gridMinHeight: any;
  //map
  bucketCommandMap: Map<String, String>;
  summaryReasonMap: Map<String, String[]>;
  filterHeader: String;
  summery: String;
  //reprocess
  selectedProp: any;

  constructor(private filterService: FilterService, private gridService: GridService, private fileProcessor: ProcessorService) {

    this.fileSelected = new FileSelected();
    this.fileSelected.Id = '';
    this.fileSelected.filter = '';

    this.bucketCommandMap = new Map();
    this.bucketCommandMap.set("Client Mismatch", "week0RRClassification");
    this.bucketCommandMap.set("Dup Asset", "week0DuplicateFilter");
    this.bucketCommandMap.set("SS", "week0InvestorCodeFilter");
    this.bucketCommandMap.set("Unsupp AV", "week0AssetValueFilter");
    this.bucketCommandMap.set("RR/RTNG Failure", "week0RRRtngAggregator");
    this.bucketCommandMap.set("Unsupp PropType", "week0PropertyTypeFilter");
    this.bucketCommandMap.set("Model Fail", "week0RAInputPayload");
    this.bucketCommandMap.set("Processed", "Processed");
    this.bucketCommandMap.set("Past 12 Cycles", "SOPWeekNPast12CyclesFilter");
    this.bucketCommandMap.set("Hubzu Failure", "weekNFetchData");
    this.bucketCommandMap.set("Odd Listing", "weekNOddListingsFilter");
    this.bucketCommandMap.set("Assignment", "weekNAssignmentFilter");
    this.bucketCommandMap.set("Unsupp State", "weekNZipStateFilter");
    this.bucketCommandMap.set("SS & PMI", "weekNSSPmiFilter");
    this.bucketCommandMap.set("SOP", "weekNSOPFilter");
    this.bucketCommandMap.set("Vacant", "SOPWeekNSOPFilter");
    this.bucketCommandMap.set("Active Listings", "weekNActiveListingsFilter");
    this.bucketCommandMap.set("Successful / Underreview", "weekNSuccessfulUnderreviewFilter");

    this.summaryReasonMap = new Map();
    this.summaryReasonMap.set("Client Mismatch", ["Client Mismatch", "Contains assets for which input ‘Classification’ value (Ocwen/NRZ) does not match with backend (RR) classification",
      "RR Classification is different from input"]);
    this.summaryReasonMap.set("Dup Asset", ["Duplicate Asset", "Contains assets which had a previous successful transaction",
      "Previously Assigned"]);
    this.summaryReasonMap.set("SS", ["SS", "Contains assets for which investor code is part of Special Servicing investor list",
      "Special Servicing Investor"]);
    this.summaryReasonMap.set("Unsupp AV", ["Unsupp AV", "Contains assets which have value outside the Dynamic Pricing model supported range of 10k-750k",
      "AV Outside Range"]);
    this.summaryReasonMap.set("RR/RTNG Failure", ["RR/RTNG Failure", "Contains assets for which data fetch from RR and/or RTNG failed",
      "Unable to fetch Loan details from Real Resolution or Real Trans or Both "]);
    this.summaryReasonMap.set("Unsupp PropType", ["Unsupp PropType", "Contains assets which have property type not supported by Dynamic Pricing model",
      "Unsupported Property Type."]);
    this.summaryReasonMap.set("Model Fail", ["Model Fail", "Contains Assets which failed the RA call",
      "RA call failed"]);
    this.summaryReasonMap.set("Processed", ["Processed", "Contain assets which have been successfully processed through Dynamic Pricing model"]);
    this.summaryReasonMap.set("Past 12 Cycles", ["Past 12 Cycles", "Contain assets which have more than 12 listings through Dynamic Pricing model"]);
    this.summaryReasonMap.set("Hubzu Failure", ["Hubzu Failure", "Contains Assets for which data fetch from SS, RR, Stage 5 and/or Hubzu failed",
      "Unable to fetch Loan details from Real Resolution, Special Serving, Stage 5 and/or Hubzu"]);
    this.summaryReasonMap.set("Odd Listing", ["Odd Listing", "Contains Assets whose listing week is currently odd (1,3,5,7,9,11)", "Odd Listing"]);
    this.summaryReasonMap.set("Assignment", ["Assignment", "Contains Ocwen Assets which don't exist in Week 0 Database or were benchmarked", "Assets benchmarked or non-existent"]);
    this.summaryReasonMap.set("Unsupp State", ["Unsupp State", "Contains Assets which are from unsupported States/Zips",
      "Unsupported States/Zips"]);
    this.summaryReasonMap.set("SS & PMI", ["SS & PMI", "Contains Assets for which investor code is part of Special Servicing investor list and/or has no Private Mortgage Insurance (PMI)",
      "SS and/or PMI"]);
    this.summaryReasonMap.set("SOP", ["SOP", "Contains Assets which are self Occupied", "Self Occupied"]);
    this.summaryReasonMap.set("Active Listings", ["Active Listings", "Contains Assets whose listing is currently active/ongoing"]);
    this.summaryReasonMap.set("Successful / Underreview", ["Successful / Underreview", "Contains Assets whose latest status is Successful/Underreview"]);
    this.summaryReasonMap.set("Vacant", ["Vacant", "Contains Assets which are vacant", "Vacant"]);

    if (window.location.pathname.toString().includes("/week0")) {
      this.weekType = "week0";
      this.columnDefs = [
        {
          headerName: "Asset #", headerTooltip: "Asset #", field: "assetNumber", headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true, checkboxSelection: true
        },
        {
          headerName: "Client Code", headerTooltip: "Client Code", field: "clientCode", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            maxLength: 6,
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this),
          tooltipField: "dpProcessParamOriginal.clientCode"
        },
        { headerName: "Status", headerTooltip: "Status", field: "status" },
        {
          headerName: "Asset Value", headerTooltip: "Asset Value", field: "assetValue", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditorFramework: NumericEditorComponent,
          cellStyle: this.cellStyling.bind(this),
          tooltipField: "dpProcessParamOriginal.assetValue"
        },
        { headerName: "AV set date", headerTooltip: "AV set date", field: "avSetDate" },
        { headerName: "List Price (106% of AV)", headerTooltip: "List Price (106% of AV)", field: "listPrice" },
        {
          headerName: "Classification", headerTooltip: "Classification", field: "classification", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: ["NRZ", "OCN"],
          },
          cellStyle: this.cellStyling.bind(this),
          tooltipField: "dpProcessParamOriginal.classification"
        },
        {
          headerName: "Property Type", headerTooltip: "Property Type", field: "propertyType", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: ["", "CONDO", "APARTMENT", "TWO FAMILY", "THREE FAMILY", "FOUR FAMILY", "DUPLEX", "TRIPLEX", "QUADPLEX",
              "MULTIFAMILY", "MANUFACTURED", "MOBILE HOME", "MODULAR", "SINGLE FAMILY", "DETACHED", "SFR", "TOWNHOME",
              "ATTACHED", "ROWHOUSE"],
          },
          cellStyle: this.cellStyling.bind(this),
          tooltipField: "dpProcessParamOriginal.propertyType"
        },
        { headerName: "Reason", headerTooltip: "Reason", field: "notes", tooltipField: "notes" }
      ];
    } else if (window.location.pathname.toString().includes("/weekN")) {
      this.weekType = "weekN";
      this.bucketCommandMap.set("Model Fail", "weekNRAIntegrarion");
      this.summaryReasonMap.set("Model Fail", ["Model Fail", "Contains Assets which failed the Week N Model call",
        "Failed Model Call"]);
      this.summaryReasonMap.set("Processed", ["Processed", "Contain assets which have been successfully processed through Dynamic Pricing Week N model"]);
      this.columnDefs = [
        {
          headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber", headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true, checkboxSelection: true
        },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        {
          headerName: "State", headerTooltip: "State", field: "state", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this)
          //tooltipField: "dpProcessWeekNParamOriginal.state"
        },
        {
          headerName: "Zip Code", headerTooltip: "Zip Code", field: "zipCode", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this)
          //tooltipField: "dpProcessWeekNParamOriginal.zipCode"
        },
        {
          headerName: "SS", headerTooltip: "SS", field: "clientCode", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this)
          //tooltipField: "dpProcessWeekNParamOriginal.clientCode"
        },
        { headerName: "PMI", headerTooltip: "PMI", field: "privateMortgageInsurance" },
        { headerName: "SOP", headerTooltip: "SOP", field: "sellerOccupiedProperty" },
        { headerName: "Reason", headerTooltip: "Reason", field: "notes", tooltipField: "notes" }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweek0")) {
      this.weekType = "sopWeek0";
      this.bucketCommandMap.set("Dup Asset", "SopWeek0DuplicateFilter");
      this.bucketCommandMap.set("Unsupp AV", "SopWeek0AssetValueFilter");
      this.summaryReasonMap.set("Dup Asset", ["Duplicate Asset", "Contains assets which had a previous eligible transaction",
        "Previously Assigned"]);
      this.summaryReasonMap.set("Unsupp AV", ["Unsupp AV", "Contains assets which have value outside the Dynamic Pricing model supported range. Supported range is 10 k to 750 k (both inclusive) for OCN and 0 to 750k (both inclusive) for NRZ",
        "AV Outside Range"]);
      this.columnDefs = [
        {
          headerName: "Asset #", headerTooltip: "Asset #", field: "assetNumber", headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true, checkboxSelection: true
        },
        { headerName: "Status", headerTooltip: "Status", field: "status" },
        { headerName: "Asset Value", headerTooltip: "Asset Value", field: "assetValue" },
        { headerName: "AV set date", headerTooltip: "AV set date", field: "avSetDate" },
        { headerName: "List Price (106% of AV)", headerTooltip: "List Price (106% of AV)", field: "listPrice" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        { headerName: "Property Type", headerTooltip: "Property Type", field: "propertyType" },
        { headerName: "Reason", headerTooltip: "Reason", field: "notes", tooltipField: "notes" }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweekN")) {
      this.weekType = "sopWeekN";
      this.bucketCommandMap.set("Model Fail", "SOPWeekNRAIntegrarion");
      this.bucketCommandMap.set("Active Listings", "SOPWeekNActiveListingsFilter");
      this.bucketCommandMap.set("Successful / Underreview", "SOPWeekNSuccessfulUnderreviewFilter");
      this.bucketCommandMap.set("Assignment", "SOPWeekNAssignmentFilter");
      this.bucketCommandMap.set("Odd Listing", "SOPWeekNOddListingsFilter");
      this.bucketCommandMap.set("SS & PMI", "SOPWeekNSSPmiFilter");
      this.bucketCommandMap.set("Unsupp State", "SOPWeekNStateFilter");
      this.bucketCommandMap.set("Hubzu Failure", "sopWeekNFetchData");
      this.bucketCommandMap.set("Past 12 Cycles", "SOPWeekNPast12CyclesFilter");

      this.summaryReasonMap.set("Model Fail", ["Model Fail", "Contains Assets which failed the Week N Model call",
        "Failed Model Call"]);
      this.summaryReasonMap.set("Processed", ["Processed", "Contain assets which have been successfully processed through Dynamic Pricing SOP Week N model"]);
      this.columnDefs = [
        {
          headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber", headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true, checkboxSelection: true
        },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        {
          headerName: "State", headerTooltip: "State", field: "state", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this)
          //tooltipField: "dpProcessWeekNParamOriginal.state"
        },
        {
          headerName: "Zip Code", headerTooltip: "Zip Code", field: "zipCode", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this)
          //tooltipField: "dpProcessWeekNParamOriginal.zipCode"
        },
        {
          headerName: "SS", headerTooltip: "SS", field: "clientCode", onCellDoubleClicked: this.isEditable.bind(this),
          cellEditor: "agLargeTextCellEditor",
          cellEditorParams: {
            rows: 1,
            cols: 10
          },
          cellStyle: this.cellStyling.bind(this)
          //tooltipField: "dpProcessWeekNParamOriginal.clientCode"
        },
        { headerName: "PMI", headerTooltip: "PMI", field: "privateMortgageInsurance" },
        { headerName: "SOP", headerTooltip: "SOP", field: "sellerOccupiedProperty" },
        { headerName: "Reason", headerTooltip: "Reason", field: "notes", tooltipField: "notes" }
      ];
    }

    this.gridOptions = {

      // PROPERTIES - object properties, myRowData and myColDefs are created somewhere in your application
      columnDefs: this.columnDefs,
      overlayNoRowsTemplate: "Zero Loans in this filter",

      // PROPERTIES - simple boolean / string / number properties
      enableFilter: true,
      enableColResize: true,
      enableSorting: true,
      suppressClickEdit: true,
      rowSelection: "multiple",
      suppressRowClickSelection: true,
      suppressMenuHide: true,
      headerHeight: 40,

      // EVENTS - add event callback handlers
      onGridReady: this.resizeColumns.bind(this)
      // CALLBACKS
    }

    this.filterBucketSelectedSubscription = this.gridService.getFilterBucketSelected().subscribe(fileSelected => {
      if (this.gridApi) {
        this.gridApi.api.sizeColumnsToFit();
      }
      this.intViewportHeight = window.innerHeight;
      this.gridMaxHeight = this.intViewportHeight - 229;
      this.gridMidHeight = this.intViewportHeight - 263;
      this.gridMinHeight = this.intViewportHeight - 280;
      if ((fileSelected.Id != '') && (this.fileSelected.Id != fileSelected.Id)) {
        this.filterService.getFileInfo(fileSelected.Id, this.weekType).subscribe(response => {
          if (response.error) {
            this.errorFlag = true;
            this.errorMsg = response.message;
          } else {
            this.errorFlag = false;
            this.fileSelected = fileSelected;
            this.filterHeader = this.summaryReasonMap.get(this.fileSelected.filter)[0];
            this.summery = this.summaryReasonMap.get(this.fileSelected.filter)[1];
            this.fileDetailInfo = response.response;
            this.gridService.setFileDetailInfo(this.fileDetailInfo);
            this.rowData = [];
            for (var columnEntry in response.response) {
              response.response[columnEntry].notes = this.summaryReasonMap.get(this.fileSelected.filter)[2];
              if (this.bucketCommandMap.get(this.fileSelected.filter) == "Processed") {
                if (!window.location.pathname.toString().includes("/SOP") && response.response[columnEntry].command == null) {
                  this.rowData.push(response.response[columnEntry]);
                }
                else if (window.location.pathname.toString().includes("/SOP") && response.response[columnEntry].failedStepCommandName == null) {
                  this.rowData.push(response.response[columnEntry]);
                }
              }
              else if ((response.response[columnEntry].command != null) && (this.bucketCommandMap.get(this.fileSelected.filter) == response.response[columnEntry].command.name)) {
                this.rowData.push(response.response[columnEntry]);
              }
              else if ((response.response[columnEntry].failedStepCommandName != null) && (response.response[columnEntry].failedStepCommandName.includes(this.bucketCommandMap.get(this.fileSelected.filter)))) {
                this.rowData.push(response.response[columnEntry]);
              }
            }
            this.successFlag = true;
            this.successMsg = response.message;
          }
        }, error => {
          this.errorFlag = true;
          this.errorMsg = error.json;
        });
      } else if (fileSelected.filter != '') {
        this.errorFlag = false;
        this.fileSelected = fileSelected;
        this.filterHeader = this.summaryReasonMap.get(this.fileSelected.filter)[0];
        this.summery = this.summaryReasonMap.get(this.fileSelected.filter)[1];
        this.rowData = [];
        for (var columnEntry in this.fileDetailInfo) {
          this.fileDetailInfo[columnEntry].notes = this.summaryReasonMap.get(this.fileSelected.filter)[2];
          if (this.bucketCommandMap.get(this.fileSelected.filter) == "Processed") {
            if (!window.location.pathname.toString().includes("/SOP") && this.fileDetailInfo[columnEntry].command == null) {
              this.rowData.push(this.fileDetailInfo[columnEntry]);
            }
            else if (window.location.pathname.toString().includes("/SOP") && this.fileDetailInfo[columnEntry].failedStepCommandName == null) {
              this.rowData.push(this.fileDetailInfo[columnEntry]);
            }
          }
          else if ((this.fileDetailInfo[columnEntry].command != null) && (this.bucketCommandMap.get(this.fileSelected.filter) == this.fileDetailInfo[columnEntry].command.name)) {
            this.rowData.push(this.fileDetailInfo[columnEntry]);
          }
          else if ((this.fileDetailInfo[columnEntry].failedStepCommandName != null) && (this.fileDetailInfo[columnEntry].failedStepCommandName.includes(this.bucketCommandMap.get(this.fileSelected.filter)))) {
            this.rowData.push(this.fileDetailInfo[columnEntry]);
          }
        }
      }
    });
  }

  cellStyling(params) {
    /*if (this.weekType == "week0") {
      return { 'background-color': params.value != params.data.dpProcessParamOriginal[params.colDef.field] ? '#94bb1e' : '' };
    } else if (this.weekType == "weekN") {
      return { 'background-color': params.value != params.data.dpProcessWeekNParamOriginal[params.colDef.field] ? '#94bb1e' : '' };
    }*/
  }

  resizeColumns(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params;
  }

  isEditable(params) {
    var flag = false;
    /*switch (this.fileSelected.filter) {
      case "Unsupp AV":
        if (params.colDef.field == "assetValue") {
          flag = true;
        }
        break;
      case "SS":
        if (params.colDef.field == "clientCode") {
          flag = true;
        }
        break;
      case "Client Mismatch":
        if (params.colDef.field == "classification") {
          flag = true;
        }
        break;
      case "Unsupp PropType":
        if (params.colDef.field == "propertyType") {
          flag = true;
        }
        break;
      case "Unsupp State":
        if ((params.colDef.field == "state") || (params.colDef.field == "zipCode")) {
          flag = true;
        }
        break;
      case "SS & PMI":
        if (params.colDef.field == "clientCode") {
          flag = true;
        }
        break;
    }*/
    params.columnApi.getColumn(params.colDef.field).getColDef().editable = flag;
    if (flag) {
      params.api.startEditingCell({
        rowIndex: params.rowIndex,
        colKey: params.column.colId
      });
    }
  }

  onReProcess() {
    this.errorFlag = false;
    this.gridApi.api.stopEditing();
    const selectedNodes = this.gridApi.api.getSelectedNodes();
    if (selectedNodes.length > 0) {
      this.selectedProp = new Props();
      if (this.weekType == "week0") {
        this.selectedProp.dpfileProcessStatusInfo = selectedNodes[0].data.dynamicPricingFilePrcsStatus;
        this.selectedProp.columnEntries = [];
        for (var selectedNode in selectedNodes) {
          this.selectedProp.columnEntries.push(selectedNodes[selectedNode].data);
        }
      } else if (this.weekType == "weekN") {
        this.selectedProp = new weekNProps();
        this.selectedProp.dpWeeknProcessStatus = selectedNodes[0].data.dpWeekNProcessStatus;
        this.selectedProp.columnEntries = [];
        for (var selectedNode in selectedNodes) {
          selectedNodes[selectedNode].data.dpWeekNProcessStatus.createdDate = null;
          selectedNodes[selectedNode].data.dpWeekNProcessStatus.lastModifiedDate = null;
          //selectedNodes[selectedNode].data.dpProcessWeekNParamOriginal.createdDate = null;
          //selectedNodes[selectedNode].data.dpProcessWeekNParamOriginal.lastModifiedDate = null;
          this.selectedProp.columnEntries.push(selectedNodes[selectedNode].data);
        }
        this.selectedProp.columnCount = null;
      } else if (this.weekType == "sopWeek0") {
        this.selectedProp = new SopWeek0Props();
        this.selectedProp.dpSopWeek0ProcessStatusInfo = selectedNodes[0].data.sopWeek0ProcessStatus;
        this.selectedProp.columnEntries = [];
        for (var selectedNode in selectedNodes) {
          this.selectedProp.columnEntries.push(selectedNodes[selectedNode].data);
        }
        this.selectedProp.dataLevelError = false;
        this.selectedProp.columnCount = null;
      } else if (this.weekType == "sopWeekN") {
        this.selectedProp = new SopWeekNProps();
        this.selectedProp.dpWeeknProcessStatus = selectedNodes[0].data.sopWeekNProcessStatus;
        this.selectedProp.columnEntries = [];
        for (var selectedNode in selectedNodes) {
          this.selectedProp.columnEntries.push(selectedNodes[selectedNode].data);
        }
        this.selectedProp.dataLevelError = false;
        this.selectedProp.columnCount = null;
      }
      this.selectedProp.reprocess = true;
      this.fileProcessor.processFile(this.selectedProp, null, this.weekType).subscribe(response => {
        if (!response.success) {
          this.errorFlag = true;
          this.error = response.message;
        } else {
          this.errorFlag = true;
          this.error = response.message;
          this.gridService.setRedirectToDashboard(true);
        }
      }, error => {
        this.errorFlag = true;
        this.error = "Reprocess failed";
      });
    } else {
      this.error = "Select properties using checkbox";
      this.errorFlag = true;
    }
  }

  isAvailable() {
    if (this.fileSelected.filter == "Processed") {
      return false;
    } else {
      return true;
    }
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if (this.filterBucketSelectedSubscription) {
      this.filterBucketSelectedSubscription.unsubscribe();
    }
  }

}
