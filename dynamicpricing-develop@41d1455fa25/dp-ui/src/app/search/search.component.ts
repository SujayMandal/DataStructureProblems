import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { GridOptions } from "ag-grid";
import { SearchService } from "app/search.service";
import { Subscription } from "rxjs/Subscription";
import { GridService } from "app/grid.service";
import { DatePipe } from "@angular/common";
import { BsModalRef, BsModalService } from "ngx-bootstrap/modal";



@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  clearSearchTabSubscription: Subscription;
  changeGridSubscription: Subscription;
  recommHistory: boolean = true;
  errorMsg: string = '';
  errorFlag: boolean = false;
  successFlag: boolean = false;
  loanID: String;
  response: Object = null;
  excludeReason: string;
  modalRef: BsModalRef;
  //grid variables
  columnDefs;
  gridOptions: GridOptions;
  rowData: Array<any> = [];
  futureTabData: Array<any> = [];
  gridReadyFlag: boolean = false;
  intViewportHeight: any;
  gridMinHeight: any;
  gridMaxHeight: any;
  gridApi: any;
  searchDropdownSettings: any;
  possibleSearches: String[];
  selectedSearch: String[];
  reason:any;
 

  constructor(private loanSearch: SearchService, private gridService: GridService, private datePipe: DatePipe, private modalService: BsModalService) {

    this.possibleSearches = ['Vacant', 'SOP'];
    this.selectedSearch = [];

    this.searchDropdownSettings = {
      singleSelection: true,
      allowSearchFilter: false,
      closeDropDownOnSelection: true
    };

    this.createDefaultGrid(datePipe);

    this.changeGridSubscription = this.gridService.getShowFutureRecGrid().subscribe(flag => {
      if (!flag) {
        this.createDefaultGrid(datePipe);
      } else {
        this.recommHistory = false;
       /*  this.columnDefs = [
          {
            headerName: "Loan Number", headerTooltip: "Loan Number",
            field: "loanNumber"
          },
          {
            headerName: "Old Loan Number", headerTooltip: "Old Loan Number",
            field: "oldLoanNumber"
          },
          {
            headerName: "Latest List Status", headerTooltip: "Latest List Status",
            field: "listStatus"
          },
          {
            headerName: "Latest List End Date", headerTooltip: "Latest List End Date",
            field: "listEndDate"
          },
          { headerName: "Last Reduction Date", headerTooltip: "Last Reduction Date", field: "lastReductionDate" },
          { headerName: "Latest SOP Flag", headerTooltip: "Latest SOP Flag", field: "sopFlag" },
          { headerName: "Latest List Type", headerTooltip: "Latest List Type", field: "listType" },
          { headerName: "SS / PMI Flag", headerTooltip: "SS / PMI Flag", field: "ssPmiFlag" },
          { headerName: "State", headerTooltip: "State", field: "state" },
          { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
          { headerName: "Week 0 Run Date", headerTooltip: "Week 0 Run Date", field: "week0RunDate" },
          { headerName: "Week 0 Eligibility", headerTooltip: "Week 0 Eligibility", field: "week0Eligibility" },
          { headerName: "Assignment", headerTooltip: "Assignment", field: "week0Assignment" },
          { headerName: "No. Of Listings", headerTooltip: "No. Of Listings", field: "listingCount" },
          { headerName: "Future Reductions", headerTooltip: "Future Reductions", field: "futureReductionFlag" }
        ]; */
      }
    });

    this.clearSearchTabSubscription = this.gridService.getClearSearchTab().subscribe(flag => {
      if (flag) {
        this.loanID = null;
        this.selectedSearch = [];
        this.successFlag = false;
        this.errorMsg = '';
        this.errorFlag = false;
      }
    });

  }

  createDefaultGrid(datePipe: DatePipe) {
    this.recommHistory = true;
    this.columnDefs = [
      {
        headerName: "Loan Number", headerTooltip: "Loan Number",
        field: "loanNumber"
      },
      {
        headerName: "Old Loan Number", headerTooltip: "Old Loan Number",
        field: "oldLoanNumber"
      },
      {
        headerName: "Classification", headerTooltip: "Classification",
        field: "classification"
      },
      {
        headerName: "Initial Valuation", headerTooltip: "Initial Valuation",
        field: "initialValuation"
      },
      {
        headerName: "Assignment Date", headerTooltip: "Assignment Date", valueGetter: function transformDate(params) {
          return datePipe.transform(params.data.assignmentDate, 'MM/dd/yyyy');
        }
      },
      { headerName: "Eligible", headerTooltip: "Eligible", field: "eligible" },
      { headerName: "Assignment", headerTooltip: "Assignment", field: "assignment" },
      { headerName: "Recommended Value", headerTooltip: "Recommended Value", field: "recommendedValue" },
      { headerName: "Week", headerTooltip: "Week", field: "week" },
      { headerName: "Notes", headerTooltip: "Notes", field: "notes", tooltipField: "notes" }
    ];
  }

  onSearch() {
    this.onSearchInit();
    this.intViewportHeight = window.innerHeight;
    this.gridMaxHeight = this.intViewportHeight - 260;
    this.loanSearch.searchLoanID(this.loanID, this.selectedSearch[0], this.recommHistory).subscribe(response => {
      if (this.gridApi) {
        this.gridApi.api.sizeColumnsToFit();
      }
    
      if (response.message != null) {
        this.errorFlag = true;
        this.errorMsg = response.message;
      } else {
       this.response = response.response;
        this.rowData = [];
        if (this.recommHistory) {
          for (var columnEntry in response.response) {
            this.rowData.push(response.response[columnEntry]);
          }
        } else {
        //  this.rowData.push(response.response); // for grid-view

          this.futureTabData= response.response;
          this.reason = JSON.stringify(this.futureTabData["reason"]).replace(/[{"}]/g,' ').replace("null","");
        
     
        }
        this.successFlag = true;
      }
    }, error => {
      this.errorFlag = true;
      this.errorMsg = "Loan Search failed";
    }); 

  }

  private onSearchInit() {
    this.errorFlag = false;
    this.successFlag = false;
  }

  ngOnInit() {
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params;
  }

  onExclude() {
    this.loanSearch.removeLoan(this.loanID, this.selectedSearch[0], this.excludeReason).subscribe(response => {
      this.errorFlag = true;
      if (response.message == null) {
        response.message = "Loan excluded"
      }
      this.errorMsg = response.message;
    }, error => {
      this.errorFlag = true;
      this.errorMsg = "Loan exclusion failed";
    });
  }

  openModal(template: TemplateRef<any>) {
    this.excludeReason = null;
    this.modalRef = this.modalService.show(template);
  }

  confirm(): void {
    this.modalRef.hide();
    this.onExclude();
    this.excludeReason = null;
  }

  decline(): void {
    this.modalRef.hide();
    this.excludeReason = null;
  }

  ngOnDestroy() {
    if (this.clearSearchTabSubscription) {
      this.clearSearchTabSubscription.unsubscribe();
    }
    if (this.changeGridSubscription) {
      this.changeGridSubscription.unsubscribe();
    }
  }

}