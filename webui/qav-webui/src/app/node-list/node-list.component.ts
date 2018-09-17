import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';

import {ResultPage} from '../util/resultpage';
import {GraphService} from '../graph/graph.service';
import {Node} from '../graph/graph.types';

/**
 * Shows a list of nodes.
 *
 * The query field allows to narrow down on the shown nodes.
 */
@Component({
  selector: 'app-node-list',
  templateUrl: './node-list.component.html'
})
export class NodeListComponent implements OnInit {

  resultPage: ResultPage<Node>;
  errorMessage: string;

  query = '';

  constructor(private graphService: GraphService,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params: Params) => {
      this.query = params['q'] || '';
    });

    this.getNodeList(0, 30);
  }

  getNodeList(page: number, size: number): void {
    this.graphService.getNodes(this.query, page, size)
      .then(result => this.setResult(result))
      .catch(error => this.setError(error));
  }

  onSubmitQuery(): void {
    this.router.navigate(['nodes'], {queryParams: this.query !== '' ? {q: this.query} : null});
    this.getNodeList(0, 30);
  }

  onBack(): void {
    if (!this.resultPage.first) {
      this.getNodeList(this.resultPage.number - 1, this.resultPage.size);
    }
  }

  onNext(): void {
    if (!this.resultPage.last) {
      this.getNodeList(this.resultPage.number + 1, this.resultPage.size);
    }
  }

  onFirst(): void {
    this.getNodeList(0, this.resultPage.size);
  }

  onLast(): void {
    this.getNodeList(this.resultPage.totalPages - 1, this.resultPage.size);
  }

  onSelectPage(pageNo: number): void {
    this.getNodeList(pageNo - 1, this.resultPage.size);
  }

  /**
   * Decide which pages to show in the pagination.
   *
   * @returns array of numbers
   */
  pageRange(): Array<number> {
    const a = [];
    let start = 0;
    let end = this.resultPage.totalPages;
    const maxPages = 10;

    if (this.resultPage.totalPages > maxPages) {
      start = this.resultPage.number > (maxPages / 2) ? this.resultPage.number - (maxPages / 2) : 0;
      end = start + maxPages;
      if (end > this.resultPage.totalPages) {
        start -= (end - this.resultPage.totalPages);
        end = this.resultPage.totalPages;
      }
    }

    for (let i = start; i < end; ++i) {
      a.push(i + 1);
    }

    return a;
  }

  private setResult(result: ResultPage<Node>) {
    this.resultPage = result;
    this.errorMessage = null;
  }

  private setError(result: string) {
    this.resultPage = null;
    this.errorMessage = result;
  }


}
