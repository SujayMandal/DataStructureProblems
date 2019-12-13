import { Component, OnInit, Inject } from '@angular/core';
import * as $ from 'jquery';
import { APP_CONFIG } from "../app-config.constants";
import { IAppConfig } from "../app-config.interface";
import { GridService } from "app/grid.service";
import { Subscription } from "rxjs/Subscription";
import { FilterService } from "app/filter.service";
import { FileSelected } from "app/file-selected";
import { ReportviewService } from 'app/reportview.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  filterBucketSelectedSubscription: Subscription;
  redirectToDashboardSubscription: Subscription;
  filterBucketActive: boolean = false;
  filterBucketClickable: boolean = false;
  fileSelected: FileSelected;
  week0Selected: boolean = false;
  weekNSelected: boolean = false;
  sopWeek0Selected: boolean = false;
  sopWeekNSelected: boolean = false;
  searchSelected: boolean = false;
  reportSelected: boolean = false;
  reportmenu1 : boolean = false;
  reportmenu2 : boolean = false;
  reportmenu3 : boolean = false;
  menu0: boolean = false;
  menu1: boolean = false;
  menu2: boolean = false;
  menu3: boolean = false;
  menu31: boolean = false;
  menu32: boolean = false;
  menu33: boolean = false;
  menu34: boolean = false;
  menu35: boolean = false;
  menu36: boolean = false;
  menu37: boolean = false;
  menu38: boolean = false;
  menu39: boolean = false;
  menu40: boolean = false;
  menu41: boolean = false;
  menu42: boolean = false;
  menu43: boolean = false;
  menu44: boolean = false;
  menu45: boolean = false;
  menu46: boolean = false;
  menu47: boolean = false;
  menu48: boolean = false;
  menu4: boolean = false;
  menu5: boolean = false;
  menu6: boolean = false;
  //map
  bucketElementMap: Map<String, String>;

  constructor( @Inject(APP_CONFIG) private config: IAppConfig, private filterService: FilterService, private gridService: GridService,private reportService: ReportviewService) {
    if(window.location.pathname.toString().includes("week0")){
      this.weekNSelected = false;
      this.week0Selected = true;
      this.searchSelected = false;
      this.reportSelected = false;
      this.menu1 = true;
      if(window.location.pathname.toString().includes("SOP")) {
        this.sopWeek0Selected = true;
      } else {
        this.sopWeek0Selected = false;
      }
      this.gridService.setClearFetchTab(true);
      this.gridService.setClearSearchTab(true);
    } else if(window.location.pathname.toString().includes("weekN")) {
      this.week0Selected = false;
      this.weekNSelected = true;
      this.searchSelected = false;
      this.reportSelected = false;
      this.menu0 = true;
      if(window.location.pathname.toString().includes("SOP")) {
        this.sopWeekNSelected = true;
      } else {
        this.sopWeekNSelected = false;
      }
      this.gridService.setClearUploadTab(true);
      this.gridService.setClearSearchTab(true);
    } else if(window.location.pathname.toString().includes("search")) {
      this.week0Selected = false;
      this.weekNSelected = false;
      this.searchSelected = true;
      this.reportSelected = false;
      this.menu5 = true;
      this.gridService.setClearFetchTab(true);
      this.gridService.setClearUploadTab(true);
    } else if(window.location.pathname.toString().includes("reports")) {
      this.week0Selected = false;
      this.weekNSelected = false;
      this.searchSelected = false;
      this.reportSelected = true;
      this.reportmenu1 = true;
      this.menuBars('reportmenu1');
      this.gridService.setClearFetchTab(true);
      this.gridService.setClearUploadTab(true);
      this.reportService.setClearQaReportTab(true);
      this.gridService.setShowWeekNGridData(true);
      this.gridService.setShowWeek0GridData(false);
      this.gridService.setShowPermReportData(false);
    }

    this.fileSelected = new FileSelected();
    this.fileSelected.Id = '';
    this.fileSelected.filter = '';

    this.bucketElementMap = new Map();
    this.bucketElementMap.set("Client Mismatch", "menu31");
    this.bucketElementMap.set("Dup Asset", "menu32");
    this.bucketElementMap.set("SS", "menu33");
    this.bucketElementMap.set("Unsupp AV", "menu34");
    this.bucketElementMap.set("RR/RTNG Failure", "menu35");
    this.bucketElementMap.set("Unsupp PropType", "menu36");
    this.bucketElementMap.set("Model Fail", "menu37");
    this.bucketElementMap.set("Processed", "menu38");
    this.bucketElementMap.set("Hubzu Failure", "menu39");
    this.bucketElementMap.set("Unsupp State", "menu40");
    this.bucketElementMap.set("SS & PMI", "menu41");
    this.bucketElementMap.set("SOP", "menu42");
    this.bucketElementMap.set("Active Listings", "menu43");
    this.bucketElementMap.set("Assignment", "menu44");
    this.bucketElementMap.set("Odd Listing", "menu45");
    this.bucketElementMap.set("Successful / Underreview", "menu46");
    this.bucketElementMap.set("Vacant", "menu47");
    this.bucketElementMap.set("Past 12 Cycles", "menu48");

    this.filterBucketSelectedSubscription = this.gridService.getFilterBucketSelected().subscribe(fileSelected => {
      if ((fileSelected.Id != '') && (this.fileSelected.Id != fileSelected.Id)) {
        this.filterBucketClickable = true;
        this.fileSelected = fileSelected;
        $('#menu3').css('background-image', 'url(' + this.arrowDown + ')');
        this.filterBucketActive = true;
        this.menuBars(this.bucketElementMap.get(fileSelected.filter));
      } else if ((fileSelected.Id == '')) {
        this.filterBucketClickable = false;
        this.fileSelected = fileSelected;
        $('#menu3').css('background-image', 'url(' + this.arrowLeft + ')');
        this.filterBucketActive = false;
      }
    });

    this.redirectToDashboardSubscription = this.gridService.getRedirectToDashboard().subscribe(flag => {
      if (flag) {
        this.menuHide('menu2');
      }
    });

  }

  baseURL: string = location.href.substr(0, location.href.lastIndexOf('/'));
  arrowLeft = this.baseURL + '/assets/images/add-plus-button.png';
  arrowDown = this.baseURL + '/assets/images/minus.png'

  menuBars(param) {
    this.menu0 = param === 'menu0' ? true : false;
    this.menu1 = param === 'menu1' ? true : false;
    this.menu2 = param === 'menu2' ? true : false;
    this.menu3 = param === 'menu3' ? true : false;
    this.menu31 = param === 'menu31' ? true : false;
    this.menu32 = param === 'menu32' ? true : false;
    this.menu33 = param === 'menu33' ? true : false;
    this.menu34 = param === 'menu34' ? true : false;
    this.menu35 = param === 'menu35' ? true : false;
    this.menu36 = param === 'menu36' ? true : false;
    this.menu37 = param === 'menu37' ? true : false;
    this.menu38 = param === 'menu38' ? true : false;
    this.menu39 = param === 'menu39' ? true : false;
    this.menu40 = param === 'menu40' ? true : false;
    this.menu41 = param === 'menu41' ? true : false;
    this.menu42 = param === 'menu42' ? true : false;
    this.menu43 = param === 'menu43' ? true : false;
    this.menu44 = param === 'menu44' ? true : false;
    this.menu45 = param === 'menu45' ? true : false;
    this.menu46 = param === 'menu46' ? true : false;
    this.menu47 = param === 'menu47' ? true : false;
    this.menu48 = param === 'menu48' ? true : false;
    this.menu4 = param === 'menu4' ? true : false;
    this.menu5 = param === 'menu5' ? true : false;
    this.menu6 = param === 'menu6' ? true : false;
    this.reportmenu1 = param === 'reportmenu1' ? true : false;
    this.reportmenu2 = param === 'reportmenu2' ? true : false;
    this.reportmenu3 = param === 'reportmenu3' ? true : false;
  }

  menuHide(param) {
    if (param == 'menu1') {
      this.menuBars('menu1')
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
    } else if (param == 'menu0') {
      this.menuBars('menu0')
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
    } else if (param == 'menu5') {
      this.menuBars('menu5')
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
      this.gridService.setClearSearchTab(true);
      this.gridService.setShowFutureRecGrid(false);
    } else if (param == 'menu6') {
      this.menuBars('menu6')
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
      this.gridService.setClearSearchTab(true);
      this.gridService.setShowFutureRecGrid(true);
    } else if (param == 'reportmenu1') {
      this.menuBars('reportmenu1');
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
      this.reportService.setClearQaReportTab(true);
      this.gridService.setShowWeekNGridData(true);
      this.gridService.setShowWeek0GridData(false);
     // this.gridService.setShowPermReportData(false);
    }else if (param == 'reportmenu2') {
      this.menuBars('reportmenu2');
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
      this.reportService.setClearQaReportTab(true);
      this.gridService.setShowWeekNGridData(false);
      this.gridService.setShowWeek0GridData(true);
    //  this.gridService.setShowPermReportData(false);
    }else if (param == 'reportmenu3') {
      this.menuBars('reportmenu3');
      this.fileSelected.Id = '';
      this.fileSelected.filter = '';
      this.reportService.setClearQaReportTab(true);
      this.gridService.setShowPermReportData(true);
    }
     else {
      this.gridService.setClearUploadTab(true);
      this.gridService.setClearFetchTab(true);
      if (param == 'menu2') {
        this.menuBars('menu2')
        this.gridService.setLoadDashboardGridFlag(true);
        this.fileSelected.Id = '';
        this.fileSelected.filter = '';
      }
      else if (param == 'menu3') {
        this.menuBars('menu3')
      }
      else if (param == 'menu31') {
        this.menuBars('menu31')
        this.fileSelected.filter = "Client Mismatch";
      }
      else if (param == 'menu32') {
        this.menuBars('menu32')
        this.fileSelected.filter = "Dup Asset";
      }
      else if (param == 'menu33') {
        this.menuBars('menu33')
        this.fileSelected.filter = "SS";
      }
      else if (param == 'menu34') {
        this.menuBars('menu34')
        this.fileSelected.filter = "Unsupp AV";
      }
      else if (param == 'menu35') {
        this.menuBars('menu35')
        this.fileSelected.filter = "RR/RTNG Failure";
      }
      else if (param == 'menu36') {
        this.menuBars('menu36')
        this.fileSelected.filter = "Unsupp PropType";
      }
      else if (param == 'menu37') {
        this.menuBars('menu37')
        this.fileSelected.filter = "Model Fail";
      }
      else if (param == 'menu38') {
        this.menuBars('menu38')
        this.fileSelected.filter = "Processed";
      }
      else if (param == 'menu39') {
        this.menuBars('menu39')
        this.fileSelected.filter = "Hubzu Failure";
      }
      else if (param == 'menu40') {
        this.menuBars('menu40')
        this.fileSelected.filter = "Unsupp State";
      }
      else if (param == 'menu41') {
        this.menuBars('menu41')
        this.fileSelected.filter = "SS & PMI";
      }
      else if (param == 'menu42') {
        this.menuBars('menu42')
        this.fileSelected.filter = "SOP";
      }
      else if (param == 'menu43') {
        this.menuBars('menu43')
        this.fileSelected.filter = "Active Listings";
      }
      else if (param == 'menu44') {
        this.menuBars('menu44')
        this.fileSelected.filter = "Assignment";
      }
      else if (param == 'menu45') {
        this.menuBars('menu45')
        this.fileSelected.filter = "Odd Listing";
      }
      else if (param == 'menu46') {
        this.menuBars('menu46')
        this.fileSelected.filter = "Successful / Underreview";
      }
      else if (param == 'menu47') {
        this.menuBars('menu47')
        this.fileSelected.filter = "Vacant";
      }
      else if (param == 'menu48') {
        this.menuBars('menu48')
        this.fileSelected.filter = "Past 12 Cycles";
      }
      else if (param == 'menu4') {
        if (this.filterBucketClickable) {
          this.menuBars('menu4')
        }
      }
    }
    this.gridService.setFilterBucketSelected(this.fileSelected);
  }

  menuToggle() {
    if (this.filterBucketClickable) {
      if (!this.filterBucketActive) {
        $('#menu3').css('background-image', 'url(' + this.arrowDown + ')');
        this.filterBucketActive = true;
      } else {
        $('#menu3').css('background-image', 'url(' + this.arrowLeft + ')');
        this.filterBucketActive = false;
      }
    }
  }

  activeEle(param) {
    if (param == 'menu0') {
      return this.menu0 ? 'activeLi' : 'inActiveLi';
    }
    if (param == 'menu1') {
      return this.menu1 ? 'activeLi' : 'inActiveLi';
    }
    else if (param == 'menu2') {
      return this.menu2 ? 'activeLi' : 'inActiveLi';
    }
    else if (param == 'reportmenu1'){
      return this.reportmenu1 ?  'activeLi' : 'inActiveLi';
    }
    else if (param == 'reportmenu2'){
      return this.reportmenu2 ?  'activeLi' : 'inActiveLi';
    }
    else if (param == 'reportmenu3'){
      return this.reportmenu3 ?  'activeLi' : 'inActiveLi';
    }
    else if (param == 'menu3') {
      return (this.menu31 || this.menu32 || this.menu33 || this.menu34 || this.menu35 || this.menu36 || this.menu37
       || this.menu38 || this.menu48 || this.menu39 || this.menu40 || this.menu41 || this.menu42 || this.menu43 || this.menu44 || this.menu45 || this.menu46 || this.menu47) ? 'activeLi' : 'inActiveLi';
    }
    else if (param == 'menu31') {
      return this.menu31 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu32') {
      return this.menu32 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu33') {
      return this.menu33 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu34') {
      return this.menu34 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu35') {
      return this.menu35 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu36') {
      return this.menu36 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu37') {
      return this.menu37 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu38') {
      return this.menu38 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu39') {
      return this.menu39 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu40') {
      return this.menu40 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu41') {
      return this.menu41 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu42') {
      return this.menu42 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu43') {
      return this.menu43 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu44') {
      return this.menu44 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu45') {
      return this.menu45 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu46') {
      return this.menu46 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu47') {
      return this.menu47 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu48') {
      return this.menu48 ? 'activeSmLi' : 'inActiveLi';
    }
    else if (param == 'menu4') {
      return this.menu4 ? 'activeLi' : 'inActiveLi';
    }
    else if (param == 'menu5') {
      return this.menu5 ? 'activeLi' : 'inActiveLi';
    }
    else if (param == 'menu6') {
      return this.menu6 ? 'activeLi' : 'inActiveLi';
    }
  }

  ngOnInit(): void {
  }

  ngOnDestroy() {
    if (this.filterBucketSelectedSubscription) {
      this.filterBucketSelectedSubscription.unsubscribe();
    }
    if (this.redirectToDashboardSubscription) {
      this.redirectToDashboardSubscription.unsubscribe();
    }
  }

}
