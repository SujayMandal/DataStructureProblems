import { Component, OnInit, ElementRef } from '@angular/core';
import * as $ from 'jquery';
import { UploaderService } from "../uploader.service";
import { GridOptions } from "ag-grid/main";
import { ProcessorService } from "../processor.service";
import { DatePipe } from "@angular/common";
import { GridService } from "app/grid.service";
import { Subscription } from "rxjs/Subscription";
import { weekNProps } from "app/weekn-props";
import { SopWeekNProps } from 'app/sop-weekn-props';

@Component({
  selector: 'app-upload-block',
  templateUrl: './upload-block.component.html',
  styleUrls: ['./upload-block.component.scss']
})
export class UploadBlockComponent implements OnInit {

  clearUploadTabSubscription: Subscription;
  enableUpload: boolean = false;
  enableProcess: boolean = false;
  browseEnable: boolean = true;
  errorMsg: string = '';
  successMsg: string = '';
  errorFlag: boolean = false;
  successFlag: boolean = false;
  showSuccessMsg: boolean = true;
  fileName: string = '';
  response: Object = null;
  weekType: String;
  viewWeek: String;
  weekNProp: any;
  //grid variables
  columnDefs;
  gridOptions: GridOptions;
  rowData: Array<any> = [];
  gridReadyFlag: boolean = false;
  intViewportHeight: any;
  gridMinHeight: any;
  gridMaxHeight: any;
  gridApi: any;

  constructor(private fileUploader: UploaderService, private elem: ElementRef, private fileProcessor: ProcessorService,
    private datePipe: DatePipe, private gridService: GridService) {

    if (window.location.pathname.toString().includes("/week0")) {
      this.weekType = "week0";
      this.viewWeek = "Week0";
      this.columnDefs = [
        { headerName: "Asset #", headerTooltip: "Asset #", field: "assetNumber" },
        { headerName: "Client Code", headerTooltip: "Client Code", field: "clientCode" },
        { headerName: "Status", headerTooltip: "Status", field: "status" },
        { headerName: "Asset Value", headerTooltip: "Asset Value", field: "assetValue" },
        { headerName: "AV set date", headerTooltip: "AV set date", field: "avSetDate" },
        { headerName: "List Price (106% of AV)", headerTooltip: "List Price (106% of AV)", field: "listPrice" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" },
        { headerName: "Property Type", headerTooltip: "Property Type", field: "propertyType" },
        {
          headerName: "Last Updated", headerTooltip: "Last Updated", valueGetter: function transformDate(params) {
            return datePipe.transform(params.data.dynamicPricingFilePrcsStatus.uploadTimestampStr, 'MM/dd/yyyy HH:mm:ss');
          }
        }
      ];
    } else if (window.location.pathname.toString().includes("/weekN")) {
      this.weekType = "weekN";
      this.viewWeek = "WeekN";
      this.columnDefs = [
        { headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber" },
        // { headerName: "Classification", field: "classification" },
        // {
        //   headerName: "Latest List End Date", valueGetter: function transformDate(params) {
        //     return datePipe.transform(params.data.listEndDateDtNn, 'MM:dd:yyyy');
        //   }
        // },
        // { headerName: "Latest Status", field: "listSttsDtlsVc" },
        // {
        //   headerName: "Date Of Last Reduction", valueGetter: function transformDate(params) {
        //     return datePipe.transform(params.data.dateOfLastReduction, 'MM:dd:yyyy');
        //   }
        // },
        {
          headerName: "Delivery Date", headerTooltip: "Delivery Date", valueGetter: function transformDate(params) {
            if (params.data.dpWeekNProcessStatus != null) {
              return datePipe.transform(params.data.dpWeekNProcessStatus.fetchedDateStr, 'MM/dd/yyyy HH:mm:ss');
            } else {
              return null;
            }
          }
        }
        // { headerName: "Vacant Status", field: "sellerOccupiedProperty" }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweek0")) {
      this.weekType = "sopWeek0";
      this.viewWeek = "SOP Week0";
      this.columnDefs = [
        { headerName: "Asset #", headerTooltip: "Asset #", field: "assetNumber" },
        { headerName: "State", headerTooltip: "State", field: "state" },
        { headerName: "Prop type", headerTooltip: "Prop type", field: "propertyType" },
        { headerName: "Status", headerTooltip: "Status", field: "status" },
        { headerName: "Asset Value", headerTooltip: "Asset Value", field: "assetValue" },
        { headerName: "AV set date", headerTooltip: "AV set date", field: "avSetDate" },
        { headerName: "REO date", headerTooltip: "REO date", field: "reoDate" },
        { headerName: "List Price", headerTooltip: "List Price", field: "listPrice" },
        { headerName: "Classification", headerTooltip: "Classification", field: "classification" }
      ];
    } else if (window.location.pathname.toString().includes("/SOPweekN")) {
      this.weekType = "sopWeekN";
      this.viewWeek = "SOP WeekN";
      this.columnDefs = [
        { headerName: "Asset ID", headerTooltip: "Asset ID", field: "assetNumber" },
        {
          headerName: "Delivery Date", headerTooltip: "Delivery Date", valueGetter: function transformDate() {
            return datePipe.transform(new Date(), 'MM/dd/yyyy HH:mm:ss', 'EDT');
          }
        }
      ];
    }

    this.clearUploadTabSubscription = this.gridService.getClearUploadTab().subscribe(flag => {
      if (flag) {
        this.enableUpload = false;
        this.enableProcess = false;
        this.browseEnable = true;
        this.errorMsg = '';
        this.successMsg = '';
        this.errorFlag = false;
        this.successFlag = false;
        this.showSuccessMsg = true;
        this.fileName = '';
        this.response = null;
      }
    });

  }

  onSelectFile(event) {
    let fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      let file: File = fileList[0];
      this.fileName = file.name;
      this.enableUpload = true;
      this.enableProcess = false;
    } else {
      this.enableUpload = false;
    }
    this.errorFlag = false;
    this.successFlag = false;
  }

  onUpload() {

    this.onUploadInit();

    let file = this.elem.nativeElement.querySelector('#fileChooser').files[0];
    let formData = new FormData();
    formData.append('file', file, file.name);
    this.intViewportHeight = window.innerHeight;
    this.gridMaxHeight = this.intViewportHeight - 260;
    this.gridMinHeight = this.intViewportHeight - 326;
    this.fileUploader.uploadFile(formData, file.name, this.weekType).subscribe(response => {
      if (this.gridApi) {
        this.gridApi.api.sizeColumnsToFit();
      }
      this.fileName = '';
      if (this.weekType == "week0") {
        if (!response.success) {
          this.errorFlag = true;
          this.errorMsg = response.response.errorMessages;
        } else {
          this.response = response.response.dpProcessParamEntry;
          this.rowData = [];
          for (var columnEntry in response.response.dpProcessParamEntry.columnEntries) {
            this.rowData.push(response.response.dpProcessParamEntry.columnEntries[columnEntry]);
          }
          this.successFlag = true;
          this.showSuccessMsg = false;
          this.successMsg = response.message;
          this.enableProcess = true;
        }
      } else if (this.weekType == "sopWeek0") {
        if (!response.success) {
          this.errorFlag = true;
          this.errorMsg = response.response.errorMessages;
        } else {
          this.response = response.response.dpSopParamEntryInfo;
          this.rowData = [];
          for (var columnEntry in response.response.dpSopParamEntryInfo.columnEntries) {
            this.rowData.push(response.response.dpSopParamEntryInfo.columnEntries[columnEntry]);
          }
          this.successFlag = true;
          this.showSuccessMsg = false;
          this.successMsg = response.message;
          this.enableProcess = true;
        }
      } else if (this.weekType == "sopWeekN") {
        if (!response.success) {
          this.errorFlag = true;
          this.errorMsg = response.message;
        } else {
          this.response = response.response;
          this.rowData = [];
          for (var columnEntry in response.response) {
            this.rowData.push(response.response[columnEntry]);
          }
          this.successFlag = true;
          this.showSuccessMsg = false;
          this.successMsg = response.message;
          this.enableProcess = true;
        }
      } else {
        if (!response.success) {
          this.errorFlag = true;
          this.errorMsg = response.message;
        } else {
          this.response = response.response;
          this.rowData = [];
          for (var columnEntry in response.response) {
            this.rowData.push(response.response[columnEntry]);
          }
          this.successFlag = true;
          this.showSuccessMsg = false;
          this.successMsg = response.message;
          this.enableProcess = true;
        }
      }
    }, error => {
      this.errorFlag = true;
      this.errorMsg = "File Upload failed";
    });

    this.enableUpload = false;
    this.browseEnable = true;
    $('#fileChooser').val('');
  }

  private onUploadInit() {
    this.enableUpload = false;
    this.enableProcess = false;
    this.browseEnable = false;
    this.errorFlag = false;
    this.successFlag = false;
  }


  fileChooser() {
    this.enableUpload = false;
    $('#fileChooser').trigger('click');
  }

  onProcess() {

    this.onProcessInit();
    var propsForProcess = this.response;

    if (this.weekType == "weekN") {
      this.weekNProp = new weekNProps();
      this.weekNProp.dpWeeknProcessStatus = propsForProcess[0].dpWeekNProcessStatus;
      this.weekNProp.columnEntries = [];
      for (var prop in propsForProcess) {
        propsForProcess[prop].dpWeekNProcessStatus.createdDate = null;
        propsForProcess[prop].dpWeekNProcessStatus.lastModifiedDate = null;
        if (propsForProcess[prop].dpProcessWeekNParamOriginal != null) {
          propsForProcess[prop].dpProcessWeekNParamOriginal.createdDate = null;
          propsForProcess[prop].dpProcessWeekNParamOriginal.lastModifiedDate = null;
        }
        this.weekNProp.columnEntries.push(propsForProcess[prop]);
      }
      this.weekNProp.columnCount = null;
      this.weekNProp.reprocess = false;
      propsForProcess = this.weekNProp;
    } else if(this.weekType == "sopWeekN") {
      this.weekNProp = new SopWeekNProps();
      this.weekNProp.dpSopWeekNProcessStatus = propsForProcess[0].sopWeekNProcessStatus;
      this.weekNProp.columnEntries = [];
      for (var prop in propsForProcess) {
        propsForProcess[prop].sopWeekNProcessStatus.createdDate = null;
        propsForProcess[prop].sopWeekNProcessStatus.lastModifiedDate = null;
        this.weekNProp.columnEntries.push(propsForProcess[prop]);
      }
      this.weekNProp.columnCount = null;
      this.weekNProp.reprocess = false;
      propsForProcess = this.weekNProp;
    }

    this.fileProcessor.processFile(propsForProcess, null, this.weekType).subscribe(response => {
      if (!response.success) {
        this.errorFlag = true;
        this.errorMsg = response.message;
      } else {
        this.successFlag = true;
        this.showSuccessMsg = true;
        this.successMsg = response.message;
        this.enableProcess = false;
      }
    }, error => {
      this.errorFlag = true;
      this.errorMsg = "Processing failed";
    });
  }

  private onProcessInit() {
    this.enableProcess = false;
    this.errorFlag = false;
    this.successFlag = false;
  }

  ngOnInit() {
  }

  onGridReady(params) {
    params.api.sizeColumnsToFit();
    this.gridApi = params;
  }

  ngOnDestroy() {
    if (this.clearUploadTabSubscription) {
      this.clearUploadTabSubscription.unsubscribe();
    }
  }

}