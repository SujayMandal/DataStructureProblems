import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { MenuComponent } from "./menu/menu.component";
import { SearchComponent } from "app/search/search.component";

export const MAINMENU_ROUTES: Routes = [
    //full : makes sure the path is absolute path
      { path: '', redirectTo: 'search', pathMatch: 'prefix' },
      { path: 'reports', component: MenuComponent },
      { path: 'search', component: MenuComponent },
      { path: 'week0', component: MenuComponent },
      { path: 'weekN', component: MenuComponent },
      { path: 'SOPweek0', component: MenuComponent },
      { path: 'SOPweekN', component: MenuComponent }
];
export const appRoutingProviders: any[] = [

];
export const CONST_ROUTING = RouterModule.forRoot(MAINMENU_ROUTES);

  @NgModule({
  imports: [RouterModule.forRoot(MAINMENU_ROUTES)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}