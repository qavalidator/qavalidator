/* global __dirname */

'use strict';

var path = require('path');

var nodeModulesDir = path.join(__dirname, '../node_modules');
var srcDir = path.join(__dirname, '../src');
var buildDir = path.join(__dirname, '../build/public');
var appDir = path.join(srcDir, 'app');
var entryFile = path.join(appDir, 'main.ts');
var index = path.join(srcDir, 'index.html');

// There are two profiles: production and development, default is development
// They can be set with --env <mode>
var args = require('yargs').argv;
var productionFlag = 'production';
var developmentFlag = 'development';
var isProd = (args.env === productionFlag);
var profile = isProd ? productionFlag : developmentFlag;
var environments = require('./environments');

var configuration = {

    paths: {
        nodeModulesDir: nodeModulesDir,
        entryFile: entryFile,
        buildDir: buildDir,
        srcDir: srcDir,
        index: index
    },

    vendorsToBundleSeperately: [
        'zone.js',
        'reflect-metadata'
    ],

    chunkManifest: {
        filename: 'manifest.json',
        globalVariable: 'webpackManifest'
    },

    apiUrl: environments[profile].apiUrl,
    profile: profile
};

module.exports = configuration;
