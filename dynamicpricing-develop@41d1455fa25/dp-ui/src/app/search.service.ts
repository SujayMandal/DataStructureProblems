import { Injectable, Inject } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { APP_CONFIG } from "app/app-config.constants";
import { IAppConfig } from "app/app-config.interface";
import { LoaderService } from "app/loader.service";
import { Http } from "@angular/http";

@Injectable()
export class SearchService {

  loaderSubject: Subject<boolean> = new Subject();
  loanSearchUrl = "searchAssetId?assetNumber=";
  futureLoanSearchUrl = "searchFutureRecommendations?assetNumber=";
  removeLoanUrl = "removeLoanFromDPA?assetNumber="

  constructor(private _http: Http, @Inject(APP_CONFIG) private config: IAppConfig,
    private loaderService: LoaderService) {
    this.loaderSubject.subscribe(loaderStatus => {
      this.loaderService.display(loaderStatus);
    });
  }

  public searchLoanID(loanID: String, occupancy: String, isHistory: boolean) {
    this.loaderSubject.next(true);
    var url = this.futureLoanSearchUrl;
    if (isHistory) {
      var url = this.loanSearchUrl;
    }
    return this._http.get(this.config.API_URL + url + loanID + "&occupancy=" + occupancy).map(response => {
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

  public removeLoan(loanID: String, occupancy: String, reason: String) {
    this.loaderSubject.next(true);
    return this._http.get(this.config.API_URL + this.removeLoanUrl + loanID + "&occupancy=" + occupancy + "&reason=" + reason).map(response => {
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
