import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Observable } from "rxjs/Observable";
import { FileSelected } from "app/file-selected";

import { Component, OnInit } from '@angular/core';
import { Subscription } from "rxjs/Subscription";
import { GridOptions } from "ag-grid";
import { ProcessorService } from "app/processor.service";
import { GridService } from "app/grid.service";
import { DatePipe } from "@angular/common";
import { FetchService } from "app/fetch.service";
import { DownloadService } from "app/download.service";
import { Inject } from '@angular/core';
import { APP_CONFIG } from "./app-config.constants";
import { IAppConfig } from "./app-config.interface";
import { LoaderService } from "./loader.service";
import { Http } from "@angular/http";


@Injectable()
export class ReportviewService {
 

  getFilesSubject: Subject<boolean> = new Subject();
  consolidatedReportsURL = "consolidatedReports?";
  week0ReportsURL ="week0-qa-report?";
  permanentExclusionReport="permanent-exclusion-report?";
  clearQaReportTab: boolean = false;
  
 
  private clearQaReportTabSubject = new Subject<any>();

  constructor(private _http: Http, @Inject( APP_CONFIG ) private config: IAppConfig, 
  private loaderService: LoaderService) {
     this.getFilesSubject.subscribe(loaderStatus => {
      this.loaderService.display(loaderStatus);
     });
   }

 
  setClearQaReportTab(clearQaReportTab: boolean) {
    this.clearQaReportTab = clearQaReportTab;
    this.clearQaReportTabSubject.next(clearQaReportTab);
  }
  getClearQaReportTab(): Observable<any> {
    return this.clearQaReportTabSubject.asObservable();
  }


  public getQaReptDetails(searchFilter: object) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.getFilesSubject.next(true);
    return this._http.get(this.config.API_URL+this.consolidatedReportsURL+"startDate="+searchFilter["fromDate"]+"&endDate="+searchFilter["toDate"]+"&occupancy="+searchFilter["occupancy"]+"&client="+searchFilter["clients"]).map(response => {
      {
        this.getFilesSubject.next(false);
        return response.json(); 
      };
  })
  .catch(error => {
    this.getFilesSubject.next(false);
    throw error.json(); 
  } );
  }

  public getQaWeek0ReptDetails(searchFilter: object) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.getFilesSubject.next(true);
    return this._http.get(this.config.API_URL+this.week0ReportsURL+"startDate="+searchFilter["fromDate"]+"&endDate="+searchFilter["toDate"]+"&occupancy="+searchFilter["occupancy"]+"&client="+searchFilter["clients"]).map(response => {
      {
        this.getFilesSubject.next(false);
        return response.json(); 
      };
  })
  .catch(error => {
    this.getFilesSubject.next(false);
    throw error.json(); 
  } );
  }

  public getPermReptExclusDetails(searchFilter: object) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.getFilesSubject.next(true);
    return this._http.get(this.config.API_URL+this.permanentExclusionReport+"classifications="+searchFilter).map(response => {
      { this.getFilesSubject.next(false);
        return response.json(); 
      };
  })
  .catch(error => {
    this.getFilesSubject.next(false);
    throw error.json(); 
  } );
  }
}
