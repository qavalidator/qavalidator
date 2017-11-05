var config = require('./configuration');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');

var webpackOptions = {

    entry: [
        'webpack/hot/dev-server',
        config.paths.entryFile
    ],

    output: {
        filename: 'bundle.js'
    },

    resolve: {
        extensions: ['', '.ts', '.webpack.js', '.web.js', '.js']
    },

    module: {
        preLoaders: [{
            test: /\.ts$/,
            exclude: /node_modules/,
            loader: 'tslint'
        }],

        loaders: [{
            test: /\.ts$/,
            exclude: /node_modules/,
            loader: 'ts-loader'
        }]
    },

    plugins: [
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: config.paths.index,
            inject: true,
            devServer: 'http://localhost:3000/webpack-dev-server.js'
        }),

        new webpack.DefinePlugin({
            'process.env': {
                'API_URL': JSON.stringify(config.apiUrl),
                'PROFILE': JSON.stringify(config.profile)
            }
        })
    ]
};

module.exports = webpackOptions;
