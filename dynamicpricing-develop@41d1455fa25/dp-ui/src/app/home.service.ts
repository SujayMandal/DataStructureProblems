import { Injectable, OnInit, Inject } from '@angular/core';
import { Http, Headers, Response } from "@angular/http";
import { APP_CONFIG } from "./app-config.constants";
import { IAppConfig } from "./app-config.interface";

@Injectable()
export class HomeService { 
  webAppVersion="getWebAppVersion"
  constructor(private _http: Http, @Inject( APP_CONFIG ) private config: IAppConfig) {
   console.log("This is the App's Key: ", this.config.API_URL);
  } 
  getWebAppVersion() {
    return this._http.get(this.webAppVersion).map(response => {
      {
        return response.json()["response"]["version"];
      };
    })
      .catch(error => {
        throw error.json();
      });
  }
}
