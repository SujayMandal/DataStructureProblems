import { Injectable,Inject } from '@angular/core';
import { Http, Headers, Response } from "@angular/http";
import { APP_CONFIG } from "./app-config.constants";
import { IAppConfig } from "./app-config.interface";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export class HeaderService {

  loggedinUrl = "getLoggedInDetails";
  logoutUrl = "logout";

  constructor(private _http: Http, @Inject( APP_CONFIG ) private config: IAppConfig) { }
  
    logoutUser(){
     window.location.href = this.config.API_URL + this.logoutUrl;
    }

    populateUsername() {
      return this._http.get(this.config.API_URL+this.loggedinUrl).map(response => {
        {
          return response.json();
        };
      }).catch(error => {  
          throw error.json();
      });
    }

}