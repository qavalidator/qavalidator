/**
 * represents a result page for a paged query.
 */
export class ResultPage<T> {

    first: boolean;
    last: boolean;

    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    numberOfElements: number;

    content: T[];
}
