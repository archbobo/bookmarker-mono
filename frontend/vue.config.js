// vue.config.js
module.exports = {
    // proxy all webpack dev-server requests starting with /api
    // to our Spring Boot backend (localhost:8088) using http-proxy-middleware
    // see https://cli.vuejs.org/config/#devserver-proxy
    devServer: {
        port: 3000,
        proxy: {
            '/api/*': {
                target: 'http://localhost:8080/api'
            }
        }
    },
    /*
    configureWebpack: {
      entry: {
        app: './src/main.js',
        style: [
          'bootstrap/dist/css/bootstrap.min.css'
        ]
      }
    },
    */
    // Change build paths to make them Maven compatible
    // see https://cli.vuejs.org/config/
    outputDir: 'dist',
    assetsDir: 'static'
}
