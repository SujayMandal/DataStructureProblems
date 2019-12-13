import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Http } from "@angular/http";
import { APP_CONFIG } from "app/app-config.constants";
import { IAppConfig } from "app/app-config.interface";
import { LoaderService } from "app/loader.service";

@Injectable()
export class FilterService {

  getFileInfoSubject: Subject<boolean> = new Subject();
  getWeek0InfoUrl = "getAssetDetails?fileId=";
  getWeekNInfoUrl = "getWeekNAssetDetails?weekNId=";
  getSopWeek0InfoUrl = "getSopWeek0AssetDetails?fileId=";
  getSopWeekNInfoUrl = "getSopWeekNAssetDetails?fileId=";

  constructor(private _http: Http, @Inject(APP_CONFIG) private config: IAppConfig,
    private loaderService: LoaderService) {
    this.getFileInfoSubject.subscribe(loaderStatus => {
      this.loaderService.display(loaderStatus);
    });
  }

  public getFileInfo(fileId: String, weekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.getFileInfoSubject.next(true);
    if (weekType == "week0") {
      return this._http.get(this.config.API_URL + this.getWeek0InfoUrl + fileId + "&weekType=" + weekType).map(response => {
        {
          this.getFileInfoSubject.next(false);
          return response.json();
        };
      })
        .catch(error => {
          this.getFileInfoSubject.next(false);
          throw error.json();
        });
    } else if (weekType == "weekN") {
      return this._http.get(this.config.API_URL + this.getWeekNInfoUrl + fileId + "&WeekType=" + weekType).map(response => {
        {
          this.getFileInfoSubject.next(false);
          return response.json();
        };
      })
        .catch(error => {
          this.getFileInfoSubject.next(false);
          throw error.json();
        });
    } else if (weekType == "sopWeek0") {
      return this._http.get(this.config.API_URL + this.getSopWeek0InfoUrl + fileId + "&weekType=" + weekType).map(response => {
        {
          this.getFileInfoSubject.next(false);
          return response.json();
        };
      })
        .catch(error => {
          this.getFileInfoSubject.next(false);
          throw error.json();
        });
    } else if (weekType == "sopWeekN") {
      return this._http.get(this.config.API_URL + this.getSopWeekNInfoUrl + fileId + "&weekType=" + weekType).map(response => {
        {
          this.getFileInfoSubject.next(false);
          return response.json();
        };
      })
        .catch(error => {
          this.getFileInfoSubject.next(false);
          throw error.json();
        });
    }
  }

}
