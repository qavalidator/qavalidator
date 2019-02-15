import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AnalysisResult, Image} from "./results.types";

@Injectable({
  providedIn: 'root'
})
export class ResultsService {

  apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }

  getOverview(): Promise<AnalysisResult> {
    return this.http.get<AnalysisResult>(`${this.apiUrl}/analysis`)
      .toPromise()
      .then(response => response)
      .catch(this.handleError);
  }

  getImageList(): Promise<Image[]> {
    return this.http.get<Image[]>(`${this.apiUrl}/analysis/images`)
      .toPromise()
      .then(response => response)
      .catch(this.handleError);
  }

  getImageUrl(imageId: string): string {
    return `${this.apiUrl}/analysis/images/${imageId}`
  }

  /**
   * Get the SVG image as HTML string.
   *
   * @param imageId id of the image to load
   */
  getImageSvg(imageId: string): Promise<string> {
    const options = {responseType: 'text' as 'text'};
    return this.http.get(this.getImageUrl(imageId), options)
      .toPromise()
      .then(response => response)
      .catch(this.handleError);
}

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }
}
