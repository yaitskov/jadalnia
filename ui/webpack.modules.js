const path = require('path');

const cssLoaders = [
  /* try mini-css-extract-plugin instead of style-loader for prod  */
  {
    loader: 'style-loader',
    options: {
      hmr: false
    }
  },
  {
    loader: 'css-modules-typescript-loader',
    options: {
      mode: 'emit' // 'verify' | 'emit'
    }
  },
  {
    loader: 'css-loader',
    options: {
      camelCase: true,
      modules: true,
      localIdentName: '[local]',
      importLoaders: 1
    }
  }
];

function makeModule(extraBabelPlugins) {
  return {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: [['@babel/typescript', { jsxPragma: "h" }]],
            plugins: [
              ["@babel/plugin-proposal-function-bind"],
              ['@babel/proposal-class-properties',
               {
                 'loose': false
               }
              ],
              ['@babel/plugin-proposal-throw-expressions'],
              ['@babel/proposal-object-rest-spread'],
              ['@babel/plugin-syntax-dynamic-import'],
              ["@babel/transform-react-jsx", { "pragma": "h" }],
              ...extraBabelPlugins
            ]
          }
        }
      },
      {
        test: /\.css$/i,
        use: cssLoaders
      },
      {
        test: /\.s[ac]ss$/,
        use: [...cssLoaders, 'sass-loader']
      },
      {
        test: /\.(png|svg)$/i,
        use: {
          loader: 'file-loader',
          options: {}
        }
      },
      {
        test: /\.svgc$/,  // svg preact component
        use: [
          'desvg-loader/preact',
          'svg-loader'
        ]
      }
    ]
  };
}

module.exports = makeModule;
