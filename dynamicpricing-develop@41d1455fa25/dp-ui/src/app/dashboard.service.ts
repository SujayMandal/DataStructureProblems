import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { APP_CONFIG } from "./app-config.constants";
import { IAppConfig } from "./app-config.interface";
import { LoaderService } from "./loader.service";
import { Http } from "@angular/http";

@Injectable()
export class DashboardService {

  getFilesSubject: Subject<boolean> = new Subject();
  getFilesUrl = "getDashboardDetails?weekType=";
  getFilteredFilesUrl = "getFilteredDashboardDetails";

  constructor(private _http: Http, @Inject( APP_CONFIG ) private config: IAppConfig, 
    private loaderService: LoaderService) {
       this.getFilesSubject.subscribe(loaderStatus => {
        this.loaderService.display(loaderStatus);
       });
     }

  public getAllFile(weekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.getFilesSubject.next(true);
    var url = this.getFilesUrl;
    return this._http.get(this.config.API_URL+url+weekType).map(response => {
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

public getFilteredDetails(searchFilter: object) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.getFilesSubject.next(true);
    return this._http.post(this.config.API_URL+this.getFilteredFilesUrl, searchFilter).map(response => {
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

}
