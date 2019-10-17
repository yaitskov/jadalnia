const makeWebPackModule = require('./webpack.modules.js');

function getSpecs(specList) {
  if (specList) {
    return specList.split(',').map(file => `src/**/${file}.spec.tsx`);
  }
  return ["src/**/*.spec.tsx"];
}

module.exports = function(config) {
  config.set({
    browsers: ['ChromeHeadless'],
    frameworks: ['jasmine'],
    files: getSpecs(process.env.KARMA_SPECS),
    plugins : ['karma-webpack', 'karma-jasmine', 'karma-chrome-launcher'],
    preprocessors: {
      "src/**/*.spec.tsx": ['webpack']
    },
    webpack: {
      mode: 'development',
      resolve: {
        // Add '.ts' and '.tsx' as resolvable extensions.
        extensions: [".tsx", ".js"],
        modules: ['src', 'node_modules']
      },
      module: makeWebPackModule([])
    }
  });
};
