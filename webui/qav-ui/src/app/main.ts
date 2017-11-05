import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';

import { AppModule } from './app.module';

if (process.env.PROFILE === 'production') {
    enableProdMode();
} else {
    console.log('Profile: ' + process.env.PROFILE);
}

platformBrowserDynamic().bootstrapModule(AppModule)
    .then(success => console.log(`Bootstrap success`))
    .catch(error => console.log(error));