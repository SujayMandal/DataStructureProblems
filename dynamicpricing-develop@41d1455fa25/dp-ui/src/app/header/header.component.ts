import { Component, OnInit } from '@angular/core';
import { HeaderService } from "../header.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  username: string = '';
  errorMsg: string = '';
  errorFlag: boolean = false;
  constructor(private headerService: HeaderService) {

    this.headerService.populateUsername().subscribe(
      response => {
        var rstList = response.response;

        if (response.message != null) {
          this.errorMsg = response.message;
        } else {
          this.username = rstList.username;
        }

      },
      error => {
        this.errorFlag = false;
        this.errorMsg = "User is not authenticated";
      }
    );


  }

  onSignOut() {
    this.headerService.logoutUser();
  }

  ngOnInit() {
  }

  reloadPage() {
    window.location.reload();
  }
}
