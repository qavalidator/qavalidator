import {Component, OnInit} from '@angular/core';
import {ResultsService} from "../results/results.service";
import {Image} from "../results/results.types";

@Component({
  selector: 'app-image-list',
  templateUrl: './image-list.component.html',
  styleUrls: ['./image-list.component.css']
})
export class ImageListComponent implements OnInit {

  resultList: Image[];
  errorMessage: string;

  constructor(private resultsService: ResultsService) {
  }

  ngOnInit() {
    this.getImageList();
  }

  getImageList(): void {
    this.resultsService.getImageList()
      .then(result => this.setResult(result))
      .catch(error => this.setError(error));
  }

  getImageUrl(imageId: string): string {
    return this.resultsService.getImageUrl(imageId);
  }

  private setResult(result: Image[]) {
    this.resultList = result;
    this.errorMessage = null;
  }

  private setError(result: string) {
    this.resultList = null;
    this.errorMessage = result;
  }
}
