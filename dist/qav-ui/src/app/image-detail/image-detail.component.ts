import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
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

  isSvg: boolean;
  rawSvgAsHtml: SafeHtml;
  private rawSvgOriginal: SafeHtml;
  private rawSvgWithoutWidth: SafeHtml;

  /**
   * true: scaled down to fit the frame;
   * false: not scaled, but just as large as it needs to be. Requires scrolling for larger images.
   */
  showScaled: boolean = true;

  constructor(private resultsService: ResultsService,
              private route: ActivatedRoute,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.route.params.forEach((params: Params) => {
      this.imageId = params['imageId'];
      this.imageUrl = this.resultsService.getImageUrl(this.imageId);

      this.isSvg = this.imageId.endsWith(".svg");
      if (this.isSvg) {
        this.resultsService.getImageSvg(this.imageId)
          .then(result => {
            this.prepareSvgHtmlStrings(result);
            this.resetSvg();
          });
      }
    });
  }

  /**
   * This is quite a hack: If the SVG element does not include the width and height attributes, then it scales to the
   * size of the parent div element (which is defined by the Bootstrap container).
   *
   * (Fiddling with Angular Renderer2 also does not work here, because it would manipulate DOM elements which don't come
   * via the templates, i.e. Angular does not know about them and therefore does not propagates changes to the real DOM.
   * Therefore, we do direct manipulation of the input before putting the HTML into the DOM via the div/innerHTML
   * construction.)
   *
   * @param rawSvgText the SVG as text, as downloaded from the server.
   */
  private prepareSvgHtmlStrings(rawSvgText) {
    this.rawSvgOriginal = this.sanitizer.bypassSecurityTrustHtml(rawSvgText);
    this.rawSvgWithoutWidth = this.sanitizer.bypassSecurityTrustHtml(rawSvgText.replace(/width=".+"/, "").replace(/height=".+"/, ""));
  }

  toggleScaled() {
    this.showScaled = !this.showScaled;
    this.resetSvg();
  }

  /**
   * Choose which SVG snippet to show, depending on the "showScaled" flag.
   */
  private resetSvg() {
    this.rawSvgAsHtml = this.showScaled ? this.rawSvgWithoutWidth : this.rawSvgOriginal;
  }
}
