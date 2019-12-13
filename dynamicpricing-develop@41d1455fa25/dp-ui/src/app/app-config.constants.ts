import { InjectionToken } from "@angular/core";
import { IAppConfig } from "./app-config.interface";

export const APP_DI_CONFIG: IAppConfig = {

// for Node
// API_URL: 'http://localhost:9084/dp/'

// for Tomcat
API_URL: ''

};

export let APP_CONFIG = new InjectionToken< IAppConfig >( 'app.config' );
