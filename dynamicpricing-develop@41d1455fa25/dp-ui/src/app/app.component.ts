import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { LoaderMsgService } from "./loader-msg.service";
import { HomeService } from "./home.service";
import { LoaderService } from "./loader.service";

import {Idle, DEFAULT_INTERRUPTSOURCES} from '@ng-idle/core';
import { HeaderService } from "app/header.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  idleState = 'Not started.';
  timedOut = false;

  title = 'Dynamic Pricing';
  showLoader: boolean;
  loadingMessage: string;
  appVersion: string = "4.1";
  constructor(private loaderService: LoaderService, private loaderMsgService: LoaderMsgService, private homeService: HomeService,
    private idle: Idle, private headerService: HeaderService) {
    //   this.homeService.getWebAppVersion().subscribe(
    //   response => {
    //     this.appVersion=response;
    //   },
    //   error => { 
    //   }
    // );
    idle.setIdle(1800);
    idle.setTimeout(10);
    // sets the default interrupts, in this case, things like clicks, scrolls, touches to the document
    idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    idle.onIdleEnd.subscribe(() => this.idleState = 'No longer idle.');
    idle.onTimeout.subscribe(() => {
      this.idleState = 'Timed out!';
      this.timedOut = true;
      this.headerService.logoutUser();
    });
    idle.onIdleStart.subscribe(() => this.idleState = 'You\'ve gone idle!');
    idle.onTimeoutWarning.subscribe((countdown) => this.idleState = 'You will time out in ' + countdown + ' seconds!');

    this.reset();
  }

  reset() {
    this.idle.watch();
    this.idleState = 'Started.';
    this.timedOut = false;
  }

  ngOnInit() {
    this.loaderService.status.subscribe((val: boolean) => {
      this.showLoader = val;
    });

    this.loaderMsgService.message.subscribe((val: string) => {
      this.loadingMessage = val;
      console.log("AppComponent loadingMessage", this.loadingMessage)
    });
  }
  ngOnDestroy() {
    // unsubscribe  
    this.loaderMsgService.message.unsubscribe();
    this.loaderService.status.unsubscribe();
  }
}
