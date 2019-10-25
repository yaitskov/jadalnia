const fPath = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MergeAndFlushI18nPlugin = require('./merge-and-flush-i18n');
const BrotliPlugin = require('brotli-webpack-plugin');
const CompressionPlugin = require('compression-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');


const makeWebPackModule = require('./webpack.modules.js');
const translationPlugin = require('./translation-plugin.js');

const state = new translationPlugin.newState();
const visitorFactory = translationPlugin.makeVisitorFactory(
  state,
  {
    rootPrefixLength: `${fPath.dirname(__filename)}${fPath.sep}src${fPath.sep}`.length
  });

const compressedFilesPattern = /\.(js|css|html|svg)$/;

module.exports = {
  entry: {
    main: "./src/bootstrap.tsx",
    sw: "./src/worker/worker-101.tsx"
  },
  output: {
    filename: "./[name].bundle.js",
    publicPath: "/"
  },
  mode: 'development',
  // Enable sourcemaps for debugging webpack's output.
  // devtool: "source-map",
  resolve: {
    // Add '.ts' and '.tsx' as resolvable extensions.
    extensions: ['.webpack.js', '.web.js', '.ts', '.tsx', '.js',
                 '.css', '.sass',
                 '.png', '.svg'],
    modules: ['src', 'node_modules']
  },
  module: makeWebPackModule([ visitorFactory ]),
  plugins: [
    new HtmlWebpackPlugin({
      title: 'FoodFest',
      favicon: 'favicon.png',
      chunks: ['main'],
      meta: {viewport: 'width=device-width, initial-scale=1, shrink-to-fit=no'}
    }),
    new MergeAndFlushI18nPlugin(state),
    new CompressionPlugin({
      filename: '[path].gz[query]',
      algorithm: 'gzip',
      test: compressedFilesPattern,
      threshold: 1024,
      minRatio: 0.8
    }),
    new BrotliPlugin({
      asset: '[path].br[query]',
      test: compressedFilesPattern,
      threshold: 1024,
      minRatio: 0.8
    }),
    new ForkTsCheckerWebpackPlugin()
  ]
};
