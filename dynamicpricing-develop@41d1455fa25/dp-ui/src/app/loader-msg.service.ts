import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";


@Injectable()
export class LoaderMsgService {
    
    public message:Subject<string> = new Subject<string>();
     constructor() {}
}