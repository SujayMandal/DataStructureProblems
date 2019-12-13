import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Http, RequestOptions, Headers } from "@angular/http";
import { APP_CONFIG } from "./app-config.constants";
import { IAppConfig } from "./app-config.interface";
import { LoaderService } from "./loader.service";

@Injectable()
export class ProcessorService {

  processorSubject: Subject<boolean> = new Subject();
  week0FileProcessUrl = "processFile?fileId=";
  weekNFileProcessUrl = "processWeekN?fileId=";
  sopWeek0FileProcessUrl = "processSopWeek0File?fileId=";
  sopWeekNFileProcessUrl = "processSopWeekNFile?fileId=";

  constructor(private _http: Http, @Inject(APP_CONFIG) private config: IAppConfig,
    private loaderService: LoaderService) {
    this.processorSubject.subscribe(loaderStatus => {
      this.loaderService.display(loaderStatus);
    });
  }

  public processFile(fileContent: Object, fileId: String, weekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    var url = this.week0FileProcessUrl;
    if(weekType == "weekN"){
      url = this.weekNFileProcessUrl;
    } else if(weekType == "sopWeek0") {
      url = this.sopWeek0FileProcessUrl;
    } else if(weekType == "sopWeekN") {
      url = this.sopWeekNFileProcessUrl;
    }

    this.processorSubject.next(true);
    return this._http.post(this.config.API_URL + url + fileId, fileContent).map(response => {
      {
        this.processorSubject.next(false);
        return response.json();
      };
    })
      .catch(error => {
        this.processorSubject.next(false);
        throw error.json();
      });
  }

}
