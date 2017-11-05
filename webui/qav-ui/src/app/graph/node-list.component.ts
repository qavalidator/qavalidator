import {Component}    from '@angular/core';
import {ActivatedRoute, Router, Params} from '@angular/router';
import {GraphService} from './graph.service';
import {Node}       from './graph.types';
import {ResultPage} from '../util/resultpage';

/**
 * Shows a list of nodes.
 *
 * The query field allows to narrow down on the shown nodes.
 */
@Component({
    selector: 'qav-node-list',
    template: `
        <h2>Nodes</h2>
        
        <form>
            <div class="input-group">
                <input type="text" class="form-control" id="name" name="query" 
                       placeholder="Query" aria-describedby="basic-addon2"
                       (keyup.enter)="onSubmitQuery()"
                       [(ngModel)]="query">
                <span class="input-group-addon" id="basic-addon2" (click)="onSubmitQuery()">Search</span>
            </div>
        </form>

        <div *ngIf="errorMessage" class="alert alert-danger" role="alert">
            {{errorMessage}}
        </div>
        
        <div *ngIf="resultPage">
            <ul class="nodes">
                <li *ngFor="let node of resultPage.content">
                    <a [routerLink]="['/node', node.name]">{{node.name}}</a>
                </li>
            </ul>
            
            <nav aria-label="Nodes">
                <ul class="pagination">
                    <li class="page-item">
                        <span class="page-link" aria-label="First" (click)="onFirst()">
                            <span aria-hidden="true">First</span>
                            <span class="sr-only">First</span>
                        </span>
                    </li>                
                    <li class="page-item">
                        <span class="page-link" aria-label="Previous" (click)="onBack()">
                            <span aria-hidden="true">&laquo;</span>
                            <span class="sr-only">Previous</span>
                        </span>
                    </li>
                    <li *ngFor="let page of pageRange()" 
                        class="page-item" 
                        [ngClass]="{active: page == resultPage.number + 1}"
                        (click)="onSelectPage(page)">
                            <span class="page-link">{{page}}</span>
                    </li>
                    <li class="page-item">
                        <span class="page-link" aria-label="Next" (click)="onNext()">
                            <span aria-hidden="true">&raquo;</span>
                            <span class="sr-only">Next</span>
                        </span>
                    </li>
                    <li class="page-item">
                        <span class="page-link" aria-label="Last" (click)="onLast()">
                            <span aria-hidden="true">Last ({{resultPage.totalPages}})</span>
                            <span class="sr-only">Last</span>
                        </span>
                    </li>                     
                </ul>
            </nav>
        </div>
        `,
    providers: [GraphService]
})
export class NodeListComponent {

    private resultPage: ResultPage<Node>;
    private errorMessage: string;

    private query: string = '';

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
        this.router.navigate(['nodes'], { queryParams: this.query !== '' ? {q: this.query} : null });
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
     * @returns {Array}
     */
    pageRange(): Array<number> {
        let a = [];
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
