var webpack = require('webpack');
var path = require('path');
var fs = require('fs');
var config = require('./configuration');

var HtmlWebpackPlugin = require('html-webpack-plugin');
var ChunkManifestPlugin = require('chunk-manifest-webpack-plugin');
var Clean = require('clean-webpack-plugin');
var WebpackMd5Hash = require('webpack-md5-hash');
var CopyWebpackPlugin = require('copy-webpack-plugin');

var webpackOptions = {

    entry: {
        app: config.paths.entryFile,
        vendor: config.vendorsToBundleSeperately
    },

    output: {
        filename: 'assets/js/[name]-[chunkhash].js',
        path: config.paths.buildDir
    },

    resolve: {
        extensions: ['', '.ts', '.webpack.js', '.web.js', '.js']
    },

    module: {
        preLoaders: [{
            test: /\.ts$/,
            exclude: /node_modules/,
            loader: 'tslint?failOnHint=true'
        }],

        loaders: [{
            test: /\.ts$/,
            exclude: /node_modules/,
            loader: 'ts-loader'
        }]
    },

    plugins: [
        new Clean(config.paths.buildDir, '.'),

        new WebpackMd5Hash(),

        new webpack.optimize.DedupePlugin(),

        new webpack.optimize.OccurenceOrderPlugin(),

        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor',
            minChunks: Infinity
        }),

        new ChunkManifestPlugin({
            filename: config.chunkManifest.filename,
            manifestVariable: config.chunkManifest.globalVariable
        }),

        new webpack.optimize.UglifyJsPlugin({
            compress: {
                warnings: false
            },
            output: {
                comments: false
            }
        }),

        new HtmlWebpackPlugin({
            inject: true,
            templateContent: function (templateParams, compilation, callback) {
                var chunkManifest = compilation.assets[config.chunkManifest.filename];
                if (chunkManifest) {
                    var chunkManifestJson = compilation.assets[config.chunkManifest.filename].source();
                    templateParams.htmlWebpackPlugin.options.chunkManifest = 'window.' + config.chunkManifest.globalVariable + '=' + chunkManifestJson + ';';
                }

                fs.readFile(config.paths.index, 'utf8', callback);
            }
        }),

        new CopyWebpackPlugin([
            {
                context: config.paths.srcDir,
                from: '**/*.ico',
                to: config.paths.buildDir
            },
            {
                context: config.paths.srcDir,
                from: '**/*.css',
                to: config.paths.buildDir
            },
            {
                context: config.paths.srcDir,
                from: '**/*.html',
                to: config.paths.buildDir
            }
        ]),

        new webpack.DefinePlugin({
            'process.env': {
                'API_URL': JSON.stringify(config.apiUrl),
                'PROFILE': JSON.stringify(config.profile)
            }
        })
    ]
};

module.exports = webpackOptions;