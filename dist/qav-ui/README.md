# QAV Web UI

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 6.2.2.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).


## Usage

The following command starts the server:

```
  java -jar qav-server-VERSION.jar --graph=path/to/dependencyGraph.json
```

Then, http://localhost:8080/index.html is the entry point to the QAvalidator UI.

## The Build Process

The build supports both a development mode, i.e. being interactive and redeploy the app quickly,
and a CI mode, i.e. package the app into one jar and deliver it directly from the qav-server project.

### Development Mode

Use `npm install` if the file `package.json` changed, or if the directory `node_modules` does
not yet (or not any more) exist.

Use `npm run start` to start a server on http://localhost:3000, and to see redeploys
immediately after a file changed.

### CI Mode

Just use `gradlew [clean] build` to produce the `.jar` file.
It contains a directory `public`, so that the Spring Boot app `qav-server` directly serves the app.

### Cleanup

`../gradlew clean` deletes the `build` directory.

`../gradlew veryClean` does the same, and also deletes the directories
`nodejs`, `node_modules`, and `typings`.


## Open Issues

The QAvalidator UI project is far from complete. E.g. the following issues should be improved:

* error handling is way too simple
* unit tests on TypeScript code are missing
* CSS handling on source level and in the build process should be improved
