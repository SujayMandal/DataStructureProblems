import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Http, ResponseContentType } from "@angular/http";
import { IAppConfig } from "app/app-config.interface";
import { APP_CONFIG } from "app/app-config.constants";
import { LoaderService } from "app/loader.service";
import * as FileSaver from 'file-saver';

@Injectable()
export class DownloadService {

  downloadSubject: Subject<boolean> = new Subject();
  week0FileDownloadUrl = "downloadReport?fileId=";
  weekNFileDownloadUrl = "getWeekNDownload?Id=";
  weekNStep1DownloadUrl = "downloadWeekNData?userSelectedDate=";
  sopWeek0FileDownloadUrl = "downloadSOPWeek0Report?fileId=";
  sopWeekNFileDownloadUrl = "getSopWeekNZipDownload?id=";
  sopWeekNStep1DownloadUrl = "sopWeekNDownloadFromHubzu?userSelectedDate=";

  constructor(private _http: Http, @Inject(APP_CONFIG) private config: IAppConfig,
    private loaderService: LoaderService) {
    this.downloadSubject.subscribe(loaderStatus => {
      this.loaderService.display(loaderStatus);
    });
  }

  public downloadFile(fileId: String, weekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.downloadSubject.next(true);
    var url = this.config.API_URL + this.week0FileDownloadUrl + fileId + "&type=" + weekType;
    if (weekType == "weekN") {
      url = this.config.API_URL + this.weekNFileDownloadUrl + fileId + "&type=" + weekType;
    } else if (weekType == "sopWeek0") {
      url = this.config.API_URL + this.sopWeek0FileDownloadUrl + fileId + "&type=" + weekType;
    } else if (weekType == "sopWeekN") {
      url = this.config.API_URL + this.sopWeekNFileDownloadUrl + fileId + "&type=" + weekType;
    }
    return this._http.get(url, {
      responseType: ResponseContentType.Blob
    }).map(response => {
      {
        const fileBlob = response.blob();
        const blob = new Blob([fileBlob], {
          type: 'application/zip'
        });
        FileSaver.saveAs(blob, this.getFileNameFromHeader(response.headers.get("Content-Disposition")));
        this.downloadSubject.next(false);
        return;
      };
    })
      .catch(error => {
        this.downloadSubject.next(false);
        throw error.json();
      })
      .subscribe(response => {
        console.log('Download completed.');
      }, error => {
        console.log('Download failed.');
      });
  }

  public downloadWeekNStep1(paramEntryInfo: Object, selectedDate: Number, weeekType: String) {
    console.log("This is the App's Key: ", this.config.API_URL);
    this.downloadSubject.next(true);
    var url = this.weekNStep1DownloadUrl;
    if(weeekType.includes("SOP")){
      url = this.sopWeekNStep1DownloadUrl;
    }
    return this._http.post(this.config.API_URL + url + selectedDate, paramEntryInfo, {
      responseType: ResponseContentType.Blob
    }).map(response => {
      {
        const fileBlob = response.blob();
        const blob = new Blob([fileBlob], {
          type: 'application/vnd.ms-excel'
        });
        FileSaver.saveAs(blob, this.getFileNameFromHeader(response.headers.get("Content-Disposition")));
        this.downloadSubject.next(false);
        return;
      };
    })
      .catch(error => {
        this.downloadSubject.next(false);
        throw error.json();
      })
      .subscribe(response => {
        console.log('Download completed.');
      }, error => {
        console.log('Download failed.');
      });
  }

  getFileNameFromHeader(header) {
    if (!header)
      return null;
    var result = header.split(";")[1].trim().split("=")[1];
    return result.replace(/"/g, '');
  }

}
