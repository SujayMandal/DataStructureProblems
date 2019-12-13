import { Component, OnInit, AfterViewInit, ViewChild, ViewContainerRef } from '@angular/core';
import { ICellEditorAngularComp } from "ag-grid-angular";

@Component({
  selector: 'app-numeric-editor',
  templateUrl: './numeric-editor.component.html',
  styleUrls: ['./numeric-editor.component.scss']
})
export class NumericEditorComponent implements ICellEditorAngularComp, AfterViewInit {

  params: any;
  value: number;
  cancelBeforeStart: boolean = false;

  @ViewChild('input', { read: ViewContainerRef }) public input;


  agInit(params: any): void {
    this.params = params;
    this.value = this.params.value;
    this.cancelBeforeStart = params.charPress && ('1234567890'.indexOf(params.charPress) < 0);
  }

  getValue(): any {
    return this.value;
  }

  isCancelBeforeStart(): boolean {
    return this.cancelBeforeStart;
  }

  isCancelAfterEnd(): boolean {
    return false;
  };

  onKeyDown(event): void {
    if (!this.isKeyPressedNumeric(event)) {
      if (event.preventDefault) event.preventDefault();
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.input.element.nativeElement.focus();
    })
  }

  private getCharCodeFromEvent(event): any {
    event = event || window.event;
    return (typeof event.which == "undefined") ? event.keyCode : event.which;
  }

  private isCharNumeric(charStr): boolean {
    return !!/\d/.test(charStr);
  }

  private isKeyPressedNumeric(event): boolean {
    const charCode = this.getCharCodeFromEvent(event);
    const charStr = event.key ? event.key : String.fromCharCode(charCode);
    if(charStr == "Backspace"){
      return true;
    } else {
      return this.isCharNumeric(charStr);
    }
  }

}
