import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {ResultsService} from "../results/results.service";

@Component({
  selector: 'app-image-detail',
  templateUrl: './image-detail.component.html',
  styleUrls: ['./image-detail.component.css']
})
export class ImageDetailComponent implements OnInit {

  @Input()
  imageId: string;

  imageUrl: string;

  constructor(private resultsService: ResultsService,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.forEach((params: Params) => {
      this.imageId = params['imageId'];
      this.imageUrl = this.resultsService.getImageUrl(this.imageId);
    });
  }
}
