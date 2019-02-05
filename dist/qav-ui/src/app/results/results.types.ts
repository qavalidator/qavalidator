/**
 * represents the overview over the whole analysis.
 */
export class AnalysisResult {

  analysisId: string;
  analysisStart: Date;

  baseDir: string;
  items: Result[];

}

/**
 * Part of the overview.
 */
export class Result {
  resultType: string;
  filename: string;

  noNode: number;
  noEdges: number;
}

/**
 * Represents an image which can be viewed.
 */
export class Image {

  imageName: string;

  filenamePNG: string;
  filenameSVG: string;
  filenameGraphml: string;

  noNodes: number;
  noEdges: number;
}
