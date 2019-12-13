import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Http } from "@angular/http";
import { APP_CONFIG } from "app/app-config.constants";
import { IAppConfig } from "app/app-config.interface";
import { LoaderService } from "app/loader.service";

@Injectable()
export class FetchService {

  loaderSubject: Subject<boolean> = new Subject();
  weekNDataFetchUrl = "fetchWeekNData?selectedDateMillis=";
  sopWeekNDataFetchUrl = "fetchSopWeekNHubzu?selectedDateMillis=";

  constructor(private _http: Http, @Inject( APP_CONFIG ) private config: IAppConfig, 
    private loaderService: LoaderService) {
      this.loaderSubject.subscribe(loaderStatus => {
        this.loaderService.display(loaderStatus);
       });
  }

  public fetchData(selectedDate: Number, weekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    var url = this.weekNDataFetchUrl;
    if(weekType.includes("SOP")){
      url = this.sopWeekNDataFetchUrl;
    }
    this.loaderSubject.next(true);
    return this._http.get(this.config.API_URL + url + selectedDate).map(response => {
      {
        this.loaderSubject.next(false);
        return response.json(); 
      };
  })
  .catch(error => {
    this.loaderSubject.next(false);
    throw error.json(); 
  } );
  }

}
