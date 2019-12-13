import { Component, OnInit, Input } from '@angular/core';
import { GridService } from "app/grid.service";
import { FileSelected } from "app/file-selected";

@Component({
  selector: 'app-filter-count',
  templateUrl: './filter-count.component.html',
  styleUrls: ['./filter-count.component.scss']
})
export class FilterCountComponent implements OnInit {

  params: any;
  fileSelected: FileSelected

  constructor(private gridService: GridService) { }

  agInit(params: any): void {
        this.params = params;
    }

    loadFileInfo(){
      this.fileSelected = new FileSelected();
      this.fileSelected.Id = this.params.data.id;
      this.fileSelected.filter = this.params.colDef.headerName;
      this.gridService.setFilterBucketSelected(this.fileSelected);
    }

    refresh(): boolean {
        return false;
    }

  ngOnInit() {
  }

}
