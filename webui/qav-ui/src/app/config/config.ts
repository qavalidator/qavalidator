import { Injectable } from '@angular/core';

/**
 * Configuration for the application: where to find the backend REST API,
 * maybe more in the future.
 */
@Injectable()
export class Config {

    apiUrl: string;

    constructor() {
        this.apiUrl = process.env.API_URL;
    }

}