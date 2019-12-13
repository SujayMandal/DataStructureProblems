import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Observable } from "rxjs/Observable";
import { FileSelected } from "app/file-selected";

@Injectable()
export class GridService {

  loadDashboardGridFlag: boolean = false;
  filterBucketSelected: FileSelected;
  redirectToDashboard: boolean = false;
  clearUploadTab: boolean = false;
  clearFetchTab: boolean = false;
  clearSearchTab: boolean = false;
  showFutureRecGrid: boolean = false;
  fileDetailInfo: any;
  clearSearchFilterTabs: boolean = false;
  showWeek0Grid : boolean = false;
  showWeekNGrid : boolean = false;
  showPermReportData : boolean = false;

  private loadDashboardGridFlagSubject = new Subject<any>();
  private filterBucketSelectedSubject = new Subject<any>();
  private redirectToDashboardSubject = new Subject<any>();
  private clearUploadTabSubject = new Subject<any>();
  private clearFetchTabSubject = new Subject<any>();
  private clearSearchTabSubject = new Subject<any>();
  private fileDetailInfoSubject = new Subject<any>();
  private showFutureRecGridSubject = new Subject<any>();

  private clearSearchFlterTabSubject = new Subject<any>();
  private showWeekNGridSubject = new Subject<any>();
  private showWeek0GridSubject = new Subject<any>();
  private showPermanentGridSubject = new Subject<any>();

  setLoadDashboardGridFlag(loadDashboardGridFlag: boolean) {
    this.loadDashboardGridFlag = loadDashboardGridFlag;
    this.loadDashboardGridFlagSubject.next(loadDashboardGridFlag);
  }
  getLoadDashboardGridFlag(): Observable<any> {
    return this.loadDashboardGridFlagSubject.asObservable();
  }

  setShowFutureRecGrid(showFutureRecGrid: boolean) {
    this.showFutureRecGrid = showFutureRecGrid;
    this.showFutureRecGridSubject.next(showFutureRecGrid);
  }
  setShowWeek0GridData(showWeek0Grid: boolean) {
    this.showWeek0Grid = showWeek0Grid;
    this.showWeek0GridSubject.next(showWeek0Grid);
  }
  setShowWeekNGridData(showWeekNGrid: boolean) {
    this.showWeekNGrid = showWeekNGrid;
    this.showWeekNGridSubject.next(showWeekNGrid);
  }
  setShowPermReportData(showPermReport: boolean) {
    this.showPermReportData = showPermReport;
    this.showPermanentGridSubject.next(showPermReport);
  }
  isPermanentReport(){
    if(this.showPermReportData){
      return true;
    }
    else
    return false;
  }
  getShowFutureRecGrid(): Observable<any> {
    return this.showFutureRecGridSubject.asObservable();
  }
  getShowWeek0DataGrid(): Observable<any> {
    return this.showWeek0GridSubject.asObservable();
  }
  getShowWeekNDataGrid(): Observable<any> {
    return this.showWeekNGridSubject.asObservable();
  }
  getShowPermanentReportTab(): Observable<any> {
    return this.showPermanentGridSubject.asObservable();
  }


  setFilterBucketSelected(filterBucketSelected: FileSelected) {
    this.filterBucketSelected = filterBucketSelected;
    this.filterBucketSelectedSubject.next(filterBucketSelected);
  }
  getFilterBucketSelected(): Observable<any> {
    return this.filterBucketSelectedSubject.asObservable();
  }

  setRedirectToDashboard(redirectToDashboard: boolean) {
    this.redirectToDashboard = redirectToDashboard;
    this.redirectToDashboardSubject.next(redirectToDashboard);
  }
  getRedirectToDashboard(): Observable<any> {
    return this.redirectToDashboardSubject.asObservable();
  }

  setClearUploadTab(clearUploadTab: boolean) {
    this.clearUploadTab = clearUploadTab;
    this.clearUploadTabSubject.next(clearUploadTab);
  }
  getClearUploadTab(): Observable<any> {
    return this.clearUploadTabSubject.asObservable();
  }

  setClearFetchTab(clearFetchTab: boolean) {
    this.clearFetchTab = clearFetchTab;
    this.clearFetchTabSubject.next(clearFetchTab);
  }
  getClearFetchTab(): Observable<any> {
    return this.clearFetchTabSubject.asObservable();
  }

  setClearSearchTab(clearSearchTab: boolean) {
    this.clearSearchTab = clearSearchTab;
    this.clearSearchTabSubject.next(clearSearchTab);
  }
  
  setClearSearchFilterTabs(clearSearchFilterTabs: boolean) {
    this.clearSearchFilterTabs = clearSearchFilterTabs;
    this.clearSearchFlterTabSubject.next(clearSearchFilterTabs);
  }
  getClearSearchTab(): Observable<any> {
    return this.clearSearchTabSubject.asObservable();
  }
  
  setFileDetailInfo(fileDetailInfo: any) {
    this.fileDetailInfo = fileDetailInfo;
    this.fileDetailInfoSubject.next(fileDetailInfo);
  }
  getFileDetailInfo(): Observable<any> {
    return this.fileDetailInfoSubject.asObservable();
  }

}
