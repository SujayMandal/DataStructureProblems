import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AgGridModule } from 'ag-grid-angular';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { NgIdleModule } from '@ng-idle/core';

import { AppComponent } from './app.component';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './header/header.component';
import { HeaderService } from "./header.service";
import { APP_CONFIG, APP_DI_CONFIG } from "./app-config.constants";
import { LoaderMsgService } from "./loader-msg.service";
import { LoaderService } from "./loader.service";
import { HomeService } from "./home.service";
import { MenuComponent } from './menu/menu.component';
import { UploadBlockComponent } from './upload-block/upload-block.component';
import { UploaderService } from "./uploader.service";
import { ProcessorService } from "./processor.service";
import { DashboardBlockComponent } from './dashboard-block/dashboard-block.component';
import { DashboardService } from "app/dashboard.service";
import { GridService } from "app/grid.service";
import { DatePipe } from "@angular/common";
import { FilterCountComponent } from './filter-count/filter-count.component';
import { FilterGridComponent } from './filter-grid/filter-grid.component';
import { FilterService } from "app/filter.service";
import { NumericEditorComponent } from './numeric-editor/numeric-editor.component';
import { OutputGridComponent } from './output-grid/output-grid.component';
import { DownloadService } from "app/download.service";
import { FetchBlockComponent } from './fetch-block/fetch-block.component';
import { FetchService } from "app/fetch.service";
import { SearchComponent } from './search/search.component';
import { SearchService } from "app/search.service";
import { ReportBlockComponent } from './report-block/report-block.component';
import { ReportviewService } from "app/reportview.service";
import { PermanentReportComponent } from './permanent-report/permanent-report.component';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    MenuComponent,
    UploadBlockComponent,
    DashboardBlockComponent,
    FilterCountComponent,
    FilterGridComponent,
    NumericEditorComponent,
    OutputGridComponent,
    FetchBlockComponent,
    SearchComponent,
    ReportBlockComponent,
    PermanentReportComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AgGridModule.withComponents([FilterCountComponent, NumericEditorComponent]),
    AppRoutingModule,
    NgMultiSelectDropDownModule.forRoot(),
    BsDropdownModule.forRoot(),
    NgIdleModule.forRoot(),
    ModalModule.forRoot()
  ],
  providers: [HeaderService,LoaderService,LoaderMsgService,HomeService,UploaderService,ProcessorService,DashboardService,
             { provide: APP_CONFIG, useValue: APP_DI_CONFIG},GridService,DatePipe,FilterService,DownloadService,FetchService,
             SearchService,BsModalService,ReportviewService],
  bootstrap: [AppComponent]
})
export class AppModule { }
