import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { APP_CONFIG } from "./app-config.constants";
import { IAppConfig } from "./app-config.interface";
import { LoaderService } from "./loader.service";
import { Http } from "@angular/http";

@Injectable()
export class UploaderService {

  loaderSubject: Subject<boolean> = new Subject();
  week0FileUploadUrl = "uploadFile";
  sopWeek0FileUploadUrl = "uploadSopFile";
  sopWeekNFileUploadUrl = "uploadSopWeekNFile";
  weekNFileUploadUrl = "uploadWeekNExcel";

  constructor(private _http: Http, @Inject(APP_CONFIG) private config: IAppConfig,
    private loaderService: LoaderService) {
    this.loaderSubject.subscribe(loaderStatus => {
      this.loaderService.display(loaderStatus);
    });
  }

  public uploadFile(formData: any, fileName: string, weekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.loaderSubject.next(true);
    var url = this.week0FileUploadUrl;
    if(weekType == "weekN"){
      url = this.weekNFileUploadUrl;
    } else if(weekType == "sopWeek0"){
      url = this.sopWeek0FileUploadUrl;
    } else if(weekType == "sopWeekN"){
      url = this.sopWeekNFileUploadUrl;
    }
    return this._http.post(this.config.API_URL + url, formData).map(response => {
      {
        this.loaderSubject.next(false);
        return response.json();
      };
    })
      .catch(error => {
        this.loaderSubject.next(false);
        throw error.json();
      });
  }

}
